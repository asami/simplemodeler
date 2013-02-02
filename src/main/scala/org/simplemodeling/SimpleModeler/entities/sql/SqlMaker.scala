package org.simplemodeling.SimpleModeler.entities.sql

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.expr.SqlExpressionBuilder

/**
 * @since   Nov.  2, 2012
 *  version Dec. 23, 2012
 *  version Jan. 17, 2013
 * @version Feb.  3, 2013
 * @author  ASAMI, Tomoharu
 */
trait SqlMaker {
  def context: PEntityContext

  def create: String
  def createLiteral: String
  def select: String
  def selectFetch: String
  def selectLiteral: String
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
  val attributeCandicates = entity.wholeAttributes
  val attributes = attributeCandicates.filter(isTarget)
  println("EntitySqlMaker(%s): whole = %s, filtered = %s".format(
    entity.name,
    attributeCandicates.map(x => x.name + "/" + entity.getDisplayVisibilities(x)),
    attributes))
  val joinedAttributeCandidates: Seq[(PAttribute, String)] = {
    val a = attributeCandicates.filter(_is_join_candidate)
    val b = (1 to a.length).map(x => "T" + x)
    a zip b
  }
  val joinedAttributes: Seq[(PAttribute, String)] = {
//    println("EntitySqlMaker#joinedAttributes in (%s) = %s".format(entity.name, attributes))
    val a = attributeCandicates.filter(_is_join)
    val b = (1 to a.length).map(x => "T" + x)
//    println("EntitySqlMaker#joinedAttributes out (%s) = %s".format(entity.name, a))
    a zip b
  }

  private def _is_join_candidate(a: PAttribute) = {
    a.isEntityReference
  }

  private def _is_join(a: PAttribute) = {
    a.isEntityReference &&
    (isTarget(a) || _is_refered(a))
/*
    if (a.isEntityReference) true
    else if (a.isDerive) {
      println("SqlMaker#_is_join(%s/%s) = %s".format(entity.name, a.name, a.deriveExpression))
      true
    } else false
*/
  }

  private def _is_refered(a: PAttribute) = {
    attributeCandicates.exists(_is_refer(a))
  }

  private def _is_refer(target: PAttribute)(source: PAttribute): Boolean = {
    val a = for (expr <- source.deriveExpression) yield {
      if (target == source) false
      else new SqlExpressionBuilder(expr.model)(context, joinedAttributeCandidates).isTarget(target)
    }
    a | false
  }

  def create = {
    "create"
  }

  def createLiteral = {
    UJavaString.stringLiteral(create)
  }

  def select = {
    val tablename = context.sqlTableName4SingleTableInheritance(entity)
    "select " + _columns + " from " + tablename + " T " + _joins + _wheres
  }

  def selectFetch = {
    "???"
  }

  private def _columns = {
    (_columns_attributes ++ _columns_joins).mkString(", ")
  }

  private def _columns_attributes = {
    attributes.flatMap(_column("T"))
  }

  private def _columns_joins = {
    entity.joinEntities.flatMap(_columns_join)
  }

  private def _columns_join(o: PEntityEntity) = {
    println("SqlMaker#_columns_join = %s".format(o))
    val dups = attributes.map(_.name)
    o.wholeAttributesWithoutId.filterNot(x => dups.contains(x.name)).flatMap(_column("TJ"))
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
//        println("SqlMaker:ExpressionReference = " + column)
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
            context.sqlAssociationClassCounterAssociation(p) match {
              case Some(counter) => {
                val name = context.sqlColumnName(a)
                val column = context.sqlJoinColumnName(counter)
                val b = _column_as(t + "." + column, name)
                val name2 = context.sqlNameAlias(a)
                val column2 = context.sqlNameColumnName(counter)
                val c = _column_as(t + "." + column2, name2)
            List(b, c)
              }
              case None => Nil
            }
          }
        }
      }
      a.flatMap(identity)
    }
  }

  private def _column(alias: String)(attr: PAttribute): List[String] = {
//    if (attr.isMulti) return Nil
    val columnname = context.sqlColumnName(attr)
//    println("SqlMaker#_column(%s/%s) = %s".format(entity.name, attr.name, attr))
    val name = context.asciiName(attr)
    val c1 = _column_as(alias + "." + columnname, name)
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

  private def _joins = {
    (_joins_participation ++ _joins_join).mkString(" ")
  }

  private def _joins_participation: Seq[String] = {
    for ((attr, t) <- joinedAttributes) yield {
      attr.platformParticipation match {
        case Some(s: AttributeParticipation) => _join_association_class(attr, t, s)
        case Some(s) => sys.error("???")
        case _ => _join_master(attr, t)
      }
    }
  }

  // TODO inheritance handling: table, where
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
    val assoctable = context.sqlTableName4SingleTableInheritance(assoc)
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

  // TODO inheritance handling: table, where
  private def _join_association_class_itself(ownattr: PAttribute, t: String) = {
    val assoctable = context.sqlTableName(ownattr)
    val assocjoincolumn = context.sqlColumnName(ownattr)
    "left outer join %s %s on T.%s=%s.%s".format(
      assoctable, t,
      context.sqlIdColumnName(entity), t, assocjoincolumn)
  }

  private def _joins_join: Seq[String] = {
    entity.joinEntities.map(_joins_join_entity)
  }

  private def _joins_join_entity(e: PEntityEntity) = {
//    println("SqlMaker#_joins_join_entity = " + e.name)
    val tj = "TJ"
    "left outer join %s %s on T.%s=%s.%s".format(
      context.sqlTableName(e), tj,
      context.sqlJoinColumnName(entity),
      tj, context.sqlJoinBackReferenceColumnName(entity, e))
  }

  private def _wheres = {
    List(_wheres_inheritance).flatten match {
      case Nil => ""
      case xs => " where " + xs.mkString(" and ")
    }
  }

  private def _wheres_inheritance = {
    entity.getInheritancePowertypeAttributeValue.map {
      case (attr, value) => {
        val columnname = context.sqlColumnName(attr)
        value match {
          case Left(v) => "T.%s='%s'".format(columnname, v)
          case Right(v) => "T.%s=%s".format(columnname, v)
        }
      }
    }
  }

  def selectLiteral = {
    UJavaString.stringLiteral(select)
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

class DocumentSqlMaker(val document: PDocumentEntity, isTarget: PAttribute => Boolean = _ => true)(implicit val context: PEntityContext) extends SqlMaker {
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
