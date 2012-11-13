package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * @since   Nov. 14, 2012
 * @version Nov. 14, 2012
 * @author  ASAMI, Tomoharu
 */
class SMStateMachineRelationship(val dslStateMachineRelationship: SStateMachineRelationship) extends SMRelationship(dslStateMachineRelationship) {
  var statemachine: SMStateMachine = SMNullStateMachine
}
