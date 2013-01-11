package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.recorder.GRecordable
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Jan. 11, 2013
 * @version Jan. 11, 2013
 * @author  ASAMI, Tomoharu
 */
abstract class StringExpressionBuilder(
  c: PEntityContext, e: SMExpression
) extends ExpressionBuilder(c, e) {
  type RESULT_TYPE = String

  override protected def expr_boolean(expr: Tree[SMExpressionNode], x: SMEBoolean): String = {
    x.toJava
  }

  override protected def expr_bracket(expr: Tree[SMExpressionNode], x: SMEBracket): String = {
    expr.drawTree
  }

  override protected def expr_choice(expr: Tree[SMExpressionNode], x: SMEChoice): String = {
     expr.drawTree
  }

  override protected def expr_composite(expr: Tree[SMExpressionNode], x: SMEComposite): String = {
     expr.drawTree
  }

  override protected def expr_dot(expr: Tree[SMExpressionNode], x: SMEDot): String = {
    val cond = x.containerPath.map(p => expr_string(p) + " != null").mkString(" && ")
    "(%s) ? %s : null".format(
      cond,
      expr_string(x.lhs) + "." + expr_string(x.rhs))
  }

  override protected def expr_method(expr: Tree[SMExpressionNode], x: SMEMethod): String = {
    expr.drawTree    
  }

  override protected def expr_nested(expr: Tree[SMExpressionNode], x: SMENested): String = {
    expr.drawTree
  }

  override protected def expr_null(expr: Tree[SMExpressionNode], x: SMENull): String = {
    x.toJava
  }

  override protected def expr_number(x: SMENumber): String = {
    x.toJava
  }

  override protected def expr_property(expr: Tree[SMExpressionNode], x: SMEProperty): String = {
    expr.drawTree
  }

  override protected def expr_string(x: SMEString): String = {
     x.toJava
  }

  override protected def expr_text(x: SMEText): String = {
    x.toJava
  }

  override protected def expr_unary(expr: Tree[SMExpressionNode], x: SMEUnary): String = {
     expr.drawTree
  }

  override protected def expr_identifier(x: SMEIdentifier): String = {
    _get_attribute(x) orElse
    _get_association_class(x) getOrElse {
      record_warning("XXX")
      "null"
    }
  }

  override protected def expr_binary_operator(expr: SMEBinaryOperator): String = {
    _binary_operator_string(expr)
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
