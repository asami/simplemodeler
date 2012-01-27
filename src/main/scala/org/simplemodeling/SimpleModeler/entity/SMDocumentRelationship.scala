package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * Mar. 17, 2009
 * Mar. 17, 2009
 * ASAMI, Tomoharu
 */
class SMDocumentRelationship(val dslDocumentRelationship: SDocumentRelationship) extends SMRelationship(dslDocumentRelationship) {
  var document: SMDocument = SMNullDocument
}
