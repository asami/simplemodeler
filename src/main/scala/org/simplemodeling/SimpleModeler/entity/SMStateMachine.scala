package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.{LinkedHashMap, ArrayBuffer, HashSet}
import org.simplemodeling.dsl._
import org.goldenport.value._
import org.goldenport.sdoc._
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * @since   Dec. 22, 2008
 *  version Mar. 19, 2009
 * @version Nov. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class SMStateMachine(
  val dslStateMachine: SStateMachine
//  , val ownerObject: SMObject
) extends SMObject(dslStateMachine) {
  override def typeName: String = "state machine"
  val wholeStateMap = new LinkedHashMap[String, SMState]
  val stateMap = new LinkedHashMap[String, SMState]
  def states = stateMap.values.toList
  for ((name, dslState) <- dslStateMachine.stateMap) {
    val qName = dslState.qualifiedName
    val refinedDslState = SStateRepository.getState(qName)
    val state = new SMState(refinedDslState, this)
    wholeStateMap += (qName -> state)
    stateMap += (name -> state)
  }

  add_feature(FeatureName, name) label_is "状態機械名"

  final def transitions: Seq[SMTransition] = {
    for (state <- states; trans <- state.transitions) yield trans
  }

  final def getState(aState: SState): SMState = {
    getState(aState.qualifiedName)
  }

  final def getState(qName: String): SMState = {
    wholeStateMap.get(qName) match {
      case Some(state: SMState) => state
      case None => error("syntax error: " + qName)
    }
  }

/*
  final def getState(aState: SState): SMState = {
//    println("SMStateMachine.getState = " + aState.isMaster) 2009-03-01
    if (!stateMap.contains(aState.name)) {
      stateMap.put(aState.name, new SMState(aState, this)) // XXX
    }
    stateMap.get(aState.name).get
  }
*/

  final def isReceiveEvent(anEvent: SMObject): Boolean = {
    def is_receive(state: SMState): Boolean = {
      for (transition <- state.transitions) {
	if (transition.event == anEvent) {
	  return true
	}
      }
      for (subState <- state.subStates) {
	if (is_receive(subState)) return true
      }
      return false
    }

    for (state <- states) {
      if (is_receive(state)) return true
    }
    return false
  }

/*
  final def getStateNames: Seq[String] = {
    "初期状態" :: stateMap.keys.toList ::: List("終了状態")
  }

  final def getEventsQNameRef: (Seq[String], Seq[SDoc]) = {
    val qNames = new ArrayBuffer[String]
    val events = new ArrayBuffer[SDoc]
    qNames += "自動"
    events += "自動"
    for (state <- states) {
      for (transition <- state.transitions) {
	if (!qNames.contains(transition.event.qualifiedName)) {
	  events += new SMObjectRef(transition.event)
	  qNames += transition.event.qualifiedName
	}
      }
    }
    (qNames, events)
  }
*/

  final def getEvents: Seq[SMObject] = {
    val events = new ArrayBuffer[SMObject]

    def build(state: SMState) {
      for (transition <- state.transitions) {
	if (!events.contains(transition.event)) {
	  events += transition.event
	}
      }
      state.subStates.foreach(build)
    }

    states.foreach(build)
    events
  }

  private def make_event_guard_axis_tree: (Seq[(String, String)], GTree[SDoc]) = {
    val events = new LinkedHashMap[SMObject, ArrayBuffer[SMGuard]]

    def build_events(aState: SMState) {
      for (transition <- aState.transitions) {
	if (!events.contains(transition.event)) {
	  events += transition.event -> new ArrayBuffer[SMGuard]
	}
	events(transition.event) += transition.guard
      }
      aState.subStates.foreach(build_events)
    }

    states.foreach(build_events)

    val eventGuardAxis = new ArrayBuffer[(String, String)]
    val tree = new PlainTree[SDoc]
    val root = tree.root
    eventGuardAxis += (("自動", null))
    root.addChild("自動")
    for (event <- events.keys) {
      val child = root.addContent(new SMObjectRef(event))
      for (guard <- events(event)) {
	val qName = event.qualifiedName
	val guardText = if (guard == SMNullGuard) null
			else guard.text
	eventGuardAxis += ((qName, guardText))
	if (guardText != null) {
	  child.addContent(guardText)
	}
      }
    }
    (eventGuardAxis, tree)
  }

  private def make_state_qnames_tree: (Seq[String], GTree[SDoc]) = {
    val qNames = new ArrayBuffer[String]
    val tree = new PlainTree[SDoc]

    def build(aState: SMState, aParent: GTreeNode[SDoc]) {
      if (aState.isComposite) {
	qNames += aState.qualifiedName
//	println("state qname(composite) = " + aState.qualifiedName + "," + aState.name) 2009-03-19
	val current = aParent.addContent(SText(aState.name))
	current.addContent(SText(aState.name))
	val subStates = aState.subStates
	if (subStates.isEmpty) {
	  qNames += "none:"
	} else {
	  qNames += "initState:" + subStates(0).qualifiedName
	}
	current.addContent("初期状態")
	for (state <- subStates) {
	  build(state, current)
	}
	qNames += "none:"
	current.addContent("終了状態")
      } else {
	qNames += aState.qualifiedName
//	println("state qname = " + aState.qualifiedName + "," + aState.name) 2009-03-19
	aParent.addContent(SText(aState.name))
      }
    }

    qNames += "初期状態"
    tree.root.addContent("初期状態")
    for (state <- states) {
      build(state, tree.root)
    }
    qNames += "終了状態"
    tree.root.addContent("終了状態")
    (qNames, tree)
  }

  final def buildStateTransitionTable(aTable: GTable[SDoc]) {
    val (stateQNames, stateTree) = make_state_qnames_tree
    val (eventGuardAxis, eventGuardTree) = make_event_guard_axis_tree

    aTable.setHead(eventGuardTree)
    aTable.setSide(stateTree)
    for (y <- 0 until stateQNames.length) {
      for (x <- 0 until eventGuardAxis.length) {
	val stateQName = stateQNames(y)
	val (eventQName, guard) = eventGuardAxis(x)
	if (stateQName == "初期状態") {
	  if (x == 0) {
	    aTable.put(0, 0, getState(stateQNames(1)).name)
	  } else {
	    aTable.put(x, y, "N/A")
	  }
	} else if (stateQName == "終了状態") {
	  aTable.put(x, y, "N/A")
	} else if (stateQName.startsWith("none:")) {
	  aTable.put(x, y, "N/A")
	} else if (stateQName.startsWith("initState:")) {
	  if (x == 0) {
	    val qName = stateQName.substring("initState:".length)
	    aTable.put(x, y, getState(qName).name)
	  } else {
	    aTable.put(x, y, "N/A")
	  }
	} else {
	  def match_event_guard(transition: SMTransition) = {
	    if (guard == null) {
	      transition.event.qualifiedName == eventQName &&
	      transition.guard == SMNullGuard
	    } else {
	      transition.event.qualifiedName == eventQName &&
	      transition.guard.text == guard
	    }
	  }

	  val state = getState(stateQName)
	  state.transitions.find(match_event_guard) match {
	    case Some(transition: SMTransition) => {
	      aTable.put(x, y, transition.postState.name)
	    }
	    case None => {
	      if (state.isTerminal && x == 0) {
		aTable.put(x, y, "終了状態")
	      } else {
		aTable.put(x, y, "N/A")
	      }
	    }
	  }
	}
      }
    }
  }
}

// object SMNullStateMachine extends SMStateMachine(NullStateMachine, SMNullObject)
object SMNullStateMachine extends SMStateMachine(NullStateMachine)
