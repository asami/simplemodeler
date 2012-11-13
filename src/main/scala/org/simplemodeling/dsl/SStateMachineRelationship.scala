package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * @since   Nov. 14, 2012
 * @version Nov. 14, 2012
 * @author  ASAMI, Tomoharu
 */
class SStateMachineRelationship(val statemachine: SStateMachine, aName: String) extends SRelationship(aName) {
  type Descriptable_TYPE = SStateMachineRelationship
  type Historiable_TYPE = SStateMachineRelationship

//  var multiplicity: SMultiplicity = One

  require (statemachine != null)
  target = statemachine
}
