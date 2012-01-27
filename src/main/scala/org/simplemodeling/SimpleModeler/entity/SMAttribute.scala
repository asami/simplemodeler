package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainValueName

/*
 * @since   Sep. 17, 2008
 *  version Oct. 20, 2009
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class SMAttribute(val dslAttribute: SAttribute) extends SMElement(dslAttribute) {
  val attributeType = new SMAttributeType(dslAttribute.attributeType)
  val multiplicity = new SMMultiplicity(dslAttribute.multiplicity)
  val idPolicy: SMIdPolicy = dslAttribute.idPolicy match {
    case p: AutoIdPolicy => SMAutoIdPolicy
    case p: ApplicationIdPolicy => SMApplicationIdPolicy
  }
  final def kind: SAttributeKind = dslAttribute.kind

  add_feature(FeatureName, SText(name)) label_is "属性名"
  add_feature(FeatureType, type_literal) label_is "属性型"
  add_feature(FeatureMultiplicity, multiplicity_literal) label_is "多重度"

  final def isId = dslAttribute.isId
  final def isPersistent = dslAttribute.isPersistent
  final def defaultValue = dslAttribute.defaultValue
  final def isName = kind.isInstanceOf[NameAttributeKind]
  final def isNameWeak = {
    attributeType.typeObject.isInstanceOf[SMDomainValueName]
  }
  final def isUser = kind.isInstanceOf[UserAttributeKind]
  final def isTitle = kind.isInstanceOf[TitleAttributeKind]
  final def isSubTitle = kind.isInstanceOf[SubTitleAttributeKind]
  final def isSummary = kind.isInstanceOf[SummaryAttributeKind]
  final def isCategory = kind.isInstanceOf[CategoryAttributeKind]
  final def isAuthor = kind.isInstanceOf[AuthorAttributeKind]
  final def isIcon = kind.isInstanceOf[IconAttributeKind]
  final def isLogo = kind.isInstanceOf[LogoAttributeKind]
  final def isLink = kind.isInstanceOf[LinkAttributeKind]
  final def isContent = kind.isInstanceOf[ContentAttributeKind]
  final def isCreated = kind.isInstanceOf[CreatedAttributeKind]
  final def isUpdated = kind.isInstanceOf[UpdatedAttributeKind]

  private def type_literal: SDoc = {
    SIAnchor(attributeType.name) unresolvedRef_is element_ref summary_is get_summary
  }

  private def element_ref = {
    new SElementRef(attributeType.packageName, attributeType.name)
  }

  private def get_summary: SDoc = {
    attributeType.summary
  }

  private def multiplicity_literal: SDoc = {
    SIAnchor(SText(multiplicity.text)) unresolvedRef_is SHelpRef("multiplicity", multiplicity.symbol)
  }
}
