package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 21, 2012
 * @version Jan. 15, 2013
 * @author  ASAMI, Tomoharu
 */
class JavaExpressionBuilder(
  c: PEntityContext,
  val klass: JavaClassDefinition,
  val attr: JavaClassAttributeDefinition,
  expr: SMExpression
) extends StringExpressionBuilder(c, expr) {
  val attributes = klass.wholeAttributeDefinitions.map(_.attr)

  override protected def expr_boolean(parent: String, expr: Tree[SMExpressionNode], x: SMEBoolean): String = {
    x.toJava
  }

  override protected def expr_bracket(parent: String, expr: Tree[SMExpressionNode], x: SMEBracket): String = {
    expr.drawTree
  }

  override protected def expr_choice(parent: String, expr: Tree[SMExpressionNode], x: SMEChoice): String = {
     expr.drawTree
  }

  override protected def expr_composite(parent: String, expr: Tree[SMExpressionNode], x: SMEComposite): String = {
     expr.drawTree
  }

  override protected def expr_dot(parent: String, expr: Tree[SMExpressionNode], x: SMEDot): String = {
    val cond = x.containerPath.map(p => expr_eval(parent, p) + " != null").mkString(" && ")
    "(%s) ? %s : null".format(
      cond,
      expr_eval(parent, x.lhs) + "." + expr_eval(parent, x.rhs))
  }

  override protected def expr_method(parent: String, expr: Tree[SMExpressionNode], x: SMEMethod): String = {
    expr.drawTree    
  }

  override protected def expr_nested(parent: String, expr: Tree[SMExpressionNode], x: SMENested): String = {
    expr.drawTree
  }

  override protected def expr_null(parent: String, expr: Tree[SMExpressionNode], x: SMENull): String = {
    x.toJava
  }

  override protected def expr_number(parent: String, x: SMENumber): String = {
    x.toJava
  }

  override protected def expr_property(parent: String, expr: Tree[SMExpressionNode], x: SMEProperty): String = {
    expr.drawTree
  }

  override protected def expr_string(parent: String, x: SMEString): String = {
     x.toJava
  }

  override protected def expr_text(parent: String, x: SMEText): String = {
    x.toJava
  }

  override protected def expr_unary(parent: String, expr: Tree[SMExpressionNode], x: SMEUnary): String = {
     expr.drawTree
  }

  override protected def expr_identifier(parent: String, x: SMEIdentifier): String = {
    _get_attribute(x) orElse
    _get_association_class(x) getOrElse {
      record_warning("XXX")
      "null"
    }
  }

  override protected def expr_binary_operator(parent: String, expr: SMEBinaryOperator): String = {
    _binary_operator_string(parent, expr)
  }

  private def _binary_operator_string(parent: String, expr: SMEBinaryOperator): String = {
    _binary_operator_string_lib(parent, expr)
  }

  private def _binary_operator_string_raw(parent: String, expr: SMEBinaryOperator): String = {
    expr_eval(parent, expr.lhs) + " " + expr.toJavaOperator + " " + expr_eval(parent, expr.rhs)
  }

  private def _binary_operator_string_lib(parent: String, expr: SMEBinaryOperator): String = {
    "USimpleModeler." + expr.toOperatorMethodName + "(" +
    expr_eval(parent, expr.lhs) + ", " + expr_eval(parent, expr.rhs) + ")"
  }

  private def _get_attribute(x: SMEIdentifier): Option[String] = {
    attributes.exists(_.name == x.name) option {
      UJavaString.makeGetterName(x.toJava) + "()"
    }
  }

  private def _get_association_class(x: SMEIdentifier): Option[String] = {
    for (a <- attributes.find(is_association_class(x.name))) yield {
      UJavaString.makeGetterName(a.name) + "().get(0)"
    }
  }
}
