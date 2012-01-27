package org.simplemodeling.dsl

/*
 * Sep. 23, 2008
 * Jan. 13, 2009
 */
class SAttributeCandidate(val name: String, val attributes: SAttributeSet) {
  var done = false
  var kind: SAttributeKind = NullAttributeKind

  def apply(aName: String, aValue: SAttributeType): SAttribute = {
    attributes.create(this, aName, aValue)
  }

  def apply(aName: String, aValue: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    attributes.create(this, aName, aValue, aMultiplicity)
  }

  // from SPropertyProxy
  final def create(aName: String) =
    attributes.create(this, aName)

  final def create(aName: String, aMultiplicity: SMultiplicity) =
    attributes.create(this, aName, aMultiplicity)

  // from SAttributeProxy
  final def create(aValue: SAttributeType) =
    attributes.create(this, name, aValue)

  final def create(aValue: SAttributeType, aMultiplicity: SMultiplicity) =
    attributes.create(this, name, aValue, aMultiplicity)

  // from SObject
  def is(aValue: SAttributeType): SAttribute = {
    attributes.create(this, aValue)
  }

  def is_a(aValue: SAttributeType): SAttribute = {
    attributes.create(this, aValue, One)
  }

  def is_one(aValue: SAttributeType): SAttribute = {
    attributes.create(this, aValue, One)
  }

  def is_zero_one(aValue: SAttributeType): SAttribute = {
    attributes.create(this, aValue, ZeroOne)
  }

  def is_one_more(aValue: SAttributeType): SAttribute = {
    attributes.create(this, aValue, OneMore)
  }

  def is_zero_more(aValue: SAttributeType): SAttribute = {
    attributes.create(this, aValue, ZeroMore)
  }
}
