package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Mar. 21, 2011
 * @version Mar. 21, 2011
 * @author  ASAMI, Tomoharu
 */
class FlowMachineCategory extends SDCategory("フロー機械") {
  override def tableHead = List("名前", "派生", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.anchor,
	  anEntity.feature(FeatureOwnerClass).value,
	  anEntity.caption,
	  anEntity.note)
  }
}
