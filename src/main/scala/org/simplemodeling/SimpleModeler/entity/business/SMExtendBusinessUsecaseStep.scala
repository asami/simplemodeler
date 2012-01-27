package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * Dec. 13, 2008
 * Dec. 18, 2010
 */
class SMExtendBusinessUsecaseStep(val dslExtendBusinessUsecaseStep: ExtendBusinessUsecaseStep, val dslBusinessUsecases: Seq[(String, ExtensionBusinessUsecase)]) extends SMExtendUsecaseStep(dslExtendBusinessUsecaseStep, dslBusinessUsecases) {
}
