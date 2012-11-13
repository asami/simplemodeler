package org.simplemodeling.SimpleModeler.entities.simplemodel

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Oct.  8, 2012
 * @version Nov. 13, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMPowertype(val name: String, val powertypeType: SMMPowertypeType) extends SMMSlot {
  /*
   * The powertypeType.instances is old kind registration mechanism.
   * The powertypeKinds will replace with it.
   */
  val powertypeKinds = new ArrayBuffer[SMMPowertypeKind]

  /* TODO
   * Divides powertype relationship and powertype itself.
   */
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMPowertype = {
    multiplicity = aMultiplicity
    this
  }
}
