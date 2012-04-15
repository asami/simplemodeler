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
class ExtjsEntityGridEntity(aContext: ExtjsEntityContext) extends ExtjsObjectEntity(aContext) {
  val kind = "view"

  override protected def write_Content(out: BufferedWriter) {
    val klass = new GridExtjsClassDefinition(aContext, Nil, ExtjsEntityGridEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
