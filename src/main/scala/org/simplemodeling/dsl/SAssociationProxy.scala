package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Sep. 14, 2008
 * Oct. 22, 2008
 */
class SAssociationProxy(val entity: SEntity) {
  def is(theAssociations: SAssociationSet): SAssociation = {
    theAssociations.create(entity)
  }

  def is_a(theAssociations: SAssociationSet): SAssociation = {
    theAssociations.create(entity, One)
  }

  def is_one(theAssociations: SAssociationSet): SAssociation = {
    theAssociations.create(entity, One)
  }

  def is_zero_one(theAssociations: SAssociationSet): SAssociation = {
    theAssociations.create(entity, ZeroOne)
  }

  def is_one_more(theAssociations: SAssociationSet): SAssociation = {
    theAssociations.create(entity, OneMore)
  }

  def is_zero_more(theAssociations: SAssociationSet): SAssociation = {
    theAssociations.create(entity, ZeroMore)
  }

  //
  def is(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create()
  }

  def is_a(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(One)
  }

  def is_one(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(One)
  }

  def is_zero_one(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(One)
  }

  def is_one_more(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(OneMore)
  }

  def is_zero_more(aCandidate: SAssociationCandidate): SAssociation = {
    aCandidate.create(ZeroMore)
  }
}
