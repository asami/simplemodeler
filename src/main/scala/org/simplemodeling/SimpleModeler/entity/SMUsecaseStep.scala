package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * Dec. 10, 2008
 * Dec. 10, 2008
 */
class SMUsecaseStep(val dslUsecaseStep: SUsecaseStep, val dslUsecase: SUsecase) extends SMStep(dslUsecaseStep) {

  final def getUsecaseTerm: SDoc = new SMTermRef(dslUsecase)
}
