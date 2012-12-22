package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.recorder.GRecordable
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 21, 2012
 * @version Dec. 22, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class ExpressionBuilder(val context: PEntityContext, val expr: SMExpression) extends GRecordable {
  implicit def SMExpressionShow: Show[SMExpressionNode] = showA

  setup_Recordable(context)

  def attributes: Seq[PAttribute]

  def build: String = {
    expr_string(expr.tree)
  }

  protected def expr_string(expr: Tree[SMExpressionNode]): String = {
    expr.rootLabel match {
      case x: SMEBoolean => expr_boolean(expr, x)
      case x: SMEBracket => expr_bracket(expr, x)
      case x: SMEChoice => expr_choice(expr, x)
      case x: SMEComposite => expr_composite(expr, x)
      case x: SMEDot => expr_dot(expr, x)
      case x: SMEEval => expr_string(x.children(0))
      case x: SMEIdentifier => expr_identifier(x)
      case x: SMEMethod => expr_method(expr, x)
      case x: SMENested => expr_nested(expr, x)
      case x: SMENull => expr_null(expr, x)
      case x: SMENumber => expr_number(x)
      case x: SMEProperty => expr_property(expr, x)
      case x: SMEString => expr_string(x)
      case x: SMEText => expr_text(x)
      case x: SMEUnary => expr_unary(expr, x)
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

  protected def expr_boolean(expr: Tree[SMExpressionNode], x: SMEBoolean): String = {
    x.toJava
  }

  protected def expr_bracket(expr: Tree[SMExpressionNode], x: SMEBracket): String = {
    expr.drawTree
  }

  protected def expr_choice(expr: Tree[SMExpressionNode], x: SMEChoice): String = {
     expr.drawTree
  }

  protected def expr_composite(expr: Tree[SMExpressionNode], x: SMEComposite): String = {
     expr.drawTree
  }

  protected def expr_dot(expr: Tree[SMExpressionNode], x: SMEDot): String = {
    val cond = x.containerPath.map(p => expr_string(p) + " != null").mkString(" && ")
    "(%s) ? %s : null".format(
      cond,
      expr_string(x.lhs) + "." + expr_string(x.rhs))
  }

  protected def expr_method(expr: Tree[SMExpressionNode], x: SMEMethod): String = {
    expr.drawTree    
  }

  protected def expr_nested(expr: Tree[SMExpressionNode], x: SMENested): String = {
    expr.drawTree
  }

  protected def expr_null(expr: Tree[SMExpressionNode], x: SMENull): String = {
    x.toJava
  }

  protected def expr_number(x: SMENumber): String = {
    x.toJava
  }

  protected def expr_property(expr: Tree[SMExpressionNode], x: SMEProperty): String = {
    expr.drawTree
  }

  protected def expr_string(x: SMEString): String = {
     x.toJava
  }

  protected def expr_text(x: SMEText): String = {
    x.toJava
  }

  protected def expr_unary(expr: Tree[SMExpressionNode], x: SMEUnary): String = {
     expr.drawTree
  }

  protected def expr_identifier(x: SMEIdentifier): String = {
    _get_attribute(x) orElse
    _get_association_class(x) getOrElse {
      record_warning("XXX")
      "null"
    }
  }

  protected final def get_attribute(x: SMEIdentifier): Option[PAttribute] = {
    attributes.find(_.name == x.name)
  }

  private def _get_attribute(x: SMEIdentifier): Option[String] = {
    attributes.exists(_.name == x.name) option {
      UJavaString.makeGetterName(x.toJava) + "()"
    }
  }

  protected final def get_association_class(x: SMEIdentifier): Option[PAttribute] = {
    attributes.find(_is_association_class(x.name))
  }

  private def _get_association_class(x: SMEIdentifier): Option[String] = {
    for (a <- attributes.find(_is_association_class(x.name))) yield {
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
    expr_string(expr.lhs) + " " + expr.toJavaOperator + " " + expr_string(expr.rhs)
  }

  private def _binary_operator_string_lib(expr: SMEBinaryOperator): String = {
    "USimpleModeler." + expr.toOperatorMethodName + "(" +
    expr_string(expr.lhs) + ", " + expr_string(expr.rhs) + ")"
  }
}
