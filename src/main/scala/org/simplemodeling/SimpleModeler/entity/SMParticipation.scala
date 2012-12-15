package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * @since   Oct. 23, 2008
 *  version Mar. 21, 2011
 *  version Nov. 25, 2012
 * @version Dec. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class SMParticipation(val dslParticipation: SParticipation) extends SMElement(dslParticipation) {
  var element: SMObject = SMNullObject
  var roleType: SMParticipationRole = NullParticipationRole
  var roleName: SDoc = SEmpty
  var associationOption: Option[SMAssociation] = None
  def association = associationOption.get

/*
  add_feature(FeatureName, name) label_is "参加名"
  add_feature(FeatureType, element_literal) label_is "要素"
  add_feature(FeatureRoleType, role_type_literal) label_is "役割種別"
  add_feature(FeatureRoleName, roleName) label_is "役割名"
*/

  final def elementLiteral: SDoc = {
    SIAnchor(element.name) unresolvedRef_is element_ref summary_is get_summary
  }

  private def element_ref = {
    new SElementRef(element.packageName, element.name)
  }

  private def get_summary: SDoc = {
    element.summary
  }

  final def roleTypeLiteral: SDoc = {
    roleType.label
  }

  final def elementTypeLiteral: SDoc = {
    val typeName = element.typeName
    if (typeName == null) "-"
    else SIAnchor(typeName) unresolvedRef_is SHelpRef("object-type", typeName)
  }

  final def elementKindLiteral: SDoc = {
    val kindName = element.kindName
    if (kindName == null) "-"
    else SIAnchor(kindName) unresolvedRef_is SHelpRef("object-kind", kindName)
  }
}

sealed trait SMParticipationRole {
  def label: SDoc
}

case object NullParticipationRole extends SMParticipationRole {
  def label = SText("Null")
}

case object RoleParticipationRole extends SMParticipationRole {
  def label = SText("役割")
}

case object ServiceParticipationRole extends SMParticipationRole {
  def label = SText("サービス")
}

case object RuleParticipationRole extends SMParticipationRole {
  def label = SText("規則")
}

case object AttributeParticipationRole extends SMParticipationRole {
  def label = SText("属性")
}

case object AssociationParticipationRole extends SMParticipationRole {
  def label = SText("関連")
}

case object AggregationParticipationRole extends SMParticipationRole {
  def label = SText("集約")
}

case object CompositionParticipationRole extends SMParticipationRole {
  def label = SText("合成")
}

case object AssociationClassParticipationRole extends SMParticipationRole {
  def label = SText("関連クラス")
}

case object StateMachineParticipationRole extends SMParticipationRole {
  def label = SText("状態機械")
}

case object PortParticipationRole extends SMParticipationRole {
  def label = SText("ポート")
}
