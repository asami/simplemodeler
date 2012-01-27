package org.simplemodeling.SimpleModeler.entity.domain

import scala.collection.mutable.ArrayBuffer
import org.goldenport.value._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._

/*
 * Jan.  9, 2009
 * Jan. 19, 2009
 */
class SMDomainDocumentPart(val dslDomainDocumentPart: DomainDocumentPart) extends SMDomainDocument(dslDomainDocumentPart) with SMDocumentPart {
}
