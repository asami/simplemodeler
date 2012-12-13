package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Nov. 18, 2008
 *  version Feb. 27, 2009
 * @version Dec. 13, 2012
 * @author  ASAMI, Tomoharu
 */
class SRoleSet {
  private val _roles = new ArrayBuffer[SRoleRelationship]

  def roles: Seq[SRoleRelationship] = _roles

  // from model itself
  def apply(aRole: SRole): SRoleRelationship = {
    create(aRole)
  }

  def apply(aName: String, aRole: SRole): SRoleRelationship = {
    create(aName, aRole)
  }

  def apply(aName: String, aRole: SRole, aMultiplicity: SMultiplicity): SRoleRelationship = {
    create(aName, aRole, aMultiplicity)
  }

  //
  def create(aRole: SRole): SRoleRelationship = {
    create(aRole, One)
  }

  def create(aRole: SRole, aMultiplicity: SMultiplicity): SRoleRelationship = {
    create(reference_name(aRole), aRole, aMultiplicity)
  }

  def create(aName: String, aRole: SRole): SRoleRelationship = {
    create(aName, aRole, One)
  }

  def create(aName: String, aRole: SRole, aMultiplicity: SMultiplicity, dispseq: Int = SConstants.DEFAULT_DISPLAY_SEQUENCE): SRoleRelationship = {
    val role = new SRoleRelationship(aRole, aName)
    role.multiplicity = aMultiplicity
    role.displaySequence = dispseq
    _roles += role
    role
  }

  //
  private def reference_name(anObject: SObject): String = {
    util.UDsl.makeReferenceName(anObject)
  }
}
