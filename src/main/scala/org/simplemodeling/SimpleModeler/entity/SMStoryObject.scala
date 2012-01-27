package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.{Buffer, ArrayBuffer, HashMap}
import java.util.UUID
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.util._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._
import org.goldenport.value.{GTree, GTreeNode, GTreeVisitor, PlainTree, GTreeCursor}
import org.goldenport.value.util.TreeNodeCounter
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import com.asamioffice.goldenport.text.UPathString
import org.simplemodeling.SimpleModeler.entity.util.StepFlowBuilder

/*
 * @since   Dec.  7, 2008
 * @version Nov.  4, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class SMStoryObject(val dslStoryObject: SStoryObject) extends SMObject(dslStoryObject) {
  private val _basic_flow = new PlainTree[SMStep] // ArrayBuffer[SMTaskStep]
  private val _extension_flow = new ArrayBuffer[SMExtensionSegment]
  private val _alternate_flow = new PlainTree[SMAlternatePath]
  private val _exception_flow = new PlainTree[SMExceptionPath]
  private val _stepByMark = new HashMap[String, SMStep]
  val entityUsage = new SMEntityUsage

  final def basicFlow: Seq[SMTaskStep] = { // XXX TaskStep?
    _basic_flow.root.children.map(_.asInstanceOf[SMTaskStep])
  }
  final def extensionFlow: Seq[SMExtensionSegment] = _extension_flow
  final def alternateFlow: GTree[SMAlternatePath] = _alternate_flow
  final def exceptionFlow: GTree[SMExceptionPath] = _exception_flow

  build_basicFlow
  build_extensionFlow
  build_alternateFlow
  build_exceptionFlow

  private final def make_stepFlowBuilder(root: GTreeNode[SMStep], dslStoryObject: SStoryObject): StepFlowBuilder = {
    new_StepFlowBuilder(root, dslStoryObject, register_mark _, addUse _)
  }

  def new_StepFlowBuilder(root: GTreeNode[SMStep], 
			  dslStoryObject: SStoryObject,
			  registerMark: SMStep => Unit,
			  addUse: SMUse => Unit): StepFlowBuilder

  private def build_basicFlow {
    def build_from_dsl {
      dslStoryObject.basicFlow.traverse(make_stepFlowBuilder(_basic_flow.root, dslStoryObject))
    }

/* 2008-12-15
    def build_from_dsl_BAK { // 2008-12-14
      dslStoryObject.basicFlow.traverse(new GTreeVisitor[SStep, Nothing] {
	var _current_task: BusinessTask = null
	var _current_step: SMStep = null

	def set_task(aTask: BusinessTask, aStep: SMBusinessTaskStep) {
	  require (_current_task == null)
	  require (_current_step == null)
	  _current_task = aTask
	  _basic_flow += aStep
	  _current_step = aStep
	}

	def ensure_task {
	  if (_current_task == null) {
	    // XXX syntax error : in case of using service
	    val task = NullBusinessTask
	    val step = new SMBusinessTaskStep(NullBusinessTaskStep, task)
	    set_task(task, step)
	  }
	}

	def register_mark(aStep: SMStep) {
	  if (aStep.mark != "") {
	    _stepByMark.put(aStep.mark, aStep)
	  }
	}

	override def enter(aNode: GTreeNode[SStep]) {
	  def enter_extendUsecase_step(aStep: ExtendBusinessUsecaseStep) {
	    val usecases = aStep.businessUsecases
	    val step = new SMExtendBusinessUsecaseStep(aStep, usecases)
	    // XXX alternate and exception flow
	    ensure_task
	    register_mark(step)
	    _current_step.addChild(step)
	    // include relationships
	  }

	  def enter_usecase_step(aStep: BusinessUsecaseStep) {
	    require (_current_task == null) // XXX syntax error : handle in DSL
	    require (_current_step == null)
	    val usecase = aStep.businessUsecase
	    val step = new SMBusinessUsecaseStep(aStep, usecase)
	    // XXX alternate and exception flow
	    ensure_task
	    register_mark(step)
	    _current_step.addChild(step)
	    // include relationships
	  }

	  def enter_task_step(aStep: BusinessTaskStep) {
	    require (_current_task == null) // XXX syntax error : handle in DSL
	    val task = aStep.businessTask
	    val step = new SMBusinessTaskStep(aStep, task)
	    // XXX alternate and exception flow
	    set_task(task, step)
	  }

	  def enter_action_step(aStep: SActionStep) {
	    val step = new SMActionStep(aStep)
	    ensure_task
	    register_mark(step)
	    _current_step.addChild(step)
	    _current_step = step
	  }

	  def enter_execution_step(aStep: SExecutionStep) {
	    val step = new SMExecutionStep(aStep)
	    ensure_task
	    register_mark(step)
	    _current_step.addChild(step)
	    _current_step = step
	  }

	  def enter_invocation_step(aStep: SInvocationStep) {
	    val step = new SMInvocationStep(aStep)
	    ensure_task
	    register_mark(step)
	    _current_step.addChild(step)
	    _current_step = step
	    step.dslInvocationStep.requestDocumentOption match {
	      case Some(doc: SDocument) => add_use_request(doc, step)
	      case None => //
	    }
	    step.dslInvocationStep.responseDocumentOption match {
	      case Some(doc: SDocument) => add_use_response(doc, step)
	      case None => //
	    }
	  }

	  aNode.content match {
	    case usecase: ExtendBusinessUsecaseStep => enter_extendUsecase_step(usecase)
	    case usecase: BusinessUsecaseStep => enter_usecase_step(usecase)
//	    case usecase: RequirementUsecaseStep => enter_usecase_step(usecase)
	    case task: BusinessTaskStep => enter_task_step(task)
//	    case task: RequirementTaskStep => enter_task_step(task)
	    case step: SActionStep => enter_action_step(step)
	    case step: SExecutionStep => enter_execution_step(step)
	    case step: SInvocationStep => enter_invocation_step(step)
	    case step => error("Unkonwn step: " + step)
	  }
	}

	override def leave(aNode: GTreeNode[SStep]) {
	  def leave_extendUsecase_step(aStep: ExtendBusinessUsecaseStep) {
	  }

	  def leave_usecase_step(aStep: BusinessUsecaseStep) {
	    _current_task = null
	    _current_step = null
	  }

	  def leave_task_step(aStep: BusinessTaskStep) {
	    _current_task = null
	    _current_step = null
	  }

	  def leave_step(aStep: SStep) {
	    _current_step = _current_step.parent.asInstanceOf[SMStep]
	  }

	  aNode.content match {
	    case usecase: ExtendBusinessUsecaseStep => leave_extendUsecase_step(usecase)
	    case usecase: BusinessUsecaseStep => leave_usecase_step(usecase)
//	    case usecase: RequirementUsecaseStep => leave_usecase_step(usecase)
	    case task: BusinessTaskStep => leave_task_step(task)
//	    case task: RequirementTaskStep => leave_task_step(task)
	    case step: SStep => leave_step(step)
	  }
	}

	def add_use_request(aDoc: SDocument, aStep: SMInvocationStep) {
	  val use = make_use
	  use.useKind = UsecasePrivate // XXX
	  use.elementQName = aDoc.qualifiedName
	  if (aStep.isPrimaryActor) {
	    use.userQName = aStep.primaryActorQName
	  } else {
	    aStep.primaryActorKind match {
	      case BusinessClient => {
		use.userQName = dslStoryObject.businessClient.qualifiedName
	      }
	      case BusinessWorker => {
		use.userQName = dslStoryObject.businessWorker.qualifiedName
	      }
	      case SystemClient => {
		//
	      }
	      case SystemUnderDiscussion => {
		use.userQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	      }
	      case _ => //
	    }
	  }
	  if (aStep.isSecondaryActor) {
	    use.receiverQName = aStep.secondaryActorQName
	  } else {
	    aStep.secondaryActorKind match {
	      case BusinessClient => {
		use.receiverQName = dslStoryObject.businessClient.qualifiedName
	      }
	      case BusinessWorker => {
		use.receiverQName = dslStoryObject.businessWorker.qualifiedName
	      }
	      case SystemClient => {
		//
	      }
	      case SystemUnderDiscussion => {
		use.receiverQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	      }
	      case _ => //
	    }
	  }
	  addUse(use)
	}

	def add_use_response(aDoc: SDocument, aStep: SMInvocationStep) {
	  val use = make_use
	  use.useKind = UsecasePrivate // XXX
	  use.elementQName = aDoc.qualifiedName
	  if (aStep.isPrimaryActor) {
	    use.receiverQName = aStep.primaryActorQName
	  } else {
	    aStep.primaryActorKind match {
	      case BusinessClient => {
		use.receiverQName = dslStoryObject.businessClient.qualifiedName
	      }
	      case BusinessWorker => {
		use.receiverQName = dslStoryObject.businessWorker.qualifiedName
	      }
	      case SystemClient => {
		//
	      }
	      case SystemUnderDiscussion => {
		use.receiverQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	      }
	      case _ => //
	    }
	  }
	  if (aStep.isSecondaryActor) {
	    use.userQName = aStep.secondaryActorQName
	  } else {
	    aStep.secondaryActorKind match {
	      case BusinessClient => {
		use.userQName = dslStoryObject.businessClient.qualifiedName
	      }
	      case BusinessWorker => {
		use.userQName = dslStoryObject.businessWorker.qualifiedName
	      }
	      case SystemClient => {
		//
	      }
	      case SystemUnderDiscussion => {
		use.userQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	      }
	      case _ => //
	    }
	  }
	  addUse(use)
	}

	def make_use = {
	  val dslUse = new SUse(UUID.randomUUID.toString)
	  val use = new SMUse(dslUse)
	  use
	}
      })
    }
*/

    def resolve_flow {
      new BasicFlowCounter(_basic_flow.root)
      /* 2008-12-08
      val counter = new TreeNodeCounter(_basic_flow: _*)
      for (task <- _basic_flow) {
	task.traverse(new GTreeVisitor[SMElement, Nothing] {
	  override def enter(aNode: GTreeNode[SMElement]) {
	    def enter_step(aStep: SMStep) {
	      aStep.sequenceNumber = counter.getSequenceNumber(aNode)
	      aStep.layeredSequenceNumber = counter.getLayeredSequenceNumber(aNode)
	    }

	    aNode.content match {
	      case step: SMStep => enter_step(step)
	    }
	  }
	  override def leave(aNode: GTreeNode[SMElement]) {
	  }
	})
      }
      */
    }

/* 2008-12-09
    def build_embedded_task {
      for (step <- _basic_flow) {
	step.task.build(step)
      }
    }
*/

    build_from_dsl
    resolve_flow
//    build_embedded_task
  }

  private def build_extensionFlow {
    def build_from_dsl {
      for (segment <- dslStoryObject.extensionFlow) {
//        record_trace("SExtensionSegment flow = " + segment.flow.toXml)
        val smSegment = new SMExtensionSegment(segment)
        _extension_flow += smSegment
        segment.flow.traverse(make_stepFlowBuilder(smSegment.flow.root, dslStoryObject))
//        record_trace("SMExtensionSegment flow = " + smSegment.flow.toXml)
//        record_trace("SMExtensionSegment flow(root) = " + smSegment.flow.root.toXml)
      }
    }

    def resolve_flow {
      for (segment <- _extension_flow) {
	new BasicFlowCounter(segment.flow.root)
      }
    }

    build_from_dsl
    resolve_flow
  }

  private def build_alternateFlow {
    def build_from_dsl {
      dslStoryObject.alternateFlow.traverse(new GTreeVisitor[SPath] {
	val _cursor = _alternate_flow.cursor

	override def enter(aNode: GTreeNode[SPath]) {
	  val path = make_alternate_path(aNode.content)
	  _cursor.enter(path)
	}

	override def leave(aNode: GTreeNode[SPath]) {
	  _cursor.leave()
	}

      })
    }

    def resolve_flow {
    }
    
    build_from_dsl
    resolve_flow
  }

  private def build_exceptionFlow {
    def build_from_dsl {
      dslStoryObject.exceptionFlow.traverse(new GTreeVisitor[SPath] {
	val _cursor = _exception_flow.cursor

	override def enter(aNode: GTreeNode[SPath]) {
	  val path = new SMExceptionPath(aNode.content.asInstanceOf[SExceptionPath])
	  path.targetSteps ++= getSteps(path.markRange) // XXX syntax error
	  _cursor.enter(path)
	}

	override def leave(aNode: GTreeNode[SPath]) {
	  _cursor.leave()
	}
      })
    }

    def resolve_flow {
    }

    build_from_dsl
    resolve_flow
  }

/* 2008-12-09
  // invoked by SMBusinessTask
  protected def build_basic_flow(aRoot: SMTaskStep) {
    val task = new SMBusinessTask(NullBusinessTask)
    val step = new SMBusinessTaskStep(NullBusinessTaskStep, task)
    _basic_flow += step
    println("build_basic_flow = " + aRoot)
    for (child <- aRoot.children) {
      println("build_basic_flow : " + child.deepCopy)
      step.addChild(child.deepCopy)
    }
    new BasicFlowCounter(_basic_flow)
    println("build_basic_flow *** " + step.toXml)
  }
*/

  final def getStep(aMark: String): Option[SMStep] = {
    _stepByMark.get(aMark)
  }

  final def getSteps(aMarkRange: (String, String)): Seq[SMStep] = {
    val head = getStep(aMarkRange._1).get
    val mayTail = getStep(aMarkRange._2)
    // XXX calc intermediate steps
    if (mayTail.isDefined) {
      Array(head, mayTail.get)
    } else {
      Array(head)
    }
  }

  final def getPrimaryActorTerm(aStep: SMStep): SDoc = {
    if (aStep.isPrimaryActor) {
      aStep.getPrimaryActorTerm
    } else {
      aStep.primaryActorKind match {
        case BusinessClientActorKind => _business_client_term()
        case BusinessWorkerActorKind => _business_worker_term()
        case SystemClientActorKind => _system_client_term()
        case SystemUnderDiscussionActorKind => _system_under_discussion_term()
        case _ => "Unknown"
      }
    }
  }

  final def getSecondaryActorTerm(aStep: SMStep): SDoc = {
    if (aStep.isSecondaryActor) {
      aStep.getSecondaryActorTerm
    } else {
      aStep.secondaryActorKind match {
        case BusinessClientActorKind => _business_client_term()
        case BusinessWorkerActorKind => _business_worker_term()
	      case SystemClientActorKind => _system_client_term()
	      case SystemUnderDiscussionActorKind => _system_under_discussion_term()
	      case _ => "Unknown"
      }
    }
  }
  
  private def _business_client_term(story: SStoryObject = dslStoryObject) = {
    object_term(story.businessClient)
  }

  private def _business_worker_term(story: SStoryObject = dslStoryObject) = {
    object_term(story.businessWorker)
  }

  private def _system_client_term(story: SStoryObject = dslStoryObject) = {
    object_term(story.systemClient)
  }

  private def _system_under_discussion_term(story: SStoryObject = dslStoryObject) = {
    object_term(story.systemUnderDiscussion)
  }

  private def object_term(anObj: Option[SObject], defaultterm: SDoc): SDoc = {
    anObj match {
      case Some(obj) => new SIAnchor(obj.term) unresolvedRef_is new SElementRef(obj.packageName, obj.name)
      case None => defaultterm
    }
  }

  private def object_term(anObj: SObject): SDoc = {
    new SIAnchor(anObj.term) unresolvedRef_is new SElementRef(anObj.packageName, anObj.name)
  }

  private def make_alternate_path(aPath: SPath): SMAlternatePath = {
    aPath match {
      case path: SJumpPath => {
	val jumpPath = new SMJumpPath(path)
	val mayStep = getStep(jumpPath.destination)
	if (mayStep.isEmpty) {
	  syntax_error("invalid step id = " + jumpPath.destination)
	}
	jumpPath.destinationStep = mayStep.get
	jumpPath
      }
      case path: SOmitPath => new SMOmitPath(path)
      case path: SSubstitutePath => new SMSubstitutePath(path)
      case path => error("not implemented yet: " + path)
    }
  }

  private def make_exception_path(aPath: SPath): SMExceptionPath = {
    aPath match {
      case path: SExceptionPath => new SMExceptionPath(path)
      case path => error("not implemented yet: " + path)
    }
  }

  private def register_mark(aStep: SMStep) {
    if (aStep.mark != "") {
      _stepByMark.put(aStep.mark, aStep)
    }
  }
}
