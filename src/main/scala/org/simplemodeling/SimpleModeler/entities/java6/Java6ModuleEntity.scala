package org.simplemodeling.SimpleModeler.entities.java6

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
 * @since   Aug. 15, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
class Java6ModuleEntity(aContext: Java6EntityContext) extends Java6ObjectEntity(aContext) with PModuleEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new ModuleJavaClassDefinition(aContext, Nil, Java6ModuleEntity.this)
    klass.entries ++= entries
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
