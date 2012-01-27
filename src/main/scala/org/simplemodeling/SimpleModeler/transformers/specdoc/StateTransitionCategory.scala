package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * Dec. 24, 2008
 * Dec. 18, 2010
 */
class StateTransitionCategory extends SDCategory("状態遷移") {
  override def tableHead = List("名前", "エンティティ", "事前状態", "ガード", "事後状態", "派生", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.anchor,
	  anEntity.feature(FeatureType).value,
	  anEntity.feature(FeaturePreState).value,
	  anEntity.feature(FeatureGuard).value,
	  anEntity.feature(FeaturePostState).value,
	  anEntity.feature(FeatureOwnerClass).value,
	  anEntity.caption,
	  anEntity.note)
  }
}
