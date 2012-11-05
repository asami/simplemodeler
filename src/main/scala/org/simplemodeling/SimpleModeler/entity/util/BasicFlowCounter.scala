package org.simplemodeling.SimpleModeler.entity.util

import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.value._
import org.goldenport.values.LayeredSequenceNumber
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Dec.  6, 2008
 *  version Apr. 17, 2011
 * @version Nov.  5, 2012
 * @author  ASAMI, Tomoharu
 */
class BasicFlowCounter(val root: GTreeNode[SMStep]) {
  private var _seqNumber = 0
  private var _enterLeaveSeqNumber = 0
  private val _taskSteps = new ArrayBuffer[SMTaskStep]

  for (child <- root.children if child.isInstanceOf[SMTaskStep]) {
    _taskSteps += child.asInstanceOf[SMTaskStep]
  }
  root.traverse(new GTreeVisitor[SMStep] {
    override def enter(aNode: GTreeNode[SMStep]) {
      def count_node(aStep: SMStep) {
	_seqNumber += 1
	_enterLeaveSeqNumber += 1
	aStep.sequenceNumber = _seqNumber
	aStep.layeredSequenceNumber = calc_layeredSequenceNumber(aNode.asInstanceOf[GTreeNode[SMStep]])
	aStep.enterSequenceNumber = _enterLeaveSeqNumber
      }

      def count_enter(aStep: SMStep) {
	count_node(aStep)
      }

      aNode.content match {
	case step: SMTaskStep => //
	case step: SMExtendUsecaseStep => count_node(step)
	case step: SMUsecaseStep => count_node(step)
	case step: SMActionStep => count_node(step)
	case step: SMExecutionStep => count_node(step)
	case step: SMInvocationStep => {
	  if (step.isBidirectional) {
	    count_enter(step)
	  } else {
	    count_node(step)
	  }
	}
	case step: SMParameterStep => count_node(step)
      }
    }

    override def leave(aNode: GTreeNode[SMStep]) {
      def count_leave(aStep: SMStep) {
	_enterLeaveSeqNumber += 1
	aStep.leaveSequenceNumber = _enterLeaveSeqNumber
      }

      aNode.content match {
	case step: SMTaskStep => //
	case step: SMExtendUsecaseStep => //
	case step: SMUsecaseStep => //
	case step: SMActionStep => //
	case step: SMExecutionStep => //
	case step: SMInvocationStep => {
	  if (step.isBidirectional) {
	    count_leave(step)
	  }
	}
	case step: SMParameterStep => //
      }
    }
  })

  private def calc_layeredSequenceNumber(aNode: GTreeNode[SMStep]): LayeredSequenceNumber = {
    def calc_number(aNode: GTreeNode[SMStep]): (Int, Boolean) = {
      if (_taskSteps.contains(aNode.parent)) {
	if (_taskSteps.length > 1) {
	  var index = 0
	  var done = false
	  var i = 0
	  val length = _taskSteps.length
	  while (i < length && !done) {
	    val root = _taskSteps(i)
	    if (root == aNode.parent) {
	      index += aNode.parent.indexOf(aNode) + 1
	      done = true
	    } else {
	      index += root.length
	    }
	    i += 1
	  }
	  (index, false)
	} else {
	  (aNode.parent.indexOf(aNode) + 1, false)
	}
      } else {
        if (aNode != null && aNode.parent != null)
	  (aNode.parent.indexOf(aNode) + 1, true)
        else
          (0, false)
      }
    }

    def calc_number_list(aNode: GTreeNode[SMStep], aList: List[Int]): List[Int] = {
      calc_number(aNode) match {
	case (0, false) => aList
	case (n, false) => n :: aList
	case (n, true) => n :: calc_number_list(aNode.parent, aList)
      }
    }

    new LayeredSequenceNumber(calc_number_list(aNode, Nil).reverse)
  }
}
