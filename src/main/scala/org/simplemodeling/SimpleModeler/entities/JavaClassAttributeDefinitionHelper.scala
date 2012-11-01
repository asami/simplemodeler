package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Oct. 30, 2012
 * @version Nov.  1, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaClassAttributeDefinitionHelper {
  self: JavaClassAttributeDefinition =>

  def code_expression: String = {
    if (isDerive) {
      attr.deriveExpression.map(code_expression) | varName
    } else {
      varName
    }
  }

  def code_expression(expr: PExpression): String = {
    code_expression(expr.model)
  }

  def code_expression(expr: SMExpression): String = {
    _expr_string(expr.tree)
  }

  private def _expr_string(expr: Tree[SMExpressionNode]): String = {
    implicit def SMExpressionShow: Show[SMExpressionNode] = showA

    expr.rootLabel match {
      case x: SMEBoolean => x.toJava
      case x: SMEBracket => expr.drawTree
      case x: SMEChoice => expr.drawTree
      case x: SMEComposite => expr.drawTree
      case x: SMEDot => {
        val cond = x.containerPath.map(p => _expr_string(p) + " != null").mkString(" && ")
        "(%s) ? %s : null".format(
          cond,
          _expr_string(x.lhs) + "." + _expr_string(x.rhs))
      }
      case x: SMEEval => _expr_string(x.children(0))
      case x: SMEIdentifier => _getter(x)
      case x: SMEMethod => expr.drawTree
      case x: SMENested => expr.drawTree
      case x: SMENull => x.toJava
      case x: SMENumber => x.toJava
      case x: SMEProperty => expr.drawTree
      case x: SMEString => x.toJava
      case x: SMEText => x.toJava
      case x: SMEUnary => expr.drawTree
      case x: SMEBOAdd => _binary_operator_string(x)
      case x: SMEBOAnd => _binary_operator_string(x)
      case x: SMEBODiv => _binary_operator_string(x)
      case x: SMEBOEq => _binary_operator_string(x)
      case x: SMEBOGe => _binary_operator_string(x)
      case x: SMEBOGt => _binary_operator_string(x)
      case x: SMEBOLe => _binary_operator_string(x)
      case x: SMEBOLt => _binary_operator_string(x)
      case x: SMEBOMod => _binary_operator_string(x)
      case x: SMEBOMul => _binary_operator_string(x)
      case x: SMEBONe => _binary_operator_string(x)
      case x: SMEBOOr => _binary_operator_string(x)
      case x: SMEBOSub => _binary_operator_string(x)
    }
  }

  private def _getter(x: SMEIdentifier): String = {
    UJavaString.makeGetterName(x.toJava) + "()"
  }

  private def _binary_operator_string(expr: SMEBinaryOperator): String = {
    _binary_operator_string_lib(expr)
  }

  private def _binary_operator_string_raw(expr: SMEBinaryOperator): String = {
    _expr_string(expr.lhs) + " " + expr.toJavaOperator + " " + _expr_string(expr.rhs)
  }

  private def _binary_operator_string_lib(expr: SMEBinaryOperator): String = {
    "USimpleModeler." + expr.toOperatorMethodName + "(" +
    _expr_string(expr.lhs) + ", " + _expr_string(expr.rhs) + ")"
  }
}
