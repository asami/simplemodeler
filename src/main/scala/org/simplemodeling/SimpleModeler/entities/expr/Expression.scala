package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * Derived from SqlSchemaExpressionBuilder.
 * 
 * @since   Jan. 13, 2013
 * @version Jan. 13, 2013
 * @author  ASAMI, Tomoharu
 */
sealed trait Expression {
}

case class RootExpression() extends Expression

case class ValueAttributeExpression(
  model: SMEIdentifier,
  attribute: PAttribute
) extends Expression

case class EntityAttributeExpression(
  model: SMEIdentifier,
  attribute: PAttribute,
  target: PEntityEntity
) extends Expression

case class AssociationClassExpression(
  model: SMEIdentifier,
  attribute: PAttribute,
  association: PEntityEntity,
  tosource: PAttribute,
  totarget: PAttribute,
  target: PEntityEntity
) extends Expression

case class DotContExpression(
  model: SMEDot,
  lhs: Expression
) extends Expression

case class DotExpression(
  model: SMEDot,
  lhs: Expression,
  rhs: Expression
) extends Expression
