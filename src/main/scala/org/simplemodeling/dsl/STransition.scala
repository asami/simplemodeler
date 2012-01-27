package org.simplemodeling.dsl

/*
 * Dec. 20, 2008
 * Mar. 18, 2009
 * ASAMI, Tomoharu
 */
class STransition(val event: SEvent, val preState: SState, val postState: SState) extends SElement {
  var guard: String = ""

// println("STransition = " + event + "," + preState + "," + postState) 2009-03-18

  final def guard_is(aGurad: String): STransition = {
    guard = aGurad
    this
  }
}

object NullTransition extends STransition(NullEvent, NullState, NullState)
