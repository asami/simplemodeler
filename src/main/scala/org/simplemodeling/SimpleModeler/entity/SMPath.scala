package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * Nov. 23, 2008
 * Dec.  4, 2008
 */
abstract class SMPath(val dslPath: SPath) extends SMElement(dslPath) {
  var targetStep: SMStep = NullSMStep

  final def markRange = dslPath.markRange
  final def condition = dslPath.condition
}
