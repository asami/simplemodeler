package org.simplemodeling.SimpleModeler.entities

import java.io._
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

// derived from GaejEntityObjectEntity since Jul.  3, 2009.
/*
 * @since   Apr. 23, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
trait PEntityObjectEntity extends PObjectEntity {
  var documentName: String
  
  lazy val entities = {
    val e = for (attr <- attributes if attr.attributeType.isEntity) yield {
      attr.attributeType.asInstanceOf[PEntityType].entity
    }
    ListSet(e: _*)
  }
}
