package org.simplemodeling.SimpleModeler.entities.extjs

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Mar. 31, 2012
 * @version Mar. 31, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsValueEntity(val extjsContext: ExtjsEntityContext) extends JavaObjectEntityBase(extjsContext) with PValueEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new ValueJavaClassDefinition(extjsContext, Nil, ExtjsValueEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
