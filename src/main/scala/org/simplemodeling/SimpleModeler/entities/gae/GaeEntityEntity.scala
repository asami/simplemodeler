package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}
import org.simplemodeling.SimpleModeler.entity._

/*
 * Apr. 22, 2009
 * Apr. 22, 2009
 * ASAMI, Tomoharu
 */
class GaeEntityEntity(aContext: GEntityContext) extends GaeObjectEntity(aContext) {
  var modelEntity: SMEntity = null
}

