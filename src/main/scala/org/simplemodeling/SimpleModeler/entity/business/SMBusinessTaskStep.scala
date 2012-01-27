package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * Dec.  7, 2008
 * Dec. 18, 2010
 */
class SMBusinessTaskStep(val dslBusinessTaskStep: BusinessTaskStep, val dslBusinessTask: BusinessTask) extends SMTaskStep(dslBusinessTaskStep, dslBusinessTask) {
  var businessTask: SMBusinessTask = NullSMBusinessTask
}
