package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc.SDoc

/*
 * Nov. 22, 2008
 * Nov. 22, 2008
 */
class SMRelationshipType(val dslRelationshipType: SObject) {
  def name = dslRelationshipType.name
  def packageName = dslRelationshipType.packageName
  def qualifiedName = dslRelationshipType.qualifiedName
  def summary = dslRelationshipType.summary
  var typeObject: SMObject = SMNullObject
}
