package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * @since   Dec. 20, 2008
 * @version Nov. 13, 2010
 * @author  ASAMI, Tomoharu
 */
class DomainState(aName: String) extends SState(aName) {
  def this() = this(null)

  def transition(event: => DomainEvent, state: => DomainState): STransition = {
    transit_event(event, state)
  }

  def transition(event: => DomainEvent, state: => DomainState, guard: String): STransition = {
    val transition = transit_event(event, state)
    transition.guard = guard
    transition
  }

  def transit(event: => DomainEvent, state: => DomainState): STransition = {
    transit_event(event, state)
  }

  def state(aState: DomainState): DomainState = {
    if (subStateMap.contains(aState.name)) {
      error("duplicate name = " + aState.name) // syntax error
    }
    subStateMap.put(aState.name, aState)
    aState
  }
}
