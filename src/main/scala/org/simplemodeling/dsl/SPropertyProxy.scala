package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Sep. 12, 2008
 * Oct. 18, 2008
 */
class SPropertyProxy(
  val name: String,
  val attributes: SAttributeSet,
  val associations: SAssociationSet) {

  def is(anEntity: SEntity): SAssociation = {
    associations.create(name, anEntity)
  }

  def is_a(anEntity: SEntity): SAssociation = {
    associations.create(name, anEntity, One)
  }

  def is_one(anEntity: SEntity): SAssociation = {
    associations.create(name, anEntity, One)
  }

  def is_zero_one(anEntity: SEntity): SAssociation = {
    associations.create(name, anEntity, ZeroOne)
  }

  def is_one_more(anEntity: SEntity): SAssociation = {
    associations.create(name, anEntity, OneMore)
  }

  def is_zero_more(anEntity: SEntity): SAssociation = {
    associations.create(name, anEntity, ZeroMore)
  }

  def is(aTarget: SAttributeSet): SAttribute = {
    attributes.create(name)
  }

  def is_a(aTarget: SAttributeSet): SAttribute = {
    attributes.create(name, One)
  }

  def is_one(aTarget: SAttributeSet): SAttribute = {
    attributes.create(name, One)
  }

  def is_zero_one(aTarget: SAttributeSet): SAttribute = {
    attributes.create(name, ZeroOne)
  }

  def is_one_more(aTarget: SAttributeSet): SAttribute = {
    attributes.create(name, OneMore)
  }

  def is_zero_more(aTarget: SAttributeSet): SAttribute = {
    attributes.create(name, ZeroMore)
  }

  def is(aTarget: SAssociationSet): SAssociation = {
    associations.create(name)
  }

  def is_a(aTarget: SAssociationSet): SAssociation = {
    associations.create(name, One)
  }

  def is_one(aTarget: SAssociationSet): SAssociation = {
    associations.create(name, One)
  }

  def is_zero_one(aTarget: SAssociationSet): SAssociation = {
    associations.create(name, ZeroOne)
  }

  def is_one_more(aTarget: SAssociationSet): SAssociation = {
    associations.create(name, OneMore)
  }

  def is_zero_more(aTarget: SAssociationSet): SAssociation = {
    associations.create(name, ZeroMore)
  }

  //
  def is(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(name)
  }

  def is_a(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(name, One)
  }

  def is_one(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(name, One)
  }

  def is_zero_one(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(name, ZeroOne)
  }

  def is_one_more(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(name, OneMore)
  }

  def is_zero_more(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(name, ZeroMore)
  }

  //
  def is(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(name)
  }

  def is_a(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(name, One)
  }

  def is_one(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(name, One)
  }

  def is_zero_one(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(name, ZeroOne)
  }

  def is_one_more(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(name, OneMore)
  }

  def is_zero_more(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(name, ZeroMore)
  }

/*
  //
  def is(anAssociation: SAssociation): SAssociation = {
    anAssociation.changeName(name)
    anAssociation
  }

  def is_a(anAssociation: SAssociation): SAssociation = {
    anAssociation.changeName(name)
    anAssociation.multiplicity = One
    anAssociation
  }

  def is_one(anAssociation: SAssociation): SAssociation = {
    anAssociation.changeName(name)
    anAssociation.multiplicity = One
    anAssociation
  }

  def is_zero_one(anAssociation: SAssociation): SAssociation = {
    anAssociation.changeName(name)
    anAssociation.multiplicity = ZeroOne
    anAssociation
  }

  def is_one_more(anAssociation: SAssociation): SAssociation = {
    anAssociation.changeName(name)
    anAssociation.multiplicity = OneMore
    anAssociation
  }

  def is_zero_more(anAssociation: SAssociation): SAssociation = {
    anAssociation.changeName(name)
    anAssociation.multiplicity = ZeroMore
    anAssociation
  }

  //
  def is(anAttribute: SAttribute): SAttribute = {
    anAttribute.changeName(name)
    anAttribute
  }

  def is_a(anAttribute: SAttribute): SAttribute = {
    anAttribute.changeName(name)
    anAttribute.multiplicity = One
    anAttribute
  }

  def is_one(anAttribute: SAttribute): SAttribute = {
    anAttribute.changeName(name)
    anAttribute.multiplicity = One
    anAttribute
  }

  def is_zero_one(anAttribute: SAttribute): SAttribute = {
    anAttribute.changeName(name)
    anAttribute.multiplicity = ZeroOne
    anAttribute
  }

  def is_one_more(anAttribute: SAttribute): SAttribute = {
    anAttribute.changeName(name)
    anAttribute.multiplicity = OneMore
    anAttribute
  }

  def is_zero_more(anAttribute: SAttribute): SAttribute = {
    anAttribute.changeName(name)
    anAttribute.multiplicity = ZeroMore
    anAttribute
  }
*/
}
