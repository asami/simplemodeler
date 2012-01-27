package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc.SDoc

/*
 * Oct. 24, 2008
 * Oct. 24, 2008
 */
class SMAssociationType(val dslAssociationType: SEntity) {
  def name = dslAssociationType.name
  def packageName = dslAssociationType.packageName
  def qualifiedName = dslAssociationType.qualifiedName
  def summary = dslAssociationType.summary
  var typeObject: SMObject = SMNullObject
}
