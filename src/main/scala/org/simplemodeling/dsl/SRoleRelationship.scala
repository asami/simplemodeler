package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Nov. 19, 2008
 * Feb. 27, 2009
 */
class SRoleRelationship(val role: SRole, aName: String) extends SRelationship(aName) {
  type Descriptable_TYPE = SRoleRelationship
  type Historiable_TYPE = SRoleRelationship

  var multiplicity: SMultiplicity = One

  require (role != null)
  target = role
}
