package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * Dec. 20, 2008
 * Feb. 27, 2009
 */
class SPowertypeSet {
  private val _powertypes = new ArrayBuffer[SPowertypeRelationship]

  def powertypes: Seq[SPowertypeRelationship] = _powertypes

  // from model itself
  def apply(aPowertype: SPowertype): SPowertypeRelationship = {
    create(aPowertype)
  }

  def apply(aPowertype: SPowertype, aMultiplicity: SMultiplicity): SPowertypeRelationship = {
    create(aPowertype, aMultiplicity)
  }

  def apply(aName: String, aPowertype: SPowertype): SPowertypeRelationship = {
    create(aName, aPowertype)
  }

  def apply(aName: String, aPowertype: SPowertype, aMultiplicity: SMultiplicity): SPowertypeRelationship = {
    create(aName, aPowertype, aMultiplicity)
  }

  //
  def create(aPowertype: SPowertype): SPowertypeRelationship = {
    create(aPowertype, One)
  }

  def create(aPowertype: SPowertype, aMultiplicity: SMultiplicity): SPowertypeRelationship = {
    create(reference_name(aPowertype), aPowertype, aMultiplicity)
  }

  def create(aName: String, aPowertype: SPowertype): SPowertypeRelationship = {
    create(aName, aPowertype, One)
  }

  def create(aName: String, aPowertype: SPowertype, aMultiplicity: SMultiplicity): SPowertypeRelationship = {
    val powertype = new SPowertypeRelationship(aPowertype, aName)
    powertype.multiplicity = aMultiplicity
    _powertypes += powertype
    powertype
  }

  //
  private def reference_name(anObject: SObject): String = {
    util.UDsl.makeReferenceName(anObject)
  }
}
