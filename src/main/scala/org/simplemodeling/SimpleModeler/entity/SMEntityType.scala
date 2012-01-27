package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc.SDoc

/*
 * @since   Mar. 21, 2011
 * @version Mar. 22, 2011
 * @author  ASAMI, Tomoharu
 */
class SMEntityType(val dslEntityType: SEntity) {
  def name = dslEntityType.name
  def packageName = dslEntityType.packageName
  def qualifiedName = dslEntityType.qualifiedName
  def summary = dslEntityType.summary
  var typeObject: SMObject = SMNullObject
}
