package org.simplemodeling.SimpleModeler.entities.extjs

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 14, 2012
 * @version Apr. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsEntityEditFormEntity(aContext: ExtjsEntityContext) extends ExtjsObjectEntity(aContext) {
  kindName = "view"

  override protected def write_Content(out: BufferedWriter) {
    val klass = new EditFormExtjsClassDefinition(aContext, Nil, ExtjsEntityEditFormEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
