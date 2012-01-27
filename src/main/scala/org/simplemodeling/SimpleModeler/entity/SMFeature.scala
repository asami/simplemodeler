package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.value.GKey
import org.goldenport.sdoc.{SDoc, SEmpty}
import com.asamioffice.goldenport.text.UString

/*
 * @since   Oct. 12, 2008
 * @version Mar. 21, 2011
 * @author  ASAMI, Tomoharu
 */
class SMFeature(val key: GKey)(aFunction: () => SDoc) {
  private val _function: () => SDoc = aFunction

  def value: SDoc = _function()
  var description: SDoc = SEmpty
  var label: SDoc = UString.capitalize(key.toString)

  def this(aKey: GKey, aValue: SDoc) = this(aKey)(SMFeature.rawValue(aValue))

  final def description_is(aDesc: SDoc): SMFeature = {
    description = aDesc
    this
  }

  final def label_is(aLabel: SDoc): SMFeature = {
    label = aLabel
    this
  }
}

object FeatureComponent extends GKey("Component") {
  set_label("コンポーネント")
}

object FeaturePackage extends GKey("Package") {
  set_label("パッケージ")
}

object FeatureName extends GKey("Name") {
  set_label("名前")
}

object FeatureClass extends GKey("Class") {
  set_label("クラス")
}

object FeatureBaseClass extends GKey("BaseClass") {
  set_label("基底クラス")
}

object FeatureDerivedClasses extends GKey("DerivedClasses") {
  set_label("派生クラス")
}

object FeatureOwnerClass extends GKey("OwnerClass") {
  set_label("所有クラス")
}

object FeatureUserClass extends GKey("UserClass") {
  set_label("使用クラス") // ??? 使用者クラス
}

object FeatureReceiverClass extends GKey("ReceiverClass") {
  set_label("受信クラス")
}

object FeatureElement extends GKey("Element") {
  set_label("要素")
}

object FeatureType extends GKey("Type") {
  set_label("種類")
}

object FeatureKind extends GKey("Kind") {
  set_label("種別")
}

object FeaturePowertype extends GKey("Powertype") {
  set_label("区分")
}

// currently unsed
object FeatureRole extends GKey("Role") {
  set_label("役割")
}

object FeatureRoleType extends GKey("RoleType") {
  set_label("役割種類")
}

object FeatureRoleKind extends GKey("RoleKind") {
  set_label("役割種別")
}

object FeatureRoleName extends GKey("RoleName") {
  set_label("役割名")
}

object FeatureUseKind extends GKey("UseKind") {
  set_label("使用種別")
}

object FeatureMultiplicity extends GKey("Multiplicity") {
  set_label("多重度")
}

object FeatureTerm extends GKey("Term") {
  set_label("用語")
}

/*
object FeatureStateMachine extends GKey("StateMachine") {
  set_label("状態機械")
}
*/

object FeatureInput extends GKey("Input") {
  set_label("入力")
}

object FeatureOutput extends GKey("Output") {
  set_label("出力")
}

object FeatureInOut extends GKey("InOut") {
  set_label("入出力")
}

object FeaturePreState extends GKey("PreState") {
  set_label("事前状態")
}

object FeaturePostState extends GKey("PostState") {
  set_label("事後状態")
}

object FeatureGuard extends GKey("Guard") {
  set_label("ガード")
}

object FeatureIncludeTask extends GKey("IncludeTask") {
  set_label("Includeタスク")
}

object FeatureIncludeUsecase extends GKey("IncludeUsecase") {
  set_label("Includeユースケース")
}

object FeatureExtendUsecase extends GKey("ExtendUsecase") {
  set_label("Extendユースケース")
}

object FeatureUserTask extends GKey("userTask") {
  set_label("利用者タスク")
}

object FeatureUserUsecase extends GKey("userUsecase") {
  set_label("利用者ユースケース")
}

object SMFeature {
  def rawValue(value: SDoc)(): SDoc = value
}
