package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import de.odysseus.el._
import de.odysseus.el.util._

/*
 * @since   Oct. 28, 2012
 * @version Oct. 28, 2012
 * @author  ASAMI, Tomoharu
 */
case class SMExpression(val dslExpression: SExpression) {
  final def name = sys.error("not implemented yet")
  final def value = sys.error("not implemented yet")

  lazy val juelExpression: TreeValueExpression = {
    SMExpression.juelExpression(dslExpression.expr)
  }
}

object SMExpression {
  private val _factory = new ExpressionFactoryImpl()
  private val _context = new SimpleContext(); // more on this here...

  def juelExpression(expr: String) = {
    _factory.createValueExpression(_context, expr, classOf[Object])
  }
}
