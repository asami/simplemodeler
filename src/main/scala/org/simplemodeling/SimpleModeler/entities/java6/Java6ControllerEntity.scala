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
class Java6ControllerEntity(javaContext: Java6EntityContext) extends JavaObjectEntityBase(javaContext) with PControllerEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new ControllerJavaClassDefinition(javaContext, Nil, Java6ControllerEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
