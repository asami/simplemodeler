package org.simplemodeling.SimpleModeler.entity

import scalaz._, Scalaz._
import de.odysseus.el._
import de.odysseus.el.tree.{ Tree => JTree, Node => JNode, _}
import de.odysseus.el.tree.impl._
import de.odysseus.el.tree.impl.ast._
import de.odysseus.el.util._

/*
 * @since   Oct. 31, 2012
 * @version Oct. 31, 2012
 * @author  ASAMI, Tomoharu
 */
object SMExpressionJuel {
  import de.odysseus.el.tree.impl._
  private val _factory = new ExpressionFactoryImpl()
  private val _context = new SimpleContext(); // more on this here...
  private val _store = new TreeStore(new Builder(), new Cache(100));
  private val _bindings = new Bindings(Array(), Array())

  def juelTree(expr: String) = {
    _store.get(expr)
  }

  def juelExpression(expr: String) = {
//    _factory.createValueExpression(_context, "${" + expr + "}", classOf[Object])
    _factory.createValueExpression(_context, expr, classOf[Object])
  }

  def tree(expr: String): Tree[SMExpressionNode] = {
    val t = juelTree(expr)
    build_tree(t.getRoot)
  }

  protected def build_tree(node: JNode): Tree[SMExpressionNode] = {
    /*
     * AstNode
     * AstInvocation -> AstMethod, AstFunction
     * AstLiteral -> AstBoolean, AstNull, AstNumber, AstString
     * AstRightValue -> AstBinary, AstChoice, AstComposite, AstInvocation, AstLiteral, AstNested, AstUnary
     */
    node match {
      case x: AstBinary => build_binary(x)
      case x: AstBoolean => build_boolean(x)
      case x: AstBracket => build_bracket(x)
      case x: AstChoice => build_choice(x)
      case x: AstComposite => build_composite(x)
      case x: AstDot => build_dot(x)
      case x: AstEval => build_eval(x)
      case x: AstIdentifier => build_identifier(x)
      case x: AstMethod => build_method(x)
      case x: AstNested => build_nested(x)
      case x: AstNull => build_null(x)
      case x: AstNumber => build_number(x)
      case x: AstProperty => build_property(x)
      case x: AstString => build_string(x)
      case x: AstText => build_text(x)
      case x: AstUnary => build_unary(x)
    }
  }

  protected def build_binary(x: AstBinary): Tree[SMExpressionNode] = {
    import AstBinary._
    val o = x.getOperator match {
      case ADD => SMEBOAdd(_children(x))
      case AND => SMEBOAnd(_children(x))
      case DIV => SMEBODiv(_children(x))
      case EQ => SMEBOEq(_children(x))
      case GE => SMEBOGe(_children(x))
      case GT => SMEBOGt(_children(x))
      case LE => SMEBOLe(_children(x))
      case LT => SMEBOLt(_children(x))
      case MOD => SMEBOMod(_children(x))
      case MUL => SMEBOMul(_children(x))
      case NE => SMEBONe(_children(x))
      case OR => SMEBOOr(_children(x))
      case SUB => SMEBOSub(_children(x))
    }
    node(o, o.children.toStream)
  }

  private def _children(x: JNode): Seq[Tree[SMExpressionNode]] = {
    for (i <- 0 until x.getCardinality) yield build_tree(x.getChild(i))
  }

  protected def build_boolean(x: AstBoolean): Tree[SMExpressionNode] = {
    val b = x.getValue(_bindings, _context, classOf[java.lang.Boolean])
    b match {
      case x: java.lang.Boolean => leaf(SMEBoolean(x))
      case x => sys.error(x.toString) // XXX
    }
  }

  protected def build_bracket(x: AstBracket): Tree[SMExpressionNode] = {
    _build_node(SMEBracket, x)
  }

  protected def build_choice(x: AstChoice): Tree[SMExpressionNode] = {
    _build_node(SMEChoice, x)
  }

  protected def build_composite(x: AstComposite): Tree[SMExpressionNode] = {
    _build_node(SMEComposite, x)
  }

  protected def build_dot(x: AstDot): Tree[SMExpressionNode] = {
    _build_node(SMEDot, x)
  }

  protected def build_eval(x: AstEval): Tree[SMExpressionNode] = {
    _build_node(SMEEval, x)
  }

  protected def build_identifier(x: AstIdentifier): Tree[SMExpressionNode] = {
    leaf(SMEIdentifier())
  }

  protected def build_method(x: AstMethod): Tree[SMExpressionNode] = {
    leaf(SMEMethod())
  }

  protected def build_nested(x: AstNested): Tree[SMExpressionNode] = {
    _build_node(SMENested, x)
  }

  protected def build_null(x: AstNull): Tree[SMExpressionNode] = {
    leaf(SMENull())
  }

  protected def build_number(x: AstNumber): Tree[SMExpressionNode] = {
    leaf(SMENumber())
  }

  protected def build_property(x: AstProperty): Tree[SMExpressionNode] = {
    leaf(SMEProperty())
  }  

  protected def build_string(x: AstString): Tree[SMExpressionNode] = {
    leaf(SMEString())
  }  

  protected def build_text(x: AstText): Tree[SMExpressionNode] = {
    leaf(SMEText())
  }  

  protected def build_unary(x: AstUnary): Tree[SMExpressionNode] = {
    _build_node(SMEUnary, x)
  }  

  private def _build_node(
    f: Seq[Tree[SMExpressionNode]] => SMExpressionNode,
    x: AstNode
  ): Tree[SMExpressionNode] = {
    val cs = _children(x)
    node(f(cs), cs.toStream)
  }
}
