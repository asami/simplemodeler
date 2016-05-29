package org.simplemodeling.SimpleModeler.entity

import scalaz._, Scalaz._
import de.odysseus.el._
import de.odysseus.el.tree.{ Tree => JTree, Node => JNode, _}
import de.odysseus.el.tree.impl._
import de.odysseus.el.tree.impl.ast._
import de.odysseus.el.util._

/*
 * @since   Oct. 31, 2012
 * @version May. 29, 2016
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
    Tree.node(o, o.children.toStream)
  }

  private def _children(x: JNode): Seq[Tree[SMExpressionNode]] = {
    for (i <- 0 until x.getCardinality) yield build_tree(x.getChild(i))
  }

  protected def build_boolean(x: AstBoolean): Tree[SMExpressionNode] = {
    _build_leaf_boolean(SMEBoolean, x)
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
    build_dot_node(x)
  }

  protected def build_dot_node(x: AstDot): Tree[SMExpressionNode] = {
    val name = x.toString.dropWhile(x => x == '.' || x == ' ')
    val id = Tree.leaf(SMEIdentifier(name)).asInstanceOf[Tree[SMExpressionNode]]
    val r = x.getChild(0) match {
      case c: AstDot => {
        val a = build_dot_node(c).asInstanceOf[Tree[SMEDot]]
        val b = a.rootLabel
        val cs = Seq[Tree[SMExpressionNode]](
          b.lhs,
          _build_dot_node(Seq(b.rhs, id)).asInstanceOf[Tree[SMExpressionNode]]
        )
        _build_dot_node(cs)
      }
      case i: AstIdentifier => {
        _build_dot_node(Seq(build_identifier(i), id))
      }
    }
    r.asInstanceOf[Tree[SMExpressionNode]]
  }

  private def _build_dot_node(cs: Seq[Tree[SMExpressionNode]]): Tree[SMEDot] = {
    Tree.node(SMEDot(cs), cs.toStream).asInstanceOf[Tree[SMEDot]]
  }

  protected def build_eval(x: AstEval): Tree[SMExpressionNode] = {
    _build_node(SMEEval, x)
  }

  protected def build_identifier(x: AstIdentifier): Tree[SMExpressionNode] = {
    Tree.leaf(SMEIdentifier(x.getName))
  }

  protected def build_method(x: AstMethod): Tree[SMExpressionNode] = {
    Tree.leaf(SMEMethod())
  }

  protected def build_nested(x: AstNested): Tree[SMExpressionNode] = {
    _build_node(SMENested, x)
  }

  protected def build_null(x: AstNull): Tree[SMExpressionNode] = {
    Tree.leaf(SMENull())
  }

  protected def build_number(x: AstNumber): Tree[SMExpressionNode] = {
    _build_leaf_number(SMENumber, x)
  }

  protected def build_property(x: AstProperty): Tree[SMExpressionNode] = {
    Tree.leaf(SMEProperty())
  }  

  protected def build_string(x: AstString): Tree[SMExpressionNode] = {
    _build_leaf_string(SMEString, x)
  }  

  protected def build_text(x: AstText): Tree[SMExpressionNode] = {
    _build_leaf_string(SMEText, x)
  }  

  protected def build_unary(x: AstUnary): Tree[SMExpressionNode] = {
    _build_node(SMEUnary, x)
  }  

  private def _build_leaf_boolean(
    f: Boolean => SMExpressionNode,
    x: AstNode
  ): Tree[SMExpressionNode] = {
    val b = x.getValue(_bindings, _context, classOf[Boolean])
    b match {
      case x: java.lang.Boolean => Tree.leaf(f(x))
      case x => sys.error(x.toString) // XXX
    }
  }

  private def _build_leaf_number(
    f: Number => SMExpressionNode,
    x: AstNode
  ): Tree[SMExpressionNode] = {
    val b = x.getValue(_bindings, _context, classOf[Number])
    b match {
      case x: java.lang.Number => Tree.leaf(f(x))
      case x => sys.error(x.toString) // XXX
    }
  }

  private def _build_leaf_string(
    f: String => SMExpressionNode,
    x: AstNode
  ): Tree[SMExpressionNode] = {
    val b = x.getValue(_bindings, _context, classOf[String])
    b match {
      case x: java.lang.String => Tree.leaf(f(x))
      case x => sys.error(x.toString) // XXX
    }
  }

  private def _build_node(
    f: Seq[Tree[SMExpressionNode]] => SMExpressionNode,
    x: AstNode
  ): Tree[SMExpressionNode] = {
    val cs = _children(x)
    _build_node(f, cs)
  }

  private def _build_node(
    f: Seq[Tree[SMExpressionNode]] => SMExpressionNode,
    cs: Seq[Tree[SMExpressionNode]]
  ): Tree[SMExpressionNode] = {
    Tree.node(f(cs), cs.toStream)
  }
}
