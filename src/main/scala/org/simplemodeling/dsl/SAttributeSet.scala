package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Sep. 12, 2008
 * @version Oct. 15, 2009
 * @author  ASAMI, Tomoharu
 */
class SAttributeSet {
  private val _attributes = new ArrayBuffer[SAttribute]
  private val _candidates = new ArrayBuffer[SAttributeCandidate]

  def attributes: Seq[SAttribute] = _attributes

  //
  def apply(aType: SAttributeType): SAttribute = {
    create(aType)
  }

  def apply(aType: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    create(aType, aMultiplicity)
  }

  def apply(aName: String, aType: SAttributeType): SAttribute = {
    create(aName, aType)
  }

  def apply(aName: String, aType: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    create(aName, aType, aMultiplicity)
  }

  def apply(aName: Symbol, aType: SAttributeType): SAttribute = {
    create(aName.name, aType)
  }

  def apply(aName: Symbol, aType: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    create(aName.name, aType, aMultiplicity)
  }

  //
  def create(aValue: SAttributeType): SAttribute = {
    create(aValue, One)
  }

  def create(aValue: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    create(reference_name(aValue), aValue, aMultiplicity)
  }

  def create(aName: String, aValue: SAttributeType): SAttribute = {
    create(aName, aValue, One)
  }

  def create(aName: String, aValue: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    val attr = new SAttribute(aName, aMultiplicity)
    attr.attributeType = aValue
    _attributes += attr
    attr
  }

  def create(aName: String): SAttribute = {
    create(aName, One)
  }

  def create(aName: String, aMultiplicity: SMultiplicity): SAttribute = {
    val attr = new SAttribute(aName, aMultiplicity)
    _attributes += attr
    attr
  }

  def create(aName: Symbol, aValue: SAttributeType): SAttribute = {
    create(aName.name, aValue)
  }

  def create(aName: Symbol, aValue: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    create(aName.name, aValue, aMultiplicity)
  }


  // from SPropertyProxy and SAttributeCandidate
  final def candidate(aName: String): SAttributeCandidate = {
    val candidate = new SAttributeCandidate(aName, this)
    _candidates += candidate
    candidate
  }

  // from SEntity
  final def candidate(aName: String, aKind: SAttributeKind): SAttributeCandidate = {
    val candidate = new SAttributeCandidate(aName, this)
    candidate.kind = aKind
    _candidates += candidate
    candidate
  }

  final def create(aCandidate: SAttributeCandidate, aName: String): SAttribute = {
    create(aCandidate, aName, One)
  }

  final def create(aCandidate: SAttributeCandidate, aName: String, aMultiplicity: SMultiplicity): SAttribute = {
    aCandidate.done = true
    create(aName, aMultiplicity) kind_is aCandidate.kind
  }

  // from SObject via SAttributeCandidate 
  final def create(aCandidate: SAttributeCandidate, aValue: SAttributeType): SAttribute = {
    create(aCandidate, aValue, One)
  }

  final def create(aCandidate: SAttributeCandidate, aValue: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    aCandidate.done = true
    create(aCandidate.name, aValue, aMultiplicity) kind_is aCandidate.kind
  }

  // from ??? via SAttributeCandidate
  final def create(aCandidate: SAttributeCandidate, aName: String, aValue: SAttributeType): SAttribute = {
    create(aCandidate, aName, aValue, One)
  }

  final def create(aCandidate: SAttributeCandidate, aName: String, aValue: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    aCandidate.done = true
    create(aName, aValue, aMultiplicity) kind_is aCandidate.kind
  }

  //
  private def reference_name(anObject: SObject): String = {
    util.UDsl.makeReferenceName(anObject)
  }
}
