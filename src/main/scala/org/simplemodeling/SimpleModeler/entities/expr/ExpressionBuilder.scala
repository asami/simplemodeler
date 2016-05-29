package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.recorder.GRecordable
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 21, 2012
 *  version Jan. 12, 2013
 * @version May.  7, 2016
 * @author  ASAMI, Tomoharu
 */
abstract class ExpressionBuilder(val context: PEntityContext, val expr: SMExpression) extends GRecordable {
  implicit def SMExpressionShow: Show[SMExpressionNode] = Show.showA
  type RESULT_TYPE

  setup_Recordable(context)

  def attributes: Seq[PAttribute]
  protected def root_Result: RESULT_TYPE

  def build: RESULT_TYPE = {
    expr_eval(root_Result, expr.tree)
  }

  protected def expr_eval(parent: RESULT_TYPE, expr: Tree[SMExpressionNode]): RESULT_TYPE = {
    expr.rootLabel match {
      case x: SMEBoolean => expr_boolean(parent, expr, x)
      case x: SMEBracket => expr_bracket(parent, expr, x)
      case x: SMEChoice => expr_choice(parent, expr, x)
      case x: SMEComposite => expr_composite(parent, expr, x)
      case x: SMEDot => expr_dot(parent, expr, x)
      case x: SMEEval => expr_eval(parent, x.children(0))
      case x: SMEIdentifier => expr_identifier(parent, x)
      case x: SMEMethod => expr_method(parent, expr, x)
      case x: SMENested => expr_nested(parent, expr, x)
      case x: SMENull => expr_null(parent, expr, x)
      case x: SMENumber => expr_number(parent, x)
      case x: SMEProperty => expr_property(parent, expr, x)
      case x: SMEString => expr_string(parent, x)
      case x: SMEText => expr_text(parent, x)
      case x: SMEUnary => expr_unary(parent, expr, x)
      case x: SMEBOAdd => expr_binary_operator(parent, x)
      case x: SMEBOAnd => expr_binary_operator(parent, x)
      case x: SMEBODiv => expr_binary_operator(parent, x)
      case x: SMEBOEq => expr_binary_operator(parent, x)
      case x: SMEBOGe => expr_binary_operator(parent, x)
      case x: SMEBOGt => expr_binary_operator(parent, x)
      case x: SMEBOLe => expr_binary_operator(parent, x)
      case x: SMEBOLt => expr_binary_operator(parent, x)
      case x: SMEBOMod => expr_binary_operator(parent, x)
      case x: SMEBOMul => expr_binary_operator(parent, x)
      case x: SMEBONe => expr_binary_operator(parent, x)
      case x: SMEBOOr => expr_binary_operator(parent, x)
      case x: SMEBOSub => expr_binary_operator(parent, x)
    }
  }

  protected def expr_boolean(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMEBoolean): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_bracket(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMEBracket): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_choice(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMEChoice): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_composite(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMEComposite): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_dot(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMEDot): RESULT_TYPE = {
    expr_eval(parent, x.lhs)
    expr_eval(parent, x.rhs)
  }

  protected def expr_method(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMEMethod): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_nested(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMENested): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_null(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMENull): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_number(parent: RESULT_TYPE, x: SMENumber): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_property(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMEProperty): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_string(parent: RESULT_TYPE, x: SMEString): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_text(parent: RESULT_TYPE, x: SMEText): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_unary(parent: RESULT_TYPE, expr: Tree[SMExpressionNode], x: SMEUnary): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_identifier(parent: RESULT_TYPE, x: SMEIdentifier): RESULT_TYPE = {
    sys.error("???")
  }

  protected def expr_binary_operator(parent: RESULT_TYPE, expr: SMEBinaryOperator): RESULT_TYPE = {
    sys.error("???")
  }

  protected final def get_attribute(x: SMEIdentifier): Option[PAttribute] = {
    attributes.find(_.name == x.name)
  }

  protected final def get_attribute_entity(x: SMEIdentifier): Option[PAttribute] = {
    attributes.find(is_attribute_entity(x.name))
  }

  protected final def is_attribute_entity(name: String)(a: PAttribute): Boolean = {
    a.name == name && a.isEntity
  }

  protected final def get_association_class(x: SMEIdentifier): Option[PAttribute] = {
    attributes.find(is_association_class(x.name))
  }

  protected final def is_association_class(name: String)(a: PAttribute): Boolean = {
    a.platformParticipation.map(_.source.name == name) | false
  }
}
