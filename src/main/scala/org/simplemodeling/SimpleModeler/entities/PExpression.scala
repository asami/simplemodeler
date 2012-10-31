package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import de.odysseus.el._
import de.odysseus.el.util._

/*
 * @since   Oct. 28, 2012
 * @version Oct. 31, 2012
 * @author  ASAMI, Tomoharu
 */
case class PExpression(model: SMExpression) {
  def tree = model.tree
}
