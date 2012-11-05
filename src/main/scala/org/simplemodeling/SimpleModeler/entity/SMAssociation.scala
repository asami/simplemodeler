package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain.DomainEntityPart
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * @since   Sep. 17, 2008
 *  version Nov.  9, 2009
 * @version Nov.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class SMAssociation(val dslAssociation: SAssociation) extends SMElement(dslAssociation) {
//  final def entity = dslAssociation.entity // XXX SMEntity?
  val associationType = new SMAssociationType(dslAssociation.entity)
  val multiplicity = new SMMultiplicity(dslAssociation.multiplicity)

  final def isAggregation = {
//    record_trace("dslAssociation.kind = " + dslAssociation.kind)
    dslAssociation.kind == AggregationAssociationKind
  }
  final def isComposition = dslAssociation.kind == CompositionAssociationKind

  final def isPersistent = dslAssociation.isPersistent
  final def isBinary = dslAssociation.isBinary
  final def isQueryReference = dslAssociation.isQueryReference
  final def isCache = dslAssociation.isCache
  final def backReferenceNameOption: Option[String] = dslAssociation.backReferenceNameOption
  final def backReferenceMultiplicityOption: Option[SMMultiplicity] = {
    dslAssociation.backReferenceMultiplicityOption.flatMap {
      multi => Some(new SMMultiplicity(multi))
    }
  }

  add_feature(FeatureName, SText(name)) label_is "関連名"
  add_feature(FeatureType, type_literal) label_is "関連型"
  add_feature(FeatureMultiplicity, multiplicity_literal) label_is "多重度"

  private def type_literal: SDoc = {
    SIAnchor(SText(associationType.name)) unresolvedRef_is element_ref summary_is get_summary
  }

  private def element_ref = {
    new SElementRef(associationType.packageName, associationType.name)
  }

  private def get_summary: SDoc = {
    associationType.summary
  }

  // XXX duplicate with SMAttribute
  private def multiplicity_literal: SDoc = {
    SIAnchor(SText(multiplicity.text)) unresolvedRef_is SHelpRef("multiplicity", multiplicity.symbol)
  }
}
