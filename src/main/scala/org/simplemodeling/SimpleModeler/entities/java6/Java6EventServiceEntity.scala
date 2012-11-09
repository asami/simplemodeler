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
 * @since   Nov. 10, 2012
 * @version Nov. 10, 2012
 * @author  ASAMI, Tomoharu
 */
class Java6EventServiceEntity(aContext: Java6EntityContext) extends JavaObjectEntityBase(aContext) with PServiceEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new EventServiceJavaClassDefinition(aContext, Nil, Java6EventServiceEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
