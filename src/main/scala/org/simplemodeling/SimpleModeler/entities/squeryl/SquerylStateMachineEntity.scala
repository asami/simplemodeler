package org.simplemodeling.SimpleModeler.entities.squeryl

import java.io.BufferedWriter
import org.goldenport.entity.NoCommit
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Feb. 22, 2013
 * @version Oct. 17, 2015
 * @author  ASAMI, Tomoharu
 */
class SquerylStateMachineEntity(aContext: SquerylEntityContext) extends SquerylObjectEntity(aContext) with PStateMachineEntity {
  self =>

  override protected def write_Content(out: BufferedWriter) {
    val klass = new SquerylStateMachineScalaClassDefinition(aContext, Nil, self)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
