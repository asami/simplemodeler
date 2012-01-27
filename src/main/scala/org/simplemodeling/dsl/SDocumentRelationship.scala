package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Mar. 17, 2009
 * Mar. 17, 2009
 * ASAMI, Tomoharu
 */
class SDocumentRelationship(var aName: String, val document: SDocument) extends SRelationship(aName) {
  type Descriptable_TYPE = SDocumentRelationship
  type Historiable_TYPE = SDocumentRelationship

  def this(aDocument: SDocument) = this(aDocument.term, aDocument)

//  require (document != null)
  target = document
}

object NullDocumentRelationship extends SDocumentRelationship(null, null)
