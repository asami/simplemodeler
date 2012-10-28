package org.simplemodeling.dsl

import scalaz._
import Scalaz._

/**
 * @since   Oct. 28, 2012
 * @version Oct. 28, 2012
 * @author  ASAMI, Tomoharu
 */
case class SExpression(expr: String)

object NullExpression extends SExpression("")
