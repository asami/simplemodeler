package org.simplemodeling.SimpleModeler.entities.extjs

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Mar. 31, 2012
 *  version Apr. 15, 2012
 * @version Jun. 17, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsValueEntity(extjsContext: ExtjsEntityContext) extends ExtjsObjectEntity(extjsContext) with PValueEntity {
  kindName = DEFAULT_MODEL_KIND

  override protected def write_Content(out: BufferedWriter) {
    val klass = new ValueJavaClassDefinition(extjsContext, Nil, ExtjsValueEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
