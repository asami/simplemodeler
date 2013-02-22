package org.simplemodeling.SimpleModeler.entities.squeryl

import java.io.BufferedWriter
import org.goldenport.entity.NoCommit
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Feb. 22, 2013
 * @version Feb. 22, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylPowertypeEntity(aContext: SquerylEntityContext) extends SquerylObjectEntity(aContext) with PPowertypeEntity {
  self =>

  commitMode = NoCommit
}
