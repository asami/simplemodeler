package org.simplemodeling.SimpleModeler.entity.requirement

import scala.collection.mutable.{Buffer, ArrayBuffer, HashMap}
import java.util.UUID
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.requirement._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._
import org.goldenport.value.{GTree, GTreeNode, GTreeVisitor, PlainTree, GTreeCursor}
import com.asamioffice.goldenport.text.UPathString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessTask
import org.simplemodeling.SimpleModeler.entity.util.StepFlowBuilder

/*
 * @since   Dec. 10, 2008
 *  version Nov.  4, 2011
 * @version Nov.  5, 2012
 * @author  ASAMI, Tomoharu
 */
class SMRequirementUsecase(val dslRequirementUsecase: RequirementUsecase) extends SMUsecase(dslRequirementUsecase) {
  override def typeName: String = ""

  @deprecated("unused?", "0.5")
  val includeRequirementUsecases = new ArrayBuffer[SMRequirementUsecase]
  @deprecated("unused?", "0.5")
  val includeRequirementTasks = new ArrayBuffer[SMRequirementTask]
  @deprecated("unused?", "0.5")
  val userRequirementUsecases = new ArrayBuffer[SMRequirementUsecase]
  @deprecated("unused?", "0.5")
  val userBusinessTasks = new ArrayBuffer[SMBusinessTask]

  override final def new_StepFlowBuilder(
    root: GTreeNode[SMStep],
    dslStoryObject: SStoryObject,
    registerMark: SMStep => Unit,
    addUse: SMUse => Unit): StepFlowBuilder = {
    new RequirementStepFlowBuilder(root, dslStoryObject, registerMark, addUse)
  }

  final def getRequirementTaskSteps: Seq[SMRequirementTaskStep] = {
    basicFlow.map(_.asInstanceOf[SMRequirementTaskStep]).filter(_.dslRequirementTask != NullRequirementTask)
  }

  final def getRequirementUsecaseSteps: Seq[SMRequirementUsecaseStep] = {
    val usecaseSteps = new ArrayBuffer[SMRequirementUsecaseStep]
    for (taskStep <- basicFlow) {
      taskStep.traverse(new GTreeVisitor[SMElement] {
	override def enter(aNode: GTreeNode[SMElement]) {
	  aNode.content match {
	    case step: SMRequirementUsecaseStep => usecaseSteps += step
	    case _ => //
	  }
	}
      })
    }
    usecaseSteps
  }

  final def includeTasksLiteral: SDoc = {
    objects_literal(includeRequirementTasks)
  }

  final def includeUsecasesLiteral: SDoc = {
    objects_literal(includeRequirementUsecases)
  }

  final def userUsecasesLiteral: SDoc = {
    objects_literal(userRequirementUsecases)
  }

  final def userBusinessTasksLiteral: SDoc = {
    objects_literal(userBusinessTasks)
  }
}

object NullSMRequirementUsecase extends SMRequirementUsecase(NullRequirementUsecase)
