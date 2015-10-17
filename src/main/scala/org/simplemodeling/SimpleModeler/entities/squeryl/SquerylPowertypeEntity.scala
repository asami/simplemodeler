package org.simplemodeling.SimpleModeler.entities.squeryl

import java.io.BufferedWriter
import org.goldenport.entity.NoCommit
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Feb. 22, 2013
 * @version Oct. 17, 2015
 * @author  ASAMI, Tomoharu
 */
class SquerylPowertypeEntity(aContext: SquerylEntityContext) extends SquerylObjectEntity(aContext) with PPowertypeEntity {
  self =>

  override protected def write_Content(out: BufferedWriter) {
    val klass = new SquerylPowertypeScalaClassDefinition(aContext, Nil, self)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
