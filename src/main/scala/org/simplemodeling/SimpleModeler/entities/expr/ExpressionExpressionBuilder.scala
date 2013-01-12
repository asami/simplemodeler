package org.simplemodeling.SimpleModeler.entities.expr

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * Derived from SqlSchemaExpressionBuilder.
 * 
 * @since   Jan. 11, 2013
 * @version Jan. 13, 2013
 * @author  ASAMI, Tomoharu
 */
abstract class ExpressionExpressionBuilder(
  c: PEntityContext, e: SMExpression
) extends ExpressionBuilder(c, e) {
  type RESULT_TYPE = Expression

  protected def root_Result = RootExpression()

  override protected def expr_dot(parent: Expression, expr: Tree[SMExpressionNode], x: SMEDot): Expression = {
    val a = expr_eval(parent, x.lhs)
    val b = DotContExpression(x, a)
    val c = expr_eval(b, x.rhs)
    DotExpression(x, a, c)
  }

  override protected def expr_identifier(parent: Expression, x: SMEIdentifier): Expression = {
    get_association_class(x).map(_expr_identifier_association_class(x)) orElse
    get_attribute_entity(x).map(_expr_identifier_attribute_entity(x)) orElse
    get_attribute(x).map(_expr_identifier_attribute(x)) getOrElse
    sys.error("SqlSchemaExpressionBuilder#expr_identifier")
  }

  private def _expr_identifier_attribute(x: SMEIdentifier)(a: PAttribute): Expression = {
//    println("SqlExpressionBuilder#_expr_identifier_attribute(%s) = %s".format(x, a))
//    current = joinedAttributes.find(_._1 == a).get :: current
//    val t = current.head._2
//    "%s.%s".format(t, context.sqlColumnName(a))
    ValueAttributeExpression(x, a)
  }

  private def _expr_identifier_attribute_entity(x: SMEIdentifier)(a: PAttribute): Expression = {
    val b = a.getEntity match {
      case Some(e) => e
      case None => sys.error("???")
    }
    EntityAttributeExpression(x, a, b)
  }

  private def _expr_identifier_association_class(x: SMEIdentifier)(a: PAttribute): Expression = {
//    println("SqlExpressionBuilder#_expr_identifier_attribute(%s) = %s".format(x, a))
//    current = joinedAttributes.find(_._1 == a).get :: current
//    val t = current.head._2
//    "%s.%s".format(t, context.sqlColumnName(a))
    a.platformParticipation match {
      case Some(ap: AttributeParticipation) => {
        context.sqlAssociationClassCounterAssociation(ap) match {
          case Some(toattr) => {
            toattr.getEntity match {
              case Some(entity) => {
                val src = ap.source.asInstanceOf[PEntityEntity]
                AssociationClassExpression(x, a, src, ap.attribute, toattr, entity)
              }
              case None => sys.error("???")
            }
          }
          case None => sys.error("???")
        }
      }
      case _ => sys.error("???")
    }
  }
}
