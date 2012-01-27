package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Sep. 14, 2008
 * Oct. 22, 2008
 */
class SAttributeProxy(val value: SAttributeType) {
  def is(theAttributes: SAttributeSet): SAttribute = {
    theAttributes.create(value)
  }

  def is_a(theAttributes: SAttributeSet): SAttribute = {
    theAttributes.create(value, One)
  }

  def is_one(theAttributes: SAttributeSet): SAttribute = {
    theAttributes.create(value, One)
  }

  def is_zero_one(theAttributes: SAttributeSet): SAttribute = {
    theAttributes.create(value, ZeroOne)
  }

  def is_one_more(theAttributes: SAttributeSet): SAttribute = {
    theAttributes.create(value, OneMore)
  }

  def is_zero_more(theAttributes: SAttributeSet): SAttribute = {
    theAttributes.create(value, ZeroMore)
  }

  //
  def is(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(value)
  }

  def is_a(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(value, One)
  }

  def is_one(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(value, One)
  }

  def is_zero_one(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(value, One)
  }

  def is_one_more(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(value, OneMore)
  }

  def is_zero_more(aCandidate: SAttributeCandidate): SAttribute = {
    aCandidate.create(value, ZeroMore)
  }

/*
  def is(anAttribute: SAttribute): SAttribute = {
    is_a(anAttribute)
  }

  def is_a(anAttribute: SAttribute): SAttribute = {
    is_one(anAttribute)
  }

  def is_one(anAttribute: SAttribute): SAttribute = {
    anAttribute.multiplicity = One
    anAttribute
  }

  def is_zero_one(anAttribute: SAttribute): SAttribute = {
    anAttribute.multiplicity = ZeroOne
    anAttribute
  }

  def is_one_more(anAttribute: SAttribute): SAttribute = {
    anAttribute.multiplicity = OneMore
    anAttribute
  }

  def is_zero_more(anAttribute: SAttribute): SAttribute = {
    anAttribute.multiplicity = ZeroMore
    anAttribute
  }
*/
}
