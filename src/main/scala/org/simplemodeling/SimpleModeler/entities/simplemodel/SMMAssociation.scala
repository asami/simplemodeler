package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @since   Feb.  9, 2009
 *  version Feb. 20, 2009
 * @version Oct.  8, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMAssociation(val name: String, val associationType: SMMEntityTypeSet) extends SMMSlot {
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMAssociation = {
    multiplicity = aMultiplicity
    this
  }
}
