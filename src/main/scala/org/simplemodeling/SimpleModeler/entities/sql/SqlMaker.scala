package org.simplemodeling.SimpleModeler.entities.sql

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.expr.SqlExpressionBuilder

/**
 * @since   Nov.  2, 2012
 * @version Dec. 22, 2012
 * @author  ASAMI, Tomoharu
 */
trait SqlMaker {
  def context: PEntityContext

  def create: String
  def createLiteral: String
  def select: String
  def selectGrid: String
  def selectFetch: String
  def selectLiteral: String
  def selectGridLiteral: String
  def selectFetchLiteral: String
  def update: String
  def updateLiteral: String
  def insert: String
  def insertLiteral: String
  def delete: String
  def deleteLiteral: String
}

class EntitySqlMaker(
  val entity: PEntityEntity,
  val isTarget: PAttribute => Boolean = _ => true
)(implicit val context: PEntityContext) extends SqlMaker {
  val attributes = entity.wholeAttributes.filter(isTarget)
  val joinedAttributes: Seq[(PAttribute, String)] = {
    println("EntitySqlMaker#joinedAttributes in (%s) = %s".format(entity.name, attributes))
    val a = attributes.filter(_is_join)
//    filter(_.isEntityReference).
//    filter(_.isSingle)
    val b = (1 to a.length).map(x => "T" + x)
    println("EntitySqlMaker#joinedAttributes out (%s) = %s".format(entity.name, a))
    a zip b
  }

  private def _is_join(a: PAttribute) = {
    a.isEntityReference
  }

  def create = {
    "create"
  }

  def createLiteral = {
    UJavaString.stringLiteral(create)
  }

  def select = {
    val tablename = context.sqlTableName(entity)
    "select " + _columns + " from " + tablename + " T " + _joins
  }

  def selectGrid = {
    sys.error("???")
  }

  def selectFetch = {
    sys.error("???")
  }

  private def _columns = {
    attributes.flatMap(_column).mkString(", ")
  }

  object EntityReference {
    def unapply(attr: PAttribute): Option[String] = {
      for ((a, t) <- joinedAttributes.find(_._1 == attr)) yield {
        val name = context.sqlNameAlias(a)
        val column = context.sqlNameColumnName(a)
        _column_as(t + "." + column, name)
      }
    }
  }

  object LabelReference {
    def unapply(attr: PAttribute): Option[String] = {
      val name = context.sqlNameAlias(attr)
      val column = "T." + context.sqlColumnName(attr)
      val candidates: Option[Seq[PEnumeration]] = attr.attributeType match {
        case p: PPowertypeType => Option(p.powertype.kinds)
        case s: PStateMachineType => Option(s.statemachine.states)
        case _ => None
      }
      for (cs <- candidates) yield {
        val whens = for (k <- cs) yield {
          "when %s=%s then %s".format(column, k.sqlValue, k.sqlLabel)
        }
        "(case " + whens.mkString(" ") + " else " + column + " end) as " + name
      }
    }
  }

  object ExpressionReference {
    def unapply(attr: PAttribute): Option[String] = {
      for {
        expr <- attr.deriveExpression
      } yield {
        val name = context.sqlColumnName(attr)
        val column = new SqlExpressionBuilder(expr.model)(context, joinedAttributes).build
        _column_as(column, name)
      }
    }
  }

  object AssociationClassReference {
    def unapply(attr: PAttribute): Option[List[String]] = {
      val a: Option[Option[List[String]]] = for {
        (a, t) <- joinedAttributes.find(_._1 == attr)
      } yield {
        attr.platformParticipation.collect {
          case p: AttributeParticipation => {
            val name = context.sqlColumnName(a)
            val column = context.sqlJoinColumnName(p.attribute)
            val b = _column_as(t + "." + column, name)
            val name2 = context.sqlNameAlias(a)
            val column2 = context.sqlNameColumnName(p.attribute)
            val c = _column_as(t + "." + column2, name2)
            List(b, c)
          }
        }
      }
      a.flatMap(identity)
    }
  }

  private def _column(attr: PAttribute): List[String] = {
//    if (attr.isMulti) return Nil
    val columnname = context.sqlColumnName(attr)
//    println("SqlMaker#_column(%s/%s) = %s".format(entity.name, attr.name, attr))
    val name = context.asciiName(attr)
    val c1 = _column_as("T." + columnname, name)
    attr match {
      case ExpressionReference(c) => List(c)
      case AssociationClassReference(cs) => cs
      case EntityReference(c) => List(c1, c)
      case LabelReference(c) => List(c1, c)
      case _ => List(c1)
    }
  }

  protected def _column_as(column: String, alias: String): String = {
    // Using 'coalesce' is workaround for MySQL Java Driver
    "coalesce(%s) as %s".format(column, alias)
  }

  private def _is_entity_reference(attr: PAttribute) = {
    
  }

  private def _is_powertype(attr: PAttribute) = {
    
  }

  private def _is_statemachine(attr: PAttribute) = {
    
  }

  private def _joins = {
    val a: Seq[String] = for ((attr, t) <- joinedAttributes) yield {
      attr.platformParticipation match {
        case Some(s: AttributeParticipation) => _join_association_class(attr, t, s)
        case Some(s) => sys.error("???")
        case _ => _join_master(attr, t)
      }
    }
//    a.mkString("\n", " ", "\n")
    a.mkString(" ")
  }

  private def _join_master(attr: PAttribute, t: String) = {
    "left outer join " + context.sqlTableName(attr) + " " + t + " on " +
    "T." + context.sqlColumnName(attr) + "=" + t + "." + context.sqlJoinColumnName(attr)
  }

  private def _join_association_class(attr: PAttribute, t: String, p: AttributeParticipation): String = {
    context.sqlAssociationClassCounterAssociation(p) match {
      case Some(s) => _join_association_class_direct(attr, t, p, s)
      case None => _join_association_class_itself(attr, t)
    }
  }

  private def _join_association_class_direct(ownattr: PAttribute, t: String, participation: AttributeParticipation, targetattr: PAttribute) = {
    val assoc = participation.source
    val tx = t + "x"
    val assoctable = context.sqlTableName(assoc)
    val assocjoincolumn = context.sqlColumnName(participation.attribute)
    val targettable = context.sqlTableName(targetattr)
    val targetcolumn = context.sqlColumnName(targetattr)
    val a = "left outer join %s %s on T.%s=%s.%s".format(
      assoctable, tx,
      context.sqlIdColumnName(entity), tx, assocjoincolumn)
    val b = "left outer join %s %s on %s.%s=%s.%s".format(
      targettable, t,
      tx, targetcolumn,
      t, context.sqlJoinColumnName(targetattr))
    a + " " + b
  }

  private def _join_association_class_itself(ownattr: PAttribute, t: String) = {
    val assoctable = context.sqlTableName(ownattr)
    val assocjoincolumn = context.sqlColumnName(ownattr)
    "left outer join %s %s on T.%s=%s.%s".format(
      assoctable, t,
      context.sqlIdColumnName(entity), t, assocjoincolumn)
  }

  def selectLiteral = {
    UJavaString.stringLiteral(select)
  }

  def selectGridLiteral = {
    UJavaString.stringLiteral(selectGrid)
  }

  def selectFetchLiteral = {
    UJavaString.stringLiteral(selectFetch)
  }

  def insert = {
    "insert"
  }

  def insertLiteral = {
    UJavaString.stringLiteral(insert)
  }

  def update = {
    "update"
  }

  def updateLiteral = {
    UJavaString.stringLiteral(update)
  }

  def delete = {
    "delete"
  }

  def deleteLiteral = {
    UJavaString.stringLiteral(delete)
  }
}

class DocumentSqlMaker(val document: PDocumentEntity)(implicit val context: PEntityContext) extends SqlMaker {
  def create = {
    "create"
  }

  def createLiteral = {
    UJavaString.stringLiteral(create)
  }

  def select = {
    "select %s from %s".format("columns", "table")
  }

  def selectLiteral = {
    UJavaString.stringLiteral(select)
  }

  def selectGrid = {
    "select %s from %s".format("columns", "table")
  }

  def selectGridLiteral = {
    UJavaString.stringLiteral(selectGrid)
  }

  def selectFetch = {
    "select %s from %s".format("columns", "table")
  }

  def selectFetchLiteral = {
    UJavaString.stringLiteral(selectFetch)
  }

  def insert = {
    "insert"
  }

  def insertLiteral = {
    UJavaString.stringLiteral(insert)
  }

  def update = {
    "update"
  }

  def updateLiteral = {
    UJavaString.stringLiteral(update)
  }

  def delete = {
    "delete"
  }

  def deleteLiteral = {
    UJavaString.stringLiteral(delete)
  }
}
