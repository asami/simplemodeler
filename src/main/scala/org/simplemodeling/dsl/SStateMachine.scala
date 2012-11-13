package org.simplemodeling.dsl

import scala.collection.mutable.{LinkedHashMap, LinkedHashSet, ArrayBuffer}
import org.goldenport.value._
import org.goldenport.sdoc._

/*
 * @since   Dec. 20, 2008
 *  version Mar. 18, 2009
 * @version Nov. 13, 2012
 * ASAMI, Tomoharu
 */
class SStateMachine(aName: String, pkgname: String) extends SObject(aName, pkgname) {
//  type Descriptable_TYPE = SStateMachine
//  type Historiable_TYPE = SStateMachine
  val stateMap = new LinkedHashMap[String, SState]
  protected var _current: SState = null

  if (aName == null) {
    set_name_by_className
  }

  def this() = this(null, null)

  final def transitions: Seq[STransition] = {
    for {
      state <- stateMap.values.toList
      trans <- state.transitions
    } yield trans
  }

  final def getStateNames: Seq[String] = {
    "初期状態" :: stateMap.keys.toList ::: List("終了状態")
  }

  final def getEventQNames: Seq[String] = {
    val events = new LinkedHashSet[String]
    events += "自動"
    for (state <- stateMap.values) {
      for (transition <- state.transitions) {
	events += transition.event.qualifiedName
      }
    }
    events.toList
  }
}

object NullStateMachine extends SStateMachine(null, null)
