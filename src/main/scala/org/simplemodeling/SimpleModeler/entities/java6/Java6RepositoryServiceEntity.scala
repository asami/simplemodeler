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
 * @since   Nov.  2, 2012
 * @version Dec.  4, 2012
 * @author  ASAMI, Tomoharu
 */
class Java6RepositoryServiceEntity(aContext: Java6EntityContext) extends JavaObjectEntityBase(aContext) with PServiceEntity {
  val klass = new RepositoryServiceJavaClassDefinition(aContext, Nil, Java6RepositoryServiceEntity.this)

  def wadlElement = klass.wadlElement

  /*
   * Java Source
   */
  override protected def write_Content(out: BufferedWriter) {
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
