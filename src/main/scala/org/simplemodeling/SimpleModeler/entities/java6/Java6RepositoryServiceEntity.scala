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
 * @version Dec.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class Java6RepositoryServiceEntity(aContext: Java6EntityContext) extends JavaObjectEntityBase(aContext) with PRepositoryServiceEntity {
  lazy val klass = new RepositoryServiceJavaClassDefinition(aContext, Nil, Java6RepositoryServiceEntity.this)

  def wadlElement = klass.wadlElement("", "")
  def wadlSpec(
    title: String, subtitle: String,
    author: String = null, date: String = null
  ) = {
    klass.wadlSpec(title, subtitle, author, date)
  }

  /*
   * Java Source
   */
  override protected def write_Content(out: BufferedWriter) {
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
