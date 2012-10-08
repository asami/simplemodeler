package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @since   Oct.  8, 2012
 * @version Oct.  8, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMStateMachine(val name: String, val statemachineType: SMMStateMachineType) extends SMMSlot {
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMStateMachine = {
    multiplicity = aMultiplicity
    this
  }
}
