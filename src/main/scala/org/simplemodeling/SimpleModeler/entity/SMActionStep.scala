package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}

/*
 * Dec.  5, 2008
 * Dec.  8, 2008
 */
class SMActionStep(val dslActionStep: SActionStep) extends SMStep(dslActionStep) {
  final def action = dslActionStep.action

  override protected def copy_Node(): SMActionStep = {
    new SMActionStep(dslActionStep)
  }
}
