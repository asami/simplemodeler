package org.simplemodeling.SimpleModeler.entities.squeryl

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Feb. 22, 2013
 * @version Feb. 22, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylEntityPartEntity(aContext: SquerylEntityContext) extends SquerylObjectEntity(aContext) with PEntityPartEntity {
  self =>

  override protected def write_Content(out: BufferedWriter) {
    val klass = new SquerylEntityScalaClassDefinition(aContext, Nil, self)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
