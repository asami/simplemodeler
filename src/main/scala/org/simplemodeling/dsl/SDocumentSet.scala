package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * Mar. 17, 2009
 * Mar. 17, 2009
 * ASAMI, Tomoharu
 */
class SDocumentSet(val isMaster: Boolean) {
  private val _documents = new ArrayBuffer[SDocumentRelationship]

  def documents: Seq[SDocumentRelationship] = _documents

  // from model itself
  def apply(aDocument: => SDocument): SDocumentRelationship = {
    if (isMaster) {
      create(aDocument)
    } else {
      NullDocumentRelationship
    }
  }

  //
  def create(aDocument: => SDocument): SDocumentRelationship = {
    if (isMaster) {
      val document = new SDocumentRelationship(aDocument)
      _documents += document
      document
    } else {
      NullDocumentRelationship
    }
  }
}
