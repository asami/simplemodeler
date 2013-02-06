package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.simplemodeling.dsl._

/*
 * @since   Jan. 30, 2009
 *  version Feb. 20, 2009
 *  version Mar. 25, 2012
 *  version Oct. 28, 2012
 * @version Feb.  6, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * The SMMAttribute is converted to a SAttribute
 * in a SMMEntityEntity#_build_attributes method.
 */
class SMMAttribute(
  val name: String,
  val attributeType: SMMAttributeTypeSet,
  var kind: SAttributeKind
) extends SMMSlot {
  var deriveExpression: String = ""

  def id = kind == IdAttributeKind
  def isDeriveAttribute = deriveExpression.nonEmpty

  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMAttribute = {
    multiplicity = aMultiplicity
    this
  }
}
