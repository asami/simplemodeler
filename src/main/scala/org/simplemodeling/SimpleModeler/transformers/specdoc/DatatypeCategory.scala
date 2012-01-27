package org.simplemodeling.SimpleModeler.transformers.specdoc

import org.goldenport.sdoc._
import org.goldenport.entities.specdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * Oct. 25, 2008
 * Dec. 18, 2010
 */
class DatatypeCategory extends SDCategory("データタイプ") {
  override def tableHead = List("名前", "説明", "備考")

  override def tableRow(anEntity: SDEntity): Seq[SDoc] = {
    Array(anEntity.anchor,
	  anEntity.caption,
	  anEntity.note)
  }
}
