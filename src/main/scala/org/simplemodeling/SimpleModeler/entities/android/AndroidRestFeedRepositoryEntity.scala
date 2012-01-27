package org.simplemodeling.SimpleModeler.entities.android

import java.io._
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._
import content.EntityContent

/*
 * @since   Aug. 13, 2011
 * @version Sep.  1, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidRestFeedRepositoryEntity(val androidContext: AndroidEntityContext) extends JavaObjectEntityBase(androidContext) with PServiceEntity {
  var driverIfName: Option[String] = None
  var defaultDriverName: Option[String] = None
  var entityDocumentName: Option[String] = None

  override protected def write_Content(out: BufferedWriter) {
    val klass = new AndroidRestFeedRepositoryJavaClassDefinition(androidContext, Nil, AndroidRestFeedRepositoryEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
