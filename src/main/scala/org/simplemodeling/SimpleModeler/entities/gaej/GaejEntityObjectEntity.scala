package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

/*
 * @since   Jul.  3, 2009
 * @version Oct. 28, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class GaejEntityObjectEntity(aContext: GaejEntityContext) extends GaejObjectEntity(aContext) {
  var documentName = ""
  
  lazy val entities = {
    val e = for (attr <- attributes if attr.attributeType.isEntity) yield {
      attr.attributeType.asInstanceOf[GaejEntityType].entity
    }
    ListSet(e: _*)
  }
}

