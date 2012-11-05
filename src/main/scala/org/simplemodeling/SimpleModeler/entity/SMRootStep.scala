package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}

/*
 * @since   Nov.  5, 2012
 * @version Nov.  5, 2012
 * @author  ASAMI, Tomoharu
 */
class SMRootStep() extends SMStep(NullStep) {
  override protected def copy_Node(): SMRootStep = {
    new SMRootStep()
  }
}
