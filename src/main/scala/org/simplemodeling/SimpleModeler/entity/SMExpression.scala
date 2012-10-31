package org.simplemodeling.SimpleModeler.entity

import scalaz._, Scalaz._
import org.apache.commons.lang3.StringUtils
import org.simplemodeling.dsl._

/*
 * @since   Oct. 28, 2012
 * @version Nov.  1, 2012
 * @author  ASAMI, Tomoharu
 */
case class SMExpression(dslExpression: SExpression) {
  require (dslExpression != null, "dslExpression must null")
  require (StringUtils.isNotBlank(dslExpression.expr), "dslExpression must not empty")
  final def name = sys.error("not implemented yet")
  final def value = sys.error("not implemented yet")

  lazy val tree: Tree[SMExpressionNode] = {
    SMExpressionJuel.tree("${" + dslExpression.expr + "}")
  }
}

trait SMExpressionNode {
  def children: Seq[Tree[SMExpressionNode]]
}

trait SMLeafExpressionNode extends SMExpressionNode {
  val children = Nil
}

case class SMEBoolean(value: Boolean) extends SMLeafExpressionNode {
}

case class SMEBracket(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

case class SMEChoice(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

case class SMEComposite(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

case class SMEDot(
  children: Seq[Tree[SMExpressionNode]]
) extends SMExpressionNode {
  require (children.length == 2, "Dot must have 2 children.")
  def lhs = children(0)
  def rhs = children(1)
}

case class SMEEval(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

case class SMEIdentifier(name: String) extends SMLeafExpressionNode {
}

case class SMEMethod() extends SMLeafExpressionNode {
}

case class SMENested(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

case class SMENull() extends SMLeafExpressionNode {
}

case class SMENumber(value: Number) extends SMLeafExpressionNode {
}

case class SMEProperty() extends SMLeafExpressionNode {
}

case class SMEString(value: String) extends SMLeafExpressionNode {
}

case class SMEText(value: String) extends SMLeafExpressionNode {
}

case class SMEUnary(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

trait SMEBinaryOperator extends SMExpressionNode {
}

case class SMEBOAdd(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOAnd(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBODiv(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOEq(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOGe(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOGt(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOLe(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOLt(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOMod(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOMul(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBONe(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOOr(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}

case class SMEBOSub(
  children: Seq[Tree[SMExpressionNode]]) extends SMEBinaryOperator {
}
