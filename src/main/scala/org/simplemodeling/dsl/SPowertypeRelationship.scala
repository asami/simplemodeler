package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Dec. 21, 2008
 * Jan. 20, 2009
 */
class SPowertypeRelationship(val powertype: SPowertype, aName: String) extends SRelationship(aName) {
  type Descriptable_TYPE = SPowertypeRelationship
  type Historiable_TYPE = SPowertypeRelationship

  var multiplicity: SMultiplicity = One

  require (powertype != null)
  target = powertype
}
