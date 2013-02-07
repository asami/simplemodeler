package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.simplemodeling.dsl.SAssociationKind

/*
 * @since   Feb.  9, 2009
 *  version Feb. 20, 2009
 *  version Oct.  8, 2012
 * @version Feb.  7, 2013
 * @author  ASAMI, Tomoharu
 */
class SMMAssociation(
  val name: String,
  val associationType: SMMEntityTypeSet,
  val kind: SAssociationKind
) extends SMMSlot {
  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMAssociation = {
    multiplicity = aMultiplicity
    this
  }
}
