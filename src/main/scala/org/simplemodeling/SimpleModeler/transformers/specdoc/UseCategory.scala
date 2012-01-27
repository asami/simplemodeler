package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * Nov. 10, 2008
 * Dec. 18, 2010
 */
class UseCategory extends SDCategory("使用") {
  override def tableHead = List("名前", "オブジェクト", "使用種別", "使用者", "受信者", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.anchor,
	  anEntity.feature(FeatureClass).value,
	  anEntity.feature(FeatureUseKind).value,
	  anEntity.feature(FeatureUserClass).value,
	  anEntity.feature(FeatureReceiverClass).value,
	  anEntity.caption,
	  anEntity.note)
  }
}
