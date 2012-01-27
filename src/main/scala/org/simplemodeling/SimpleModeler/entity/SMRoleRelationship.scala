package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * Nov. 20, 2008
 * Feb. 27, 2009
 */
class SMRoleRelationship(val dslRoleRelationship: SRoleRelationship) extends SMRelationship(dslRoleRelationship) {
  var role: SMRole = SMNullRole
  val multiplicity = new SMMultiplicity(dslRoleRelationship.multiplicity)
}
