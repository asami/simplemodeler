package org.simplemodeling.SimpleModeler.sdoc

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * Dec.  7, 2008
 * Dec.  7, 2008
 */
class SMKeywordRef(aName: String, aHelp: String, theParams: Any*) extends SIAnchor(aName) {
  unresolvedRef = new SHelpRef(aHelp, theParams: _*)
}
