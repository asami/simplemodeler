package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @since   Oct.  8, 2012
 * @version Oct.  8, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMPowertype(val name: String, val powertypeType: SMMPowertypeType) extends SMMSlot {
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMPowertype = {
    multiplicity = aMultiplicity
    this
  }
}
