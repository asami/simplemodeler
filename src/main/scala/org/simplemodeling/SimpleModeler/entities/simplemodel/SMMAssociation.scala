package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @since   Feb.  9, 2009
 *  version Feb. 20, 2009
 * @version Oct.  2, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMAssociation(val name: String, val associationType: SMMObjectType) extends SMMRelationship {
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMAssociation = {
    multiplicity = aMultiplicity
    this
  }
}
