package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 21, 2012
 * @version Dec. 21, 2012
 * @author  ASAMI, Tomoharu
 */
class JavaExpressionBuilder(
  c: PEntityContext,
  val klass: JavaClassDefinition,
  val attr: JavaClassAttributeDefinition,
  val expr: SMExpression
) extends ExpressionBuilder(c) {
  private val _attrs = klass.wholeAttributeDefinitions.map(_.attr)

  def build: String = {
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
    _get_attribute(x) orElse
    _get_association_class(x) getOrElse {
      record_warning("XXX")
      "null"
    }
  }

  private def _get_attribute(x: SMEIdentifier): Option[String] = {
    val name = x.name
    _attrs.exists(_.name == name) option {
      UJavaString.makeGetterName(x.toJava) + "()"
    }
  }

  private def _get_association_class(x: SMEIdentifier): Option[String] = {
    for (a <- _attrs.find(_is_association_class(x.name))) yield {
      UJavaString.makeGetterName(a.name) + "().get(0)"
    }
  }

  private def _is_association_class(name: String)(a: PAttribute): Boolean = {
    a.platformParticipation.map(_.source.name == name) | false
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
