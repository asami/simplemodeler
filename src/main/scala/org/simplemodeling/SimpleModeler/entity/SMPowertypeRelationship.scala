package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * Dec. 22, 2008
 * Jan. 20, 2009
 */
class SMPowertypeRelationship(val dslPowertypeRelationship: SPowertypeRelationship) extends SMRelationship(dslPowertypeRelationship) {
  var powertype: SMPowertype = SMNullPowertype
  val multiplicity = new SMMultiplicity(dslPowertypeRelationship.multiplicity)
}
