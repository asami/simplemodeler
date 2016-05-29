package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Oct. 28, 2012
 * @version May.  7, 2016
 * @author  ASAMI, Tomoharu
 */
case class PExpression(model: SMExpression) {
  def tree = model.tree

  def drawTree: String = {
    implicit def SMExpressionShow: Show[SMExpressionNode] = Show.showA
    tree.drawTree
  }
}
