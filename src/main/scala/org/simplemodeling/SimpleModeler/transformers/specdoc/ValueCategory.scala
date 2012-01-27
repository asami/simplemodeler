package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * Oct. 17, 2008
 * Dec. 18, 2010`
 */
class ValueCategory extends SDCategory("バリュー") {
  override def tableHead = List("名前", "種別", "区分", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.anchor,
	  anEntity.feature(FeatureKind).value,
	  anEntity.feature(FeaturePowertype).value,
	  anEntity.caption,
	  anEntity.note)
  }
}
