package org.simplemodeling.SimpleModeler.entities.squeryl

import java.io.BufferedWriter
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Feb. 22, 2013
 * @version Feb. 22, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylEntityEntity(aContext: SquerylEntityContext) extends SquerylObjectEntity(aContext) with PEntityEntity {
  self =>

  override protected def write_Content(out: BufferedWriter) {
    val klass = new SquerylEntityScalaClassDefinition(aContext, Nil, self)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
