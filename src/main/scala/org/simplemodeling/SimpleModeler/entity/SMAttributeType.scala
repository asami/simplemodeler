package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.goldenport.sdoc.SDoc

/*
 * @since   Oct. 12, 2008
 * @version Jun. 24, 2009
 * @author  ASAMI, Tomoharu
 */
class SMAttributeType(val dslAttributeType: SAttributeType) {
  def name = dslAttributeType.name
  def packageName = dslAttributeType.packageName
  def qualifiedName = dslAttributeType.qualifiedName
  def summary = dslAttributeType.summary
  var typeObject: SMObject = SMNullObject
  var constraints = new ArrayBuffer[SMConstraint]
}
