package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * Oct. 24, 2008
 * Dec. 18, 2010
 */
class ParticipationCategory extends SDCategory("参加") {
  override def tableHead = List("要素", "種類", "種別", "役割種類", "役割名", "派生", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.feature(FeatureElement).value,
	  anEntity.feature(FeatureType).value,
	  anEntity.feature(FeatureKind).value,
	  anEntity.feature(FeatureRoleType).value,
	  anEntity.feature(FeatureRoleName).value,
	  anEntity.feature(FeatureOwnerClass).value,
	  anEntity.caption,
	  anEntity.note)
  }
}
