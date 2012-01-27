package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainDocument

/*
 * @since   Oct. 23, 2008
 * @version Sep. 21, 2009
 * @author  ASAMI, Tomoharu
 */
class SMOperation(val dslOperation: SOperation) extends SMElement(dslOperation) {
  val in = make_document(dslOperation.in)
  val out = make_document(dslOperation.out)

  add_feature(FeatureName, SText(name)) label_is "操作名"
  add_feature(FeatureInput, input_literal) label_is "入力"
  add_feature(FeatureOutput, output_literal) label_is "出力"

  private def input_literal: SDoc = {
    val in = dslOperation.in
    SIAnchor(SText(in.name)) unresolvedRef_is new SElementRef(in.packageName, in.name) summary_is in.summary
  }

  private def output_literal: SDoc = {
    val out = dslOperation.out
    SIAnchor(SText(out.name)) unresolvedRef_is new SElementRef(out.packageName, out.name) summary_is out.summary
  }

  private def make_document(dslDoc: SDocument) = {
    dslDoc match {
      case NullDocument => SMNullDocument
      case domainDoc: DomainDocument => new SMDomainDocument(domainDoc)
      case _ => new SMDocument(dslDoc)
    }
  }
}
