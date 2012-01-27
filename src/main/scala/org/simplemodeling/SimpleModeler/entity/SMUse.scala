package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline._

/*
 * Nov. 10, 2008
 * Nov. 14, 2008
 */
class SMUse(val dslUse: SUse) extends SMElement(dslUse) {
  var element: SMObject = SMNullObject
  var useKind: SMUseKind = NullUseKind
  var user: SMObject = SMNullObject
  var receiver: SMObject = SMNullObject
  //
  var elementQName: String = ""
  var _userQName: String = ""
  var receiverQName: String = ""

  def userQName = _userQName
  def userQName_=(x: String) {
    require (x != "org.simplemodeling.dsl.NullAgent")
    _userQName = x
  }

<!--
  add_feature(FeatureName, name) label_is "使用名"
  add_feature(FeatureClass, elementLiteral) label_is "オブジェクト"
  add_feature(FeatureUseKind, useKindLiteral) label_is "使用種別"
  add_feature(FeatureReceiverClass, receiverLiteral) label_is "受信者"
-->

  final def elementLiteral: SDoc = {
    SIAnchor(element.name) unresolvedRef_is element_ref summary_is get_summary
  }

  private def element_ref = {
    new SElementRef(element.packageName, element.name)
  }

  private def get_summary: SDoc = {
    element.summary
  }

  final def useKindLiteral: SDoc = {
    useKind.label
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

  final def userLiteral: SDoc = {
    if (user == SMNullObject) "-"
    else SIAnchor(user.name) unresolvedRef_is user_ref
  }

  private def user_ref = {
    require (user != SMNullObject)
    new SElementRef(user.packageName, user.name)
  }

  final def receiverLiteral: SDoc = {
    if (receiver == SMNullObject) "-"
    else SIAnchor(receiver.name) unresolvedRef_is receiver_ref
  }

  private def receiver_ref = {
    require (receiver != SMNullObject)
    new SElementRef(receiver.packageName, receiver.name)
  }
}

abstract class SMUseKind {
  def label: SDoc
}

object NullUseKind extends SMUseKind {
  def label = SText("Null")
}

object OperationInput extends SMUseKind {
  def label = SText("操作入力")
}

object OperationOutput extends SMUseKind {
  def label = SText("操作出力")
}

object OperationPrivate extends SMUseKind {
  def label = SText("操作内部")
}

object UsecaseInput extends SMUseKind {
  def label = SText("ユースケース入力")
}

object UsecaseOutput extends SMUseKind {
  def label = SText("ユースケース出力")
}

object UsecasePrivate extends SMUseKind {
  def label = SText("ユースケース内部")
}
