package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * Dec. 15, 2008
 * Dec. 15, 2008
 */
class SMParameterStep(val dslParameterStep: SParameterStep) extends SMStep(dslParameterStep) {
  override protected def copy_Node(): SMParameterStep = {
    new SMParameterStep(dslParameterStep)
  }

  final def getDocumentTerm: SDoc = {
    new SMTermRef(dslParameterStep.document)
  }

  final def getParameterTerm: SDoc = {
    dslParameterStep.parameter
  }
}
