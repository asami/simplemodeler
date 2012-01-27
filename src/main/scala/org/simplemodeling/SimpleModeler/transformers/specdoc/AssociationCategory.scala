package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * Oct. 13, 2008
 * Dec. 18, 2010
 */
class AssociationCategory extends SDCategory("関連") {
  override def tableHead = List("名前", "エンティティ", "多重度", "派生", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.anchor,
	  anEntity.feature(FeatureType).value,
	  anEntity.feature(FeatureMultiplicity).value,
	  anEntity.feature(FeatureOwnerClass).value,
	  anEntity.caption,
	  anEntity.note)
  }
}
