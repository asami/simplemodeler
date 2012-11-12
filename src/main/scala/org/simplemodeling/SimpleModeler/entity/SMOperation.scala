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
 *  version Sep. 21, 2009
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class SMOperation(val dslOperation: SOperation) extends SMElement(dslOperation) {
  val in = _make_inout(dslOperation.in)
  val out = _make_inout(dslOperation.out)

  add_feature(FeatureName, SText(name)) label_is "操作名"
  add_feature(FeatureInput, input_literal) label_is "入力"
  add_feature(FeatureOutput, output_literal) label_is "出力"

  private def input_literal: SDoc = {
    dslOperation.in match {
      case Some(in) => {
        SIAnchor(SText(in.name)) unresolvedRef_is new SElementRef(in.packageName, in.name) summary_is in.summary
      }
      case None => sys.error("???")
    }
  }

  private def output_literal: SDoc = {
    dslOperation.out match {
      case Some(out) => {
    SIAnchor(SText(out.name)) unresolvedRef_is new SElementRef(out.packageName, out.name) summary_is out.summary
      }
      case None => sys.error("???")
    }
  }

  private def _make_inout(a: Option[SAttributeType]) = {
    a match {
      case Some(t) => t match {
        case d: DomainDocument => new SMDomainDocument(d)
        case d: SDocument => new SMDocument(d)
      }
      case None => 
    }
  }

  private def make_document(dslDoc: SDocument) = {
    dslDoc match {
      case NullDocument => SMNullDocument
      case domainDoc: DomainDocument => new SMDomainDocument(domainDoc)
      case _ => new SMDocument(dslDoc)
    }
  }
}
