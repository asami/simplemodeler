package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * Dec. 12, 2008
 * Jan. 18, 2009
 */
class SMExtensionBusinessUsecase(val dslExtensionBusinessUsecase: ExtensionBusinessUsecase) extends SMBusinessUsecase(dslExtensionBusinessUsecase) {
  override def kindName: String = "extension"
}
