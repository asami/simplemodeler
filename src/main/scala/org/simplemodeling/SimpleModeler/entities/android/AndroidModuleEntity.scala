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
class AndroidModuleEntity(aContext: AndroidEntityContext) extends AndroidObjectEntity(aContext) with PModuleEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new AndroidModuleJavaClassDefinition(aContext, Nil, AndroidModuleEntity.this)
    klass.entries ++= entries
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
