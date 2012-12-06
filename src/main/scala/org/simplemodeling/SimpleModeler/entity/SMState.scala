package org.simplemodeling.SimpleModeler.entity

import org.apache.commons.lang3.StringUtils
import scalaz._, Scalaz._
import scala.collection.mutable.{ArrayBuffer, LinkedHashMap}
import org.simplemodeling.dsl._
import org.goldenport.value._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * @since   Dec. 24, 2008
 *  version Mar. 19, 2009
 *  version Nov. 26, 2012
 * @version Dec.  6, 2012
 * @author  ASAMI, Tomoharu
 * ASAMI, Tomoharu
 */
class SMState(val dslState: SState, ownerStateMachine: SMStateMachine) extends SMElement(dslState) {
  val transitions = dslState.transitions.map(new SMTransition(_, ownerStateMachine))
  val subStateMap = new LinkedHashMap[String, SMState]
  def subStates = subStateMap.values.toList
  for ((name, dslSubState) <- dslState.subStateMap) {
    val qName = dslSubState.qualifiedName
    val refinedDslSubState = SStateRepository.getState(qName)
    val state = new SMState(refinedDslSubState, ownerStateMachine)
    ownerStateMachine.wholeStateMap += (qName -> state)
    subStateMap += (name -> state) // owner composition state
  }

  val value: Either[String, Int] = {
    val v = dslState.value | name
    val r = if (StringUtils.isBlank(v)) {
      name.left
    } else {
      v.parseInt.fold(_ => name.left, _.right)
    }
//    println("SMState: " + dslState + "/" + r)
    r
  }

  override protected def qualified_Name = {
//     println("state qname = " + dslState.qualifiedName) 2009-03-19
    Some(dslState.qualifiedName)
  }

  final def isTerminal = {
    dslState.transitions.isEmpty
  }

  final def isComposite = {
    !subStateMap.isEmpty
  }

/*
  final def getEventsWithGuardActions: LinkedHashMap[SMObject, Seq[(SMGuard, SMAction)]] = {
    val eventMap = new LinkedHashMap[SMObject, ArrayBuffer[(SMGuard, SMAction)]]
    for (transition <- transitions) {
      val event = transition.event
      eventMap.get(event) match {
	case Some(guardActions: ArrayBuffer[(SMGuard, SMAction)]) => {
	  guardActions += ((transition.guard, transition.action))
	}
	case None => {
	  val buf = new ArrayBuffer[(SMGuard, SMAction)]
	  buf += ((transition.guard, transition.action))
	  eventMap.put(event, buf)
	}
      }
    }
    eventMap.asInstanceOf[LinkedHashMap[SMObject, Seq[(SMGuard, SMAction)]]]
  }

  final def getEventGuardActions: Seq[(SMObject, SMGuard, SMAction)] = {
    for ((event, guardActions) <- getEventsWithGuardActions.elements.toList;
	 (guard, action) <- guardActions) yield (event, guard, action)
  }
*/
}

object SMNullState extends SMState(NullState, SMNullStateMachine)
