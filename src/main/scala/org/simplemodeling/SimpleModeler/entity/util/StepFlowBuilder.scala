package org.simplemodeling.SimpleModeler.entity.util

import scala.collection.mutable.{Buffer, ArrayBuffer, HashMap}
import java.util.UUID
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.simplemodeling.dsl.requirement._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._
import org.goldenport.value.{GTree, GTreeNode, GTreeVisitor, PlainTree, GTreeCursor}
import org.goldenport.value.util.TreeNodeCounter
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import com.asamioffice.goldenport.text.UPathString

/*
 * @since   Dec. 14, 2008
 *  version Nov.  4, 2011
 * @version Nov.  5, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class StepFlowBuilder(
  val root: GTreeNode[SMStep],
  val dslStoryObject: SStoryObject,
  val registerMark: SMStep => Unit,
  val addUse: SMUse => Unit
) extends GTreeVisitor[SStep] {
  var _current_step: SMStep = Option(root.content) getOrElse {
    root.content = new SMRootStep()
    root.content
  }
  println("StepFlowBuilder: " + _current_step)
  require (_current_step != null, "root content must not null.")
  
  private def register_mark(aStep: SMStep) {
    if (aStep.mark != "") {
      registerMark(aStep)
//      _stepByMark.put(aStep.mark, aStep)
    }
  }

  override def enter(aNode: GTreeNode[SStep]) {
    println("StepFlowBuilder#enter: " + _current_step)
    require (_current_step != null, "_current_step must be initialized.")
    def enter_extendUsecase_step(aStep: ExtendBusinessUsecaseStep) {
      val usecases = aStep.businessUsecases
      val step = new SMExtendBusinessUsecaseStep(aStep, usecases)
      // XXX alternate and exception flow
      register_mark(step)
      _enter_add(step)
      // include relationships
      println("StepFlowBuilder#enter_extendUsecase_step: " + aStep)
    }

    def enter_business_usecase_step(aStep: BusinessUsecaseStep) {
      val usecase = aStep.businessUsecase
      val step = new SMBusinessUsecaseStep(aStep, usecase)
      // XXX alternate and exception flow
      register_mark(step)
      _enter_add(step)
      // include relationships
      println("StepFlowBuilder#enter_business_usecase_step: " + aStep)
    }

    def enter_business_task_step(aStep: BusinessTaskStep) {
      val task = aStep.businessTask
      val step = new SMBusinessTaskStep(aStep, task)
      // XXX alternate and exception flow
      register_mark(step)
      _enter_add(step)
      println("StepFlowBuilder#enter_business_task_step: " + aStep)
    }

    def enter_requirement_usecase_step(aStep: RequirementUsecaseStep) {
      val usecase = aStep.requirementUsecase
      val step = new SMRequirementUsecaseStep(aStep, usecase)
      // XXX alternate and exception flow
      register_mark(step)
      _enter_add(step)
      // include relationships
      println("StepFlowBuilder#enter_requirement_usecase_step: " + aStep)
    }

    def enter_requirement_task_step(aStep: RequirementTaskStep) {
      val task = aStep.requirementTask
      val step = new SMRequirementTaskStep(aStep, task)
      // XXX alternate and exception flow
      register_mark(step)
      _enter_add(step)
      println("StepFlowBuilder#enter_requirement_usecase_step: " + aStep)
    }

    def enter_action_step(aStep: SActionStep) {
      val step = new SMActionStep(aStep)
      register_mark(step)
      _enter_add(step)
    }

    def enter_execution_step(aStep: SExecutionStep) {
      val step = new SMExecutionStep(aStep)
      register_mark(step)
      _enter_add(step)
    }

    def enter_invocation_step(aStep: SInvocationStep) {
      val step = new SMInvocationStep(aStep)
      register_mark(step)
      _enter_add(step)
      step.dslInvocationStep.requestDocumentOption match {
	case Some(doc: SDocument) => add_use_request(doc, step)
	case None => //
      }
      step.dslInvocationStep.responseDocumentOption match {
	case Some(doc: SDocument) => add_use_response(doc, step)
	case None => //
      }
    }

    def enter_parameter_step(aStep: SParameterStep) {
//      record_trace("enter_parameter_step")
      val step = new SMParameterStep(aStep)
//      record_trace("curren_step = " + _current_step)
//      new StepFlowPrinter(root)
      register_mark(step)
      _enter_add(step)
    }

    aNode.content match {
      case usecase: ExtendBusinessUsecaseStep => enter_extendUsecase_step(usecase)
      case usecase: BusinessUsecaseStep => enter_business_usecase_step(usecase)
      case usecase: RequirementUsecaseStep => enter_requirement_usecase_step(usecase)
      case task: BusinessTaskStep => enter_business_task_step(task)
      case task: RequirementTaskStep => enter_requirement_task_step(task)
      case step: SActionStep => enter_action_step(step)
      case step: SExecutionStep => enter_execution_step(step)
      case step: SInvocationStep => enter_invocation_step(step)
      case step: SParameterStep => enter_parameter_step(step)
      case step => error("Unkonwn step: " + step)
    }
  }

  private def _enter_add(step: SMStep) {
    _current_step.addChild(step)
    _current_step = step;
  }

  override def leave(aNode: GTreeNode[SStep]) {
    println("StepFlowBuilder#leave: " + _current_step)
    def leave_extendUsecase_step(aStep: ExtendBusinessUsecaseStep) {
      leave_step(aStep)
    }

    def leave_business_usecase_step(aStep: BusinessUsecaseStep) {
      leave_step(aStep)
    }

    def leave_business_task_step(aStep: BusinessTaskStep) {
      leave_step(aStep)
    }

    def leave_requirement_usecase_step(aStep: RequirementUsecaseStep) {
      leave_step(aStep)
    }

    def leave_requirement_task_step(aStep: RequirementTaskStep) {
      leave_step(aStep)
    }

    def leave_step(aStep: SStep) {
      _current_step = _current_step.parent.asInstanceOf[SMStep]
      require (_current_step != null, "leave_step keeps _cuurent_step active.")
    }

    aNode.content match {
      case usecase: ExtendBusinessUsecaseStep => leave_extendUsecase_step(usecase)
      case usecase: BusinessUsecaseStep => leave_business_usecase_step(usecase)
      case usecase: RequirementUsecaseStep => leave_requirement_usecase_step(usecase)
      case task: BusinessTaskStep => leave_business_task_step(task)
      case task: RequirementTaskStep => leave_requirement_task_step(task)
      case step: SStep => leave_step(step)
    }
  }

  private def add_use_request(aDoc: SDocument, aStep: SMInvocationStep) {
    val use = make_use
    use.useKind = UsecasePrivate // XXX
    use.elementQName = aDoc.qualifiedName
    if (aStep.isPrimaryActor) {
      use.userQName = aStep.primaryActorQName
    } else {
      aStep.primaryActorKind match {
	case BusinessClientActorKind => {
	  use.userQName = dslStoryObject.businessClient.qualifiedName
	}
	case BusinessWorkerActorKind => {
	  use.userQName = dslStoryObject.businessWorker.qualifiedName
	}
	case SystemClientActorKind => {
	  use.userQName = dslStoryObject.systemClient.qualifiedName
	}
	case SystemUnderDiscussionActorKind => {
	  use.userQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	}
	case _ => //
      }
    }
    if (aStep.isSecondaryActor) {
      use.receiverQName = aStep.secondaryActorQName
    } else {
      aStep.secondaryActorKind match {
	case BusinessClientActorKind => {
	  use.receiverQName = dslStoryObject.businessClient.qualifiedName
	}
	case BusinessWorkerActorKind => {
	  use.receiverQName = dslStoryObject.businessWorker.qualifiedName
	}
	case SystemClientActorKind => {
	  use.receiverQName = dslStoryObject.systemClient.qualifiedName
	}
	case SystemUnderDiscussionActorKind => {
	  use.receiverQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	}
	case _ => //
      }
    }
    addUse(use)
  }

  private def add_use_response(aDoc: SDocument, aStep: SMInvocationStep) {
    val use = make_use
    use.useKind = UsecasePrivate // XXX
    use.elementQName = aDoc.qualifiedName
    if (aStep.isPrimaryActor) {
      use.receiverQName = aStep.primaryActorQName
    } else {
      aStep.primaryActorKind match {
	case BusinessClientActorKind => {
	  use.receiverQName = dslStoryObject.businessClient.qualifiedName
	}
	case BusinessWorkerActorKind => {
	  use.receiverQName = dslStoryObject.businessWorker.qualifiedName
	}
	case SystemClientActorKind => {
	  use.receiverQName = dslStoryObject.systemClient.qualifiedName
	}
	case SystemUnderDiscussionActorKind => {
	  use.receiverQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	}
	case _ => //
      }
    }
    if (aStep.isSecondaryActor) {
      use.userQName = aStep.secondaryActorQName
    } else {
      aStep.secondaryActorKind match {
	case BusinessClientActorKind => {
	  use.userQName = dslStoryObject.businessClient.qualifiedName
	}
	case BusinessWorkerActorKind => {
	  use.userQName = dslStoryObject.businessWorker.qualifiedName
	}
	case SystemClientActorKind => {
	  use.userQName = dslStoryObject.systemClient.qualifiedName
	}
	case SystemUnderDiscussionActorKind => {
	  use.userQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	}
	case _ => //
      }
    }
    addUse(use)
  }

  private def make_use = {
    val dslUse = new SUse(UUID.randomUUID.toString)
    val use = new SMUse(dslUse)
    use
  }
}

abstract class StepFlowBuilder0(
  val root: GTreeNode[SMStep],
  val dslStoryObject: SStoryObject,
  val registerMark: SMStep => Unit,
  val addUse: SMUse => Unit
) extends GTreeVisitor[SStep] {
  var _current_task: STask = null
  var _current_step: SMStep = null

  private def set_task(aTask: STask, aStep: SMTaskStep) {
    require (_current_task == null)
    require (_current_step == null)
    _current_task = aTask
    root.addChild(aStep.asInstanceOf[GTreeNode[SMStep]])
    _current_step = aStep
  }

  private def ensure_task {
    if (_current_task == null) {
      // XXX syntax error : in case of using service
      val (task, step) = make_Null_TaskStep
      set_task(task, step)
    }
  }

  protected def make_Null_TaskStep: (STask, SMTaskStep)

  private def register_mark(aStep: SMStep) {
    if (aStep.mark != "") {
      registerMark(aStep)
//      _stepByMark.put(aStep.mark, aStep)
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
      println("StepFlowBuilder#enter_extendUsecase_step: " + aStep)
    }

    def enter_business_usecase_step(aStep: BusinessUsecaseStep) {
      require (_current_task == null) // XXX syntax error : handle in DSL
      require (_current_step == null)
      val usecase = aStep.businessUsecase
      val step = new SMBusinessUsecaseStep(aStep, usecase)
      // XXX alternate and exception flow
      ensure_task
      register_mark(step)
      _current_step.addChild(step)
      // include relationships
      println("StepFlowBuilder#enter_business_usecase_step: " + aStep)
    }

    def enter_business_task_step(aStep: BusinessTaskStep) {
      require (_current_task == null) // XXX syntax error : handle in DSL
      val task = aStep.businessTask
      val step = new SMBusinessTaskStep(aStep, task)
      // XXX alternate and exception flow
      set_task(task, step)
      println("StepFlowBuilder#enter_business_task_step: " + aStep)
    }

    def enter_requirement_usecase_step(aStep: RequirementUsecaseStep) {
      require (_current_task == null) // XXX syntax error : handle in DSL
      require (_current_step == null)
      val usecase = aStep.requirementUsecase
      val step = new SMRequirementUsecaseStep(aStep, usecase)
      // XXX alternate and exception flow
      ensure_task
      register_mark(step)
      _current_step.addChild(step)
      // include relationships
      println("StepFlowBuilder#enter_requirement_usecase_step: " + aStep)
    }

    def enter_requirement_task_step(aStep: RequirementTaskStep) {
      require (_current_task == null) // XXX syntax error : handle in DSL
      val task = aStep.requirementTask
      val step = new SMRequirementTaskStep(aStep, task)
      // XXX alternate and exception flow
      set_task(task, step)
      println("StepFlowBuilder#enter_requirement_usecase_step: " + aStep)
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

    def enter_parameter_step(aStep: SParameterStep) {
//      record_trace("enter_parameter_step")
      val step = new SMParameterStep(aStep)
      ensure_task
//      record_trace("curren_step = " + _current_step)
//      new StepFlowPrinter(root)
      register_mark(step)
      _current_step.addChild(step)
      _current_step = step
    }

    aNode.content match {
      case usecase: ExtendBusinessUsecaseStep => enter_extendUsecase_step(usecase)
      case usecase: BusinessUsecaseStep => enter_business_usecase_step(usecase)
      case usecase: RequirementUsecaseStep => enter_requirement_usecase_step(usecase)
      case task: BusinessTaskStep => enter_business_task_step(task)
      case task: RequirementTaskStep => enter_requirement_task_step(task)
      case step: SActionStep => enter_action_step(step)
      case step: SExecutionStep => enter_execution_step(step)
      case step: SInvocationStep => enter_invocation_step(step)
      case step: SParameterStep => enter_parameter_step(step)
      case step => error("Unkonwn step: " + step)
    }
  }

  override def leave(aNode: GTreeNode[SStep]) {
    def leave_extendUsecase_step(aStep: ExtendBusinessUsecaseStep) {
    }

    def leave_business_usecase_step(aStep: BusinessUsecaseStep) {
      _current_task = null
      _current_step = null
    }

    def leave_business_task_step(aStep: BusinessTaskStep) {
      _current_task = null
      _current_step = null
    }

    def leave_requirement_usecase_step(aStep: RequirementUsecaseStep) {
      _current_task = null
      _current_step = null
    }

    def leave_requirement_task_step(aStep: RequirementTaskStep) {
      _current_task = null
      _current_step = null
    }

    def leave_step(aStep: SStep) {
      _current_step = _current_step.parent.asInstanceOf[SMStep]
    }

    aNode.content match {
      case usecase: ExtendBusinessUsecaseStep => leave_extendUsecase_step(usecase)
      case usecase: BusinessUsecaseStep => leave_business_usecase_step(usecase)
      case usecase: RequirementUsecaseStep => leave_requirement_usecase_step(usecase)
      case task: BusinessTaskStep => leave_business_task_step(task)
      case task: RequirementTaskStep => leave_requirement_task_step(task)
      case step: SStep => leave_step(step)
    }
  }

  private def add_use_request(aDoc: SDocument, aStep: SMInvocationStep) {
    val use = make_use
    use.useKind = UsecasePrivate // XXX
    use.elementQName = aDoc.qualifiedName
    if (aStep.isPrimaryActor) {
      use.userQName = aStep.primaryActorQName
    } else {
      aStep.primaryActorKind match {
	case BusinessClientActorKind => {
	  use.userQName = dslStoryObject.businessClient.qualifiedName
	}
	case BusinessWorkerActorKind => {
	  use.userQName = dslStoryObject.businessWorker.qualifiedName
	}
	case SystemClientActorKind => {
	  use.userQName = dslStoryObject.systemClient.qualifiedName
	}
	case SystemUnderDiscussionActorKind => {
	  use.userQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	}
	case _ => //
      }
    }
    if (aStep.isSecondaryActor) {
      use.receiverQName = aStep.secondaryActorQName
    } else {
      aStep.secondaryActorKind match {
	case BusinessClientActorKind => {
	  use.receiverQName = dslStoryObject.businessClient.qualifiedName
	}
	case BusinessWorkerActorKind => {
	  use.receiverQName = dslStoryObject.businessWorker.qualifiedName
	}
	case SystemClientActorKind => {
	  use.receiverQName = dslStoryObject.systemClient.qualifiedName
	}
	case SystemUnderDiscussionActorKind => {
	  use.receiverQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	}
	case _ => //
      }
    }
    addUse(use)
  }

  private def add_use_response(aDoc: SDocument, aStep: SMInvocationStep) {
    val use = make_use
    use.useKind = UsecasePrivate // XXX
    use.elementQName = aDoc.qualifiedName
    if (aStep.isPrimaryActor) {
      use.receiverQName = aStep.primaryActorQName
    } else {
      aStep.primaryActorKind match {
	case BusinessClientActorKind => {
	  use.receiverQName = dslStoryObject.businessClient.qualifiedName
	}
	case BusinessWorkerActorKind => {
	  use.receiverQName = dslStoryObject.businessWorker.qualifiedName
	}
	case SystemClientActorKind => {
	  use.receiverQName = dslStoryObject.systemClient.qualifiedName
	}
	case SystemUnderDiscussionActorKind => {
	  use.receiverQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	}
	case _ => //
      }
    }
    if (aStep.isSecondaryActor) {
      use.userQName = aStep.secondaryActorQName
    } else {
      aStep.secondaryActorKind match {
	case BusinessClientActorKind => {
	  use.userQName = dslStoryObject.businessClient.qualifiedName
	}
	case BusinessWorkerActorKind => {
	  use.userQName = dslStoryObject.businessWorker.qualifiedName
	}
	case SystemClientActorKind => {
	  use.userQName = dslStoryObject.systemClient.qualifiedName
	}
	case SystemUnderDiscussionActorKind => {
	  use.userQName = dslStoryObject.systemUnderDiscussion.qualifiedName
	}
	case _ => //
      }
    }
    addUse(use)
  }

  private def make_use = {
    val dslUse = new SUse(UUID.randomUUID.toString)
    val use = new SMUse(dslUse)
    use
  }
}
