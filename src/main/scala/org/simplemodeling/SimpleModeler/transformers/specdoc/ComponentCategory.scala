package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Aug.  7, 2009
 * @version Dec. 18, 2010
 * @author  ASAMI, Tomoharu
 */
class ComponentCategory extends SDCategory("コンポーネント") {
  override def tableHead = List("名前", "種別", "区分", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.anchor,
	  anEntity.feature(FeatureKind).value,
	  anEntity.feature(FeaturePowertype).value,
	  anEntity.caption,
	  anEntity.note)
  }
}
