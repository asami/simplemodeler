package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 14, 2012
 * @version Apr. 21, 2012
 * @author  ASAMI, Tomoharu
 */
class MainControllerExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  pobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ControllerExtjsClassDefinition(context, aspects, pobject, maker) {
}
