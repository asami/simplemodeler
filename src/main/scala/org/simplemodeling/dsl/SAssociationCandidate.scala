package org.simplemodeling.dsl

/*
 * @since   Sep. 23, 2008
 *  version Oct. 19, 2008
 *  version Dec. 13, 2012
 * @version Feb.  7, 2013
 * @author  ASAMI, Tomoharu
 */
class SAssociationCandidate(val name: String, val associations: SAssociationSet) {
  var done = false

  def apply(aName: String, anEntity: SEntity): SAssociation = {
    associations.create(this, aName, anEntity)
  }

  def apply(aName: String, anEntity: SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    associations.create(this, aName, anEntity, aMultiplicity)
  }

  def apply(aName: String, anEntity: SEntity, aMultiplicity: SMultiplicity, kind: SAssociationKind, displayseq: Int): SAssociation = {
    associations.create(this, aName, anEntity, aMultiplicity, kind, displayseq)
  }

  // from SPropertyProxy
  final def create(aName: String) =
    associations.create(this, aName)
  final def create(aName: String, aMultiplicity: SMultiplicity) =
    associations.create(this, aName, aMultiplicity)

  // from SEntityProxy
  final def create() =
    associations.create(this, name)

  final def create(aMultiplicity: SMultiplicity) =
    associations.create(this, name, aMultiplicity)

  // from SObject
  def is(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity)
  }

  def is_a(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, One)
  }

  def is_one(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, One)
  }

  def is_zero_one(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, ZeroOne)
  }

  def is_one_more(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, OneMore)
  }

  def is_zero_more(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, ZeroMore)
  }

  // from SObject
  def is_part(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity) composition_is true
  }

  def is_a_part(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, One) composition_is true
  }

  def is_one_part(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, One) composition_is true
  }

  def is_zero_one_parts(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, ZeroOne) composition_is true
  }

  def is_one_more_parts(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, OneMore) composition_is true
  }

  def is_zero_more_parts(anEntity: SEntity): SAssociation = {
    associations.create(this, anEntity, ZeroMore) composition_is true
  }
}
