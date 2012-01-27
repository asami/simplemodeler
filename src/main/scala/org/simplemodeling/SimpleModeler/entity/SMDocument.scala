package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * Sep. 15, 2008
 * Jan. 19, 2009
 */
class SMDocument(val dslDocument: SDocument) extends SMObject(dslDocument) {
  override def typeName: String = "document"
}

object SMNullDocument extends SMDocument(NullDocument)
