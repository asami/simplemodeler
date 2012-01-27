package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * Feb.  9, 2009
 * Feb. 20, 2009
 */
class SMMAssociation(val name: String, val associationType: SMMObjectType) {
  var multiplicity: GRMultiplicity = GROne

  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMAssociation = {
    multiplicity = aMultiplicity
    this
  }
}
