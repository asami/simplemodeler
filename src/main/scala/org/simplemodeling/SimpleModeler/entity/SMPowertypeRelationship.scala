package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * @since   Dec. 22, 2008
 *  version Jan. 20, 2009
 *  version Nov. 24, 2012
 * @version Jan. 13, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * SMObject creates SMPowertypeRelationship.
 */
class SMPowertypeRelationship(val dslPowertypeRelationship: SPowertypeRelationship) extends SMRelationship(dslPowertypeRelationship) {
  var powertype: SMPowertype = SMNullPowertype
  val multiplicity = new SMMultiplicity(dslPowertypeRelationship.multiplicity)
  def isEntityReference = powertype.isKnowledge
  def isInheritancePowertype: Boolean = dslPowertypeRelationship.isInheritancePowertype
}
