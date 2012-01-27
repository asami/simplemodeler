package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.simplemodeling.SimpleModeler.entity._

/*
 * Flow Port
 * @since   Mar. 21, 2011
 * @version Mar. 22, 2011
 * @author  ASAMI, Tomoharu
 */
class SMPort(val dslPort: SPort) extends SMElement(dslPort) {
  val entityType = new SMEntityType(dslPort.entity)

  add_feature(FeatureName, SText(name)) label_is "ポート名"
  add_feature(FeatureType, type_literal) label_is "ポート型"
  add_feature(FeatureInOut, inputoutput_literal) label_is "入出力"

  private def type_literal: SDoc = {
    SIAnchor(dslPort.entity.name) unresolvedRef_is element_ref summary_is get_summary
  }

  private def element_ref = {
    new SElementRef(dslPort.entity.packageName, dslPort.entity.name)
  }

  private def get_summary: SDoc = {
    dslPort.summary
  }

  private def inputoutput_literal: SDoc = {
    val symbol = dslPort.inout match {
      case SPortIn => 'in
      case SPortOut => 'out
      case SPortInOut => 'inout
    }
    SIAnchor(SText(symbol.name)) unresolvedRef_is SHelpRef("inout", symbol)
  }
}
