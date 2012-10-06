package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @since   Jan. 30, 2009
 *  version Feb. 20, 2009
 *  version Mar. 25, 2012
 * @version Oct.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMAttribute(val name: String, val attributeType: SMMObjectType, val id: Boolean) extends SMMSlot {
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMAttribute = {
    multiplicity = aMultiplicity
    this
  }
}
