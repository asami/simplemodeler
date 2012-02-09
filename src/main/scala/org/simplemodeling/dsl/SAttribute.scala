package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc.SDoc

/**
 * @since   Sep. 10, 2008
 *  version Nov. 13, 2010
 *  version Dec. 15, 2011
 * @version Feb.  9, 2012
 * @author  ASAMI, Tomoharu
 */
class SAttribute(aName: String, aMultiplicity: SMultiplicity) extends SElement(aName) {
  type Descriptable_TYPE = SAttribute
  type Historiable_TYPE = SAttribute
  var attributeType: SAttributeType = UnknownValue
  var multiplicity: SMultiplicity = aMultiplicity
  var kind: SAttributeKind = NullAttributeKind
  var idPolicy: SIdPolicy = ApplicationIdPolicy
  var isPersistent: Boolean = true
  var defaultValue: Any = _
  var constraints = new ArrayBuffer[SConstraint]

  final def isId = {
    kind == IdAttributeKind
  }

/*
  // from SPropertyProxy
  final def changeName(aName: String) = {
    change_name(aName)
  }

  def apply(aName: String, aValue: SAttributeType): SAttribute = {
    changeName(aName)
    attributeType = aValue
    this
  }

  def apply(aName: String, aValue: SAttributeType, aMultiplicity: SMultiplicity): SAttribute = {
    changeName(aName)
    attributeType = aValue
    multiplicity = aMultiplicity
    this
  }
*/

  def by(aTarget: SAttributeType): SAttribute = {
    attributeType = aTarget
    this
  }

/*
  def of(aTarget: SAttributeType): SAttribute = {
    this
  }
*/

  final def kind_is(aKind: SAttributeKind): SAttribute = {
    kind = aKind
    this
  }

  final def idPolicy_is(aPolicy: SIdPolicy): SAttribute = {
    idPolicy = aPolicy
    this
  }

  final def persistent_is(aPersistent: Boolean): SAttribute = {
    isPersistent = aPersistent
    this
  }

  final def defaultValue_is(aValue: Any): SAttribute = {
    defaultValue = aValue
    this
  }

  def withConstraint(c: SConstraint): SAttribute = {
    constraints += c
    this
  }

  def <<(c: SConstraint): SAttribute = {
    constraints += c
    this
  }    
}
