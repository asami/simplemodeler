package org.simplemodeling.SimpleModeler.entity

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
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

sealed trait SMExpressionNode {
  def children: Seq[Tree[SMExpressionNode]]
}

trait SMLeafExpressionNode extends SMExpressionNode {
  val children = Nil
  def toJava: String
}

case class SMEBoolean(value: Boolean) extends SMLeafExpressionNode {
  def toJava = value.toString
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
  def containerPath: Seq[Tree[SMExpressionNode]] = {
    rhs match {
      case xs: SMEDot => lhs +: xs.containerPath
      case _ => List(lhs)
    }
  }
}

case class SMEEval(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

case class SMEIdentifier(name: String) extends SMLeafExpressionNode {
  def toJava = name
}

case class SMEMethod() extends SMLeafExpressionNode {
  def toJava = "SMEMethod: not implemented yet"
}

case class SMENested(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

case class SMENull() extends SMLeafExpressionNode {
  def toJava = "null"
}

case class SMENumber(value: Number) extends SMLeafExpressionNode {
  def toJava = value.toString
}

case class SMEProperty() extends SMLeafExpressionNode {
  def toJava = "SMEProperty: not implemented yet"
}

case class SMEString(value: String) extends SMLeafExpressionNode {
  def toJava = UJavaString.stringLiteral(value)
}

case class SMEText(value: String) extends SMLeafExpressionNode {
  def toJava = UJavaString.stringLiteral(value)
}

case class SMEUnary(
  children: Seq[Tree[SMExpressionNode]]) extends SMExpressionNode {
}

trait SMEBinaryOperator extends SMExpressionNode {
  def children: Seq[Tree[SMExpressionNode]]
  require (children.length == 2, "Binary operator must have 2 children.")
  def lhs = children(0)
  def rhs = children(1)
  def toJavaOperator: String
  def toOperatorMethodName: String
}

case class SMEBOAdd(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "+"
  def toOperatorMethodName = "add"
}

case class SMEBOAnd(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "&&"
  def toOperatorMethodName = "and"
}

case class SMEBODiv(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "/"
  def toOperatorMethodName = "divide"
}

case class SMEBOEq(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "=="
  def toOperatorMethodName = "euqal"
}

case class SMEBOGe(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = ">="
  def toOperatorMethodName = "greaterEqualThan"
}

case class SMEBOGt(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = ">"
  def toOperatorMethodName = "greaterThan"
}

case class SMEBOLe(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "<="
  def toOperatorMethodName = "lesserEqualThan"
}

case class SMEBOLt(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "<"
  def toOperatorMethodName = "lesserThan"
}

case class SMEBOMod(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "%"
  def toOperatorMethodName = "mod"
}

case class SMEBOMul(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "*"
  def toOperatorMethodName = "multiply"
}

case class SMEBONe(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "!="
  def toOperatorMethodName = "notEqual"
}

case class SMEBOOr(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "||"
  def toOperatorMethodName = "or"
}

case class SMEBOSub(
  children: Seq[Tree[SMExpressionNode]]
) extends SMEBinaryOperator {
  def toJavaOperator = "-"
  def toOperatorMethodName = "negate"
}
