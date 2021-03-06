package org.simplemodeling.SimpleModeler.entities.extjs

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Mar. 31, 2012
 * @version Apr. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsDocumentEntity(extjsContext: ExtjsEntityContext) extends ExtjsObjectEntity(extjsContext) with PDocumentEntity {
  kindName = "doc"

  override protected def write_Content(out: BufferedWriter) {
    val klass = new ExtjsDocumentJavaClassDefinition(extjsContext, Nil, ExtjsDocumentEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
