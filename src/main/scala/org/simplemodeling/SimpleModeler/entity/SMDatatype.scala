package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * Oct. 24, 2008
 * Jan. 18, 2009
 */
class SMDatatype(val dslDatatype: SDatatype) extends SMObject(dslDatatype) {
  override def typeName: String = "datatype"
}
