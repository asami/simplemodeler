package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl.business._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.domain.story._
import org.simplemodeling.dsl.requirement._
import org.goldenport.value.{GTree, GTreeNode, PlainTree, GTreeCursor, GTreeVisitor}

/*
 * @since   Dec.  7, 2008
 *  version Apr. 17, 2011
 *  version Nov.  4, 2012
 * @version May.  7, 2016
 * @author  ASAMI, Tomoharu
 */
/**
 * SStoryObject is SEntity. 
 * It's workaround to handle BusinessUsecase in association.
 */
abstract class SStoryObject(aName: String, aPkgName: String) extends SEntity(aName, aPkgName) {
  val primary_actor = association.candidate("primaryActor")
  val secondary_actor = association.candidate("secondaryActor")
  val supporting_actor = association.candidate("supportingActor")
  val client = association.candidate("client") // businessClient or systemClient
  val worker = association.candidate("businessWorker") // BusinessUsecase
  val system = association.candidate("systemUnderDiscussion")

  private val _basic_flow = new PlainTree[SStep]
  private val _extension_flow = new ArrayBuffer[SExtensionSegment]
  private val _alternate_flow = new PlainTree[SPath]
  private val _exception_flow = new PlainTree[SPath]
  private var _step_flow_cursor: GTreeCursor[SStep] = null
  private var _path_flow_cursor: GTreeCursor[SPath] = null
  private var _in_basic_flow = false
  private var _in_extension_flow = false
  private var _in_alternate_flow = false
  private var _in_exception_flow = false

  def this() = this(null, null)
  def this(aName: String) = this(aName, null)

  def basic_flow(theSteps: => Unit) {
    _in_basic_flow = true
    _step_flow_cursor = _basic_flow.cursor
    theSteps
    _step_flow_cursor = null // XXX
    adjust_Embedded_Task(_basic_flow.root)
    _in_basic_flow = false
  }

  protected def adjust_Embedded_Task(aRoot: GTreeNode[SStep]): Unit = {}

  def extension_flow(aName: String)(theSteps: => Unit) {
    _in_extension_flow = true
    val segment = new SExtensionSegment(aName)
    _extension_flow += segment
    _step_flow_cursor = segment.cursor
    theSteps
    _step_flow_cursor = null // XXX
    _in_extension_flow = false
  }

  def alternate_flow(thePaths: => Unit) {
    _in_alternate_flow = true
    _path_flow_cursor = _alternate_flow.cursor
    thePaths
    _path_flow_cursor = null // XXX
    _in_alternate_flow = false
    merge_Alternate_Path(_alternate_flow.root, _basic_flow.root)
  }

  protected def merge_Alternate_Path(aRoot: GTreeNode[SPath], theSteps: GTreeNode[SStep]): Unit = {}

  def exception_flow(thePaths: => Unit) {
    _in_exception_flow = true
    _path_flow_cursor = _exception_flow.cursor
    thePaths
    _path_flow_cursor = null // XXX
    _in_exception_flow = false
    merge_Exception_Path(_exception_flow.root, _basic_flow.root)
  }

  protected def merge_Exception_Path(aRoot: GTreeNode[SPath], theSteps: GTreeNode[SStep]): Unit = {}

  protected final def execute_step(aStep: SStep) = {
    _step_flow_cursor.enter(aStep)
    _step_flow_cursor.leave(aStep)
    aStep
  }

  protected final def execute_step(aStep: SStep, theSteps: => Unit) = {
    _step_flow_cursor.enter(aStep)
    theSteps
    _step_flow_cursor.leave(aStep)
    aStep
  }

  protected final def execute_path(aPath: SPath) = {
    _path_flow_cursor.enter(aPath)
    _path_flow_cursor.leave(aPath)
    aPath
  }

  //
  def action(anAction: String): SActionStep = {
    val step = new SActionStep(anAction)
    execute_step(step)
    step
  }

  def action(anAction: String)(theSteps: => Unit): SStep = {
    val step = new SActionStep(anAction)
    execute_step(step, theSteps)
    step
  }

  def action_system(anAction: String)(theSteps: => Unit): SStep = {
    val step = new SActionStep(anAction)
    step.primaryActorKind = SystemUnderDiscussionActorKind
    execute_step(step, theSteps)
    step
  }

  def step_include(): SStep = {
    error("not implemented yet.")
  }

  def step_extend(): SStep = {
    error("not implemented yet.")
  }

  def step_generalization_repeat: SStep = {
    error("not implemented yet.")
  }

  def step_generalization_substitute: SStep = {
    error("not implemented yet.")
  }

  def step_generalization_omit: SStep = {
    error("not implemented yet.")
  }

  def step_generalization_jump: SStep = {
    error("not implemented yet.")
  }

  // XXX include_usecase
  def include_business_usecase(uc: BusinessUsecase): SUsecaseStep = {
    val step = new BusinessUsecaseStep(uc)
    execute_step(step)
    step
  }

  // XXX include_usecase
  def include_requirement_usecase(uc: RequirementUsecase): SUsecaseStep = {
    val step = new RequirementUsecaseStep(uc)
    execute_step(step)
    step
  }

  // XXX include_task
  def include_business_task(t: BusinessTask): STaskStep = {
    val step = new BusinessTaskStep(t)
    execute_step(step)
    step
  }

  // XXX include_task
  def include_requirement_task(t: RequirementTask): STaskStep = {
    val step = new RequirementTaskStep(t)
    execute_step(step)
    step
  }

  def event_issue(anEvent: DomainEvent)(theSteps: => Unit): SExecutionStep = {
    val step = new SExecutionStep(Issue, anEvent)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step, theSteps)
    step
  }

  def event_open(anEvent: DomainEvent)(theSteps: => Unit): SExecutionStep = {
    val step = new SExecutionStep(Open, anEvent)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step, theSteps)
    step
  }

  def event_close(anEvent: DomainEvent)(theSteps: => Unit): SExecutionStep = {
    val step = new SExecutionStep(Close, anEvent)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step, theSteps)
    step
  }

  def resource_create(aResource: DomainResource): SExecutionStep = {
    val step = new SExecutionStep(Create, aResource)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def resource_read(aResource: DomainResource): SExecutionStep = {
    val step = new SExecutionStep(Read, aResource)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def resource_update(aResource: DomainResource): SExecutionStep = {
    val step = new SExecutionStep(Update, aResource)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def resource_delete(aResource: DomainResource): SExecutionStep = {
    val step = new SExecutionStep(Delete, aResource)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def actor_create(anActor: DomainActor): SExecutionStep = {
    val step = new SExecutionStep(Create, anActor)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def actor_read(anActor: DomainActor): SExecutionStep = {
    val step = new SExecutionStep(Read, anActor)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def actor_update(anActor: DomainActor): SExecutionStep = {
    val step = new SExecutionStep(Update, anActor)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def actor_delete(anActor: DomainActor): SExecutionStep = {
    val step = new SExecutionStep(Delete, anActor)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def role_create(aRole: DomainRole): SExecutionStep = {
    val step = new SExecutionStep(Create, aRole)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def role_read(aRole: DomainRole): SExecutionStep = {
    val step = new SExecutionStep(Read, aRole)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def role_update(aRole: DomainRole): SExecutionStep = {
    val step = new SExecutionStep(Update, aRole)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def role_delete(aRole: DomainRole): SExecutionStep = {
    val step = new SExecutionStep(Delete, aRole)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def entity_create(anEntity: DomainEntity): SExecutionStep = {
    val step = new SExecutionStep(Create, anEntity)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def entity_read(anEntity: DomainEntity): SExecutionStep = {
    val step = new SExecutionStep(Read, anEntity)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def entity_update(anEntity: DomainEntity): SExecutionStep = {
    val step = new SExecutionStep(Update, anEntity)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def entity_delete(anEntity: DomainEntity): SExecutionStep = {
    val step = new SExecutionStep(Delete, anEntity)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  def document_parameter(aDocument: DomainDocument, aParam: String): SParameterStep = {
    val step = new SParameterStep(aDocument, aParam)
    step.primaryActorKind = SystemUnderDiscussionActorKind // XXX
    execute_step(step)
    step
  }

  // alternate
  def path_repeat(aMark: String, aCondition: String): SRepeatPath = {
    val path = new SRepeatPath(aMark, aCondition)
    execute_path(path)
    path
  }

  def path_repeat(aMarkRange: (String, String), aCondition: String): SRepeatPath = {
    val path = new SRepeatPath(aMarkRange, aCondition)
    execute_path(path)
    path
  }

  def path_substitute(aMark: String, aCondition: String): SSubstitutePath = {
    val path = new SSubstitutePath(aMark, aCondition)
    execute_path(path)
    path
  }

  def path_substitute(aMarkRange: (String, String), aCondition: String): SSubstitutePath = {
    val path = new SSubstitutePath(aMarkRange, aCondition)
    execute_path(path)
    path
  }

  def path_omit(aMark: String, aCondition: String): SOmitPath = {
    val path = new SOmitPath(aMark, aCondition)
    execute_path(path)
    path
  }

  def path_omit(aMarkRange: (String, String), aCondition: String): SOmitPath = {
    val path = new SOmitPath(aMarkRange, aCondition)
    execute_path(path)
    path
  }

  def path_jump(aMark: String, aCondition: String, aDestination: String): SJumpPath = {
    val path = new SJumpPath(aMark, aCondition, aDestination)
    execute_path(path)
    path
  }

  def path_jump(aMarkRange: (String, String), aCondition: String, aDestination: String): SJumpPath = {
    val path = new SJumpPath(aMarkRange, aCondition, aDestination)
    execute_path(path)
    path
  }

  def path(aMark: String, aCondition: String, aDestination: String): SPath = {
    if (_in_alternate_flow) {
      path_jump(aMark, aCondition, aDestination)
    } else if (_in_exception_flow) {
      error("syntax error") // XXX
    } else {
      error("syntax error") // XXX
    }
  }

  // exception
  def path(aMark: String, aCondition: String): SExceptionPath = {
    val path = new SExceptionPath(aMark, aCondition)
    execute_path(path)
    path
  }

  // from BusinessUsecase
  final def copyInBasicFlow(aBasicFlow: GTree[SStep], aTaskStep: BusinessTaskStep) {

    val cursor = _basic_flow.cursor
    val root = get_step_node(aTaskStep, aBasicFlow)
//    util.UDsl.printFlow(root) // XXX
    root.traverse(new GTreeVisitor[SStep] {
      override def enter(aNode: GTreeNode[SStep]) {
	cursor.enter(aNode.content)
      }

      override def leave(aNode: GTreeNode[SStep]) {
	cursor.leave(aNode.content)
      }
    })
  }

  // from BusinessUsecase
  final def copyInBasicFlow(aBasicFlow: GTree[SStep], theTaskSteps: Seq[GTreeNode[SStep]]) {
    val cursor = _basic_flow.cursor
    for (root <- theTaskSteps) {
      root.traverse(new GTreeVisitor[SStep] {
	override def startEnter(aNode: GTreeNode[SStep]) {
	  cursor.enter(aNode.content)
	}

	override def leaveEnd(aNode: GTreeNode[SStep]) {
	  cursor.leave(aNode.content)
	}
      })
    }
  }

  protected final def get_step_node(aTaskStep: STaskStep, aBasicFlow: GTree[SStep]): GTreeNode[SStep] = {
//    println("get_step_node *** " + aBasicFlow.toXml)
//    println("get_step_node +++ " + aTaskStep)
      for (node <- aBasicFlow.root.children) {
//      println("get_step_node = " + node)
	if (node.content == aTaskStep) return node
      }
      error("not found")
    }

  //
  final def primaryActor: SEntity = {
    association.getAssociation("primaryActor") match {
      case Some(assoc: SAssociation) => assoc.entity
      case None => error("not implemented yet")
    }
  }

  final def getSecondaryActor: Option[SEntity] = {
    association.getAssociation("secondaryActor") match {
      case Some(assoc: SAssociation) => Some(assoc.entity)
      case None => None
    }
  }

  final def basicFlow: GTree[SStep] = _basic_flow
  final def extensionFlow: Seq[SExtensionSegment] = _extension_flow
  final def alternateFlow: GTree[SPath] = _alternate_flow
  final def exceptionFlow: GTree[SPath] = _exception_flow

  //
  final def businessClient: SAgent = {
    association.findAssociation("client", "primaryActor") match {
      case Some(assoc: SAssociation) => assoc.entity.asInstanceOf[SAgent]
      case None => SystemUnderDiscussion // XXX
    }
  }

  final def businessWorker: SAgent = {
    association.findAssociation("businessWorker", "secondaryActor") match {
      case Some(assoc: SAssociation) => assoc.entity.asInstanceOf[SAgent]
      case None => SystemUnderDiscussion // XXX
    }
  }

  final def systemClient: SAgent = {
    association.findAssociation("client", "primaryActor") match {
      case Some(assoc: SAssociation) => assoc.entity.asInstanceOf[SAgent]
      case None => SystemUnderDiscussion // XXX
    }
  }

  final def systemUnderDiscussion: SAgent = {
    association.findAssociation("systemUnderDiscussion", "secondaryActor") match {
      case Some(assoc: SAssociation) => assoc.entity.asInstanceOf[SAgent]
      case None => SystemUnderDiscussion
    }
  }

  final def addAlternatePath(aPath: SAlternatePath) {
    _alternate_flow.root.addContent(aPath)
  }

  final def addExceptionPath(aPath: SAlternatePath) {
  }
}

object NullStoryObject extends SStoryObject
