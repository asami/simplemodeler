package org.simplemodeling.SimpleModeler.entity.requirement

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.requirement._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * Dec. 17, 2008
 * Jan. 18, 2009
 */
class SMExtensionRequirementUsecase(val dslExtensionRequirementUsecase: ExtensionRequirementUsecase) extends SMRequirementUsecase(dslExtensionRequirementUsecase) {
  override def kindName: String = "extension"
}
