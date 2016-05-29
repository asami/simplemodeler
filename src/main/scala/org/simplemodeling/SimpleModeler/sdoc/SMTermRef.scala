package org.simplemodeling.SimpleModeler.sdoc

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * @since   Dec.  6, 2008
 * @version May. 29, 2016
 * @author  ASAMI, Tomoharu
 */
class SMTermRef(val anObject: SObject) extends SIAnchor(anObject.term) {
  unresolvedRef = new SElementRef(anObject.packageName, anObject.name)
}
