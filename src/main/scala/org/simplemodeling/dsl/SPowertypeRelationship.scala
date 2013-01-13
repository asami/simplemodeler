package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * @since   Dec. 21, 2008
 *  version Jan. 20, 2009
 * @version Jan. 13, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * SMMEntityEntity invokes a instance of SPowertypeSet as powertype attribute
 * in SObject.
 * SObject creates SPowertypeRelationship in SPowertypeSet.
 */
class SPowertypeRelationship(val powertype: SPowertype, aName: String) extends SRelationship(aName) {
  type Descriptable_TYPE = SPowertypeRelationship
  type Historiable_TYPE = SPowertypeRelationship

  var multiplicity: SMultiplicity = One
  var isInheritancePowertype: Boolean = false

  require (powertype != null)
  target = powertype
}
