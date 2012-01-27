package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * Dec.  4, 2008
 * Dec.  4, 2008
 */
class SMJumpPath(dslPath: SJumpPath) extends SMAlternatePath(dslPath) {
  var destinationStep: SMStep = NullSMStep

  def destination = dslPath.destination
}
