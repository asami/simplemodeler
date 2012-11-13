package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * @since   Dec. 20, 2008
 *  version Nov. 13, 2010
 * @version Nov. 13, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DomainStateMachine(aName: String, pkgname: String) extends SStateMachine(aName, pkgname) {
  def this() = this(null, null)

  def state(aName: String)(theDefinitions: => Unit): DomainState = {
    val s = new DomainState(aName)
//    s.objectScope = true
    _current = s
    theDefinitions
    _current = null
    state(s)
  }

  def state(aState: DomainState): DomainState = {
    if (stateMap.contains(aState.name)) {
      error("duplicate name = " + aState.name) // syntax error
    }
    stateMap.put(aState.name, aState)
    aState
  }
}
