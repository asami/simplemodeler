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
 * Dec. 13, 2008
 * Dec. 13, 2008
 */
class SMExtendUsecaseStep(val dslExtendUsecaseStep: SExtendUsecaseStep, val dslExtendUsecases: Seq[(String, SExtensionUsecase)]) extends SMStep(dslExtendUsecaseStep) {
  final def extensionPointName = dslExtendUsecaseStep.extensionPointName
}
