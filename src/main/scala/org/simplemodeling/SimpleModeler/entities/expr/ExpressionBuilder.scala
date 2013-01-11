package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.recorder.GRecordable
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 21, 2012
 * @version Jan. 11, 2013
 * @author  ASAMI, Tomoharu
 */
abstract class ExpressionBuilder(val context: PEntityContext, val expr: SMExpression) extends GRecordable {
  implicit def SMExpressionShow: Show[SMExpressionNode] = showA
  type RESULT_TYPE

  setup_Recordable(context)

  def attributes: Seq[PAttribute]

  def build: RESULT_TYPE = {
    expr_string(expr.tree)
  }

  protected def expr_string(expr: Tree[SMExpressionNode]): RESULT_TYPE = {
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
      case x: SMEBOAdd => expr_binary_operator(x)
      case x: SMEBOAnd => expr_binary_operator(x)
      case x: SMEBODiv => expr_binary_operator(x)
      case x: SMEBOEq => expr_binary_operator(x)
      case x: SMEBOGe => expr_binary_operator(x)
      case x: SMEBOGt => expr_binary_operator(x)
      case x: SMEBOLe => expr_binary_operator(x)
      case x: SMEBOLt => expr_binary_operator(x)
      case x: SMEBOMod => expr_binary_operator(x)
      case x: SMEBOMul => expr_binary_operator(x)
      case x: SMEBONe => expr_binary_operator(x)
      case x: SMEBOOr => expr_binary_operator(x)
      case x: SMEBOSub => expr_binary_operator(x)
    }
  }

  protected def expr_boolean(expr: Tree[SMExpressionNode], x: SMEBoolean): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_bracket(expr: Tree[SMExpressionNode], x: SMEBracket): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_choice(expr: Tree[SMExpressionNode], x: SMEChoice): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_composite(expr: Tree[SMExpressionNode], x: SMEComposite): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_dot(expr: Tree[SMExpressionNode], x: SMEDot): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_method(expr: Tree[SMExpressionNode], x: SMEMethod): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_nested(expr: Tree[SMExpressionNode], x: SMENested): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_null(expr: Tree[SMExpressionNode], x: SMENull): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_number(x: SMENumber): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_property(expr: Tree[SMExpressionNode], x: SMEProperty): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_string(x: SMEString): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_text(x: SMEText): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_unary(expr: Tree[SMExpressionNode], x: SMEUnary): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_identifier(x: SMEIdentifier): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_binary_operator(expr: SMEBinaryOperator): RESULT_TYPE = {
    sys.error("???")
  }
}
