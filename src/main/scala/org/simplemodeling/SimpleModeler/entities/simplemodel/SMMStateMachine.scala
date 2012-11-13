package org.simplemodeling.SimpleModeler.entities.simplemodel

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Oct.  8, 2012
 * @version Nov. 13, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMStateMachine(val name: String, val statemachineType: SMMStateMachineType) extends SMMSlot {
  /* TODO
   * The statemachineType.states is old state registration mechanism.
   * The stateMachineStates will replace with it.
   */
  val stateMachineStates = new ArrayBuffer[SMMStateMachineState]

  /* TODO
   * Divides state machine relationship and state machine itself.
   */
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMStateMachine = {
    multiplicity = aMultiplicity
    this
  }
}
