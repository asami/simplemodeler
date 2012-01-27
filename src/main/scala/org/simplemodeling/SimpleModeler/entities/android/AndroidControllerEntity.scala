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

/*
 * @since   Jul. 12, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidControllerEntity(androidContext: AndroidEntityContext) extends JavaObjectEntityBase(androidContext) with PControllerEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new AndroidControllerJavaClassDefinition(androidContext, Nil, AndroidControllerEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
