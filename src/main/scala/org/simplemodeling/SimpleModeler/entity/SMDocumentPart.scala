package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * Jan.  9, 2009
 * Jan. 19, 2009
 */
trait SMDocumentPart extends SMDocument {
}

object SMNullDocumentPart extends SMDocument(NullDocument) with SMDocumentPart
