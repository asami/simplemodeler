package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.value._
import org.goldenport.sdoc._
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * Dec. 24, 2008
 * Mar. 18, 2009
 * ASAMI, Tomoharu
 */
class SMTransition(val dslTransition: STransition, val ownerStateMachine: SMStateMachine) extends SMElement(dslTransition) {
  var resource: SMObject = SMNullObject
  var event: SMObject = SMNullObject
  var preState: SMState = SMNullState
  var postState: SMState = SMNullState
  var guard = if (dslTransition.guard == "") SMNullGuard
              else new SMGuard(dslTransition.guard)
  var action: SMAction = SMNullAction

// println("SMTransition = " + dslTransition.event + "," + dslTransition.preState + "," + dslTransition.postState) 2009-03-18

  final def resourceLiteral: SDoc = {
    new SMObjectRef(resource)
  }

  final def eventLiteral: SDoc = {
    SMObjectRef(dslTransition.event)
  }

  final def preStateLiteral: SDoc = {
    preState.name
  }

  final def postStateLiteral: SDoc = {
    postState.name
  }

  final def guardLiteral: SDoc = {
    require (guard != null)
    if (guard == SMNullGuard) "N/A"
    else guard.text
  }

  final def actionLiteral: SDoc = {
    action.text
  }
}
