package org.simplemodeling.SimpleModeler.entities.simplemodel

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Oct.  8, 2012
 *  version Nov. 13, 2012
 * @version Jan. 13, 2013
 * @author  ASAMI, Tomoharu
 */
class SMMPowertype(val name: String, val powertypeType: SMMPowertypeType) extends SMMSlot {
  /*
   * The powertypeType.instances is old kind registration mechanism.
   * The powertypeKinds will replace with it.
   */
  val powertypeKinds = new ArrayBuffer[SMMPowertypeKind]
  var isInheritancePowertype: Boolean = false

  /* TODO
   * Divides powertype relationship and powertype itself.
   */
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMPowertype = {
    multiplicity = aMultiplicity
    this
  }

  def withInheritancePowertype(v: Boolean) = {
    isInheritancePowertype = v
    this
  }
}
