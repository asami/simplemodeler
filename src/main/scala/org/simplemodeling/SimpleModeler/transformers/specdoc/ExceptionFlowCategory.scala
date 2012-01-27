package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * Nov. 23, 2008
 * Dec. 18, 2010
 */
class ExceptionFlowCategory extends SDCategory("例外脚本") {
  override def tableHead = List("名前", "派生", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.anchor,
	  anEntity.feature(FeatureOwnerClass).value,
	  anEntity.caption,
	  anEntity.note)
  }
}
