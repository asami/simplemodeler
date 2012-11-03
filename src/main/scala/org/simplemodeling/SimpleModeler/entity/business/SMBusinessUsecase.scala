package org.simplemodeling.SimpleModeler.entity.business

import scala.collection.mutable.{Buffer, ArrayBuffer, HashMap}
import java.util.UUID
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.simplemodeling.SimpleModeler.entity.util.BasicFlowCounter
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._
import org.goldenport.value.{GTree, GTreeNode, GTreeVisitor, PlainTree, GTreeCursor}
import org.goldenport.value.util.TreeNodeCounter
import com.asamioffice.goldenport.text.UPathString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.util.StepFlowBuilder

/*
 * @since   Nov.  8, 2008
 *  version Nov.  4, 2011
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
class SMBusinessUsecase(val dslBusinessUsecase: BusinessUsecase) extends SMUsecase(dslBusinessUsecase) {
  override def typeName: String = "business usecase"

  val includeBusinessTasks = new ArrayBuffer[SMBusinessTask] // XXX duplicate
  val includeBusinessUsecases = new ArrayBuffer[SMBusinessUsecase]
  val userBusinessUsecases = new ArrayBuffer[SMBusinessUsecase]

  override final def new_StepFlowBuilder(
    root: GTreeNode[SMStep],
    dslStoryObject: SStoryObject,
    registerMark: SMStep => Unit,
    addUse: SMUse => Unit): StepFlowBuilder = {
    new BusinessStepFlowBuilder(root, dslStoryObject, registerMark, addUse)
  }

  final def getBusinessTaskSteps: Seq[SMBusinessTaskStep] = {
//    val x = basicFlow.map(_.asInstanceOf[SMBusinessTaskStep])
//    val z = x.filter { x =>
//      val y = x.dslBusinessTask
//      x.dslBusinessTask != NullBusinessTask
//    }
    basicFlow.map(_.asInstanceOf[SMBusinessTaskStep]).filter(_.dslBusinessTask != NullBusinessTask)
  }

  final def getBusinessUsecaseSteps: Seq[SMBusinessUsecaseStep] = {
    val usecaseSteps = new ArrayBuffer[SMBusinessUsecaseStep]
    for (taskStep <- basicFlow) {
      taskStep.traverse(new GTreeVisitor[SMElement] {
	override def enter(aNode: GTreeNode[SMElement]) {
	  aNode.content match {
	    case step: SMBusinessUsecaseStep => usecaseSteps += step
	    case _ => //
	  }
	}
      })
    }
    usecaseSteps
  }

  final def includeTasksLiteral: SDoc = {
    objects_literal(includeBusinessTasks)
  }

  final def includeUsecasesLiteral: SDoc = {
    objects_literal(includeBusinessUsecases)
  }

  final def userUsecasesLiteral: SDoc = {
    objects_literal(userBusinessUsecases)
  }
}

object NullSMBusinessUsecase extends SMBusinessUsecase(NullBusinessUsecase)
