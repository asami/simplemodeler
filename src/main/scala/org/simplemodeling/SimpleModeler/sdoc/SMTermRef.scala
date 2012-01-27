package org.simplemodeling.SimpleModeler.sdoc

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * Dec.  6, 2008
 * Dec. 10, 2008
 */
case class SMTermRef(val anObject: SObject) extends SIAnchor(anObject.term) {
  unresolvedRef = new SElementRef(anObject.packageName, anObject.name)
}
