package org.simplemodeling.SimpleModeler.entity.util

import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.value._
import org.goldenport.values.LayeredSequenceNumber
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Dec. 16, 2008
 * @version Apr. 17, 2011
 * @author  ASAMI, Tomoharu
 */
class StepFlowPrinter(val root: GTreeNode[SMStep]) {
  root.traverse(new GTreeVisitor[SMStep] {
    override def start(aNode: GTreeNode[SMStep]) {
      println("<StepFlow>: " + aNode.content)
    }

    override def end(aNode: GTreeNode[SMStep]) {
      println("</StepFlow>")
    }

    override def enter(aNode: GTreeNode[SMStep]) {
      aNode.content match {
	case step: SMBusinessTaskStep => {
	  println("<SMBusinessTaskStep>: " + step)
	}
	case step: SMExtendBusinessUsecaseStep => {
	  println("<SMExtendBusinessUsecaseStep>: " + step)
	}
	case step: SMBusinessUsecaseStep => {
	  println("<SMBusinessTaskStep>: " + step)
	}
	case step: SMActionStep => {
	  println("<SMActionStep>: " + step)
	}
	case step: SMExecutionStep => {
	  println("<SMExecutionStep>:" + step)
	}
	case step: SMInvocationStep => {
	  println("<SMInvocationStep>:" + step)
	}
      }
    }

    override def leave(aNode: GTreeNode[SMStep]) {
      aNode.content match {
	case step: SMBusinessTaskStep => {
	  println("</SMBusinessTaskStep>")
	}
	case step: SMExtendBusinessUsecaseStep => {
	  println("</SMExtendBusinessUsecaseStep")
	}
	case step: SMBusinessUsecaseStep => {
	  println("</SMBusinessTaskStep>")
	}
	case step: SMActionStep => {
	  println("</SMActionStep>")
	}
	case step: SMExecutionStep => {
	  println("</SMExecutionStep>")
	}
	case step: SMInvocationStep => {
	  println("</SMInvocationStep>")
	}
      }
    }
  })
}
