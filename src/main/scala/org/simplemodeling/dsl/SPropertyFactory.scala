package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Sep. 11, 2008
 * Oct. 22, 2008
 */
class SPropertyFactory(val attributes: SAttributeSet, val associations: SAssociationSet) {
  final def proxy(aName: String): SPropertyProxy = {
    new SPropertyProxy(aName, attributes, associations)
  }

  final def entity(anEntity: SEntity): SAssociationProxy = {
    new SAssociationProxy(anEntity)
  }

  final def value(aValue: SAttributeType): SAttributeProxy = {
    new SAttributeProxy(aValue)
  }

/*
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
*/
}
