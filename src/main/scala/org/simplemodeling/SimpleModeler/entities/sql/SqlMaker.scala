package org.simplemodeling.SimpleModeler.entities.sql

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Nov.  2, 2012
 * @version Nov. 27, 2012
 * @author  ASAMI, Tomoharu
 */
trait SqlMaker {
  def context: PEntityContext

  def ddl: String
  def ddlLiteral: String
  def select: String
  def selectLiteral: String
  def update: String
  def updateLiteral: String
  def insert: String
  def insertLiteral: String
  def delete: String
  def deleteLiteral: String
}

class EntitySqlMaker(val context: PEntityContext)(val entity: PEntityEntity) extends SqlMaker {
  val joinedAttributes: Seq[(PAttribute, String)] = {
    val a = entity.wholeAttributesWithoutId.
    filter(_.isEntityReference).
    filter(_.isSingle)
    val b = (1 to a.length).map(x => "T" + x)
    a zip b
  }

  def ddl = {
    "create"
  }

  def ddlLiteral = {
    UJavaString.stringLiteral(ddl)
  }

  def select = {
    val tablename = context.sqlTableName(entity)
    "select " + _columns + " from " + tablename + " T " + _joins
  }

  private def _columns = {
    entity.wholeAttributes.flatMap(_column).mkString(", ")
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

  private def _column(attr: PAttribute): List[String] = {
    if (attr.isMulti) return Nil
    val columnname = context.sqlColumnName(attr)
    println("SqlMaker#_column(%s/%s) = %s".format(entity.name, attr.name, attr))
    val name = context.asciiName(attr)
    val c1 = _column_as("T." + columnname, name)
    attr match {
      case EntityReference(c) => List(c1, c)
      case LabelReference(c) => List(c1, c)
      case _ => List(c1)
    }
  }

  protected def _column_as(column: String, alias: String): String = {
    // 'coalesce' is workaround for MySQL Java Driver
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
      "left outer join " + context.sqlTableName(attr) + " " + t + " on " +
      "T." + context.sqlColumnName(attr) + "=" + t + "." + context.sqlJoinColumnName(attr)
    }
//    a.mkString("\n", " ", "\n")
    a.mkString(" ")
  }

  def selectLiteral = {
    UJavaString.stringLiteral(select)
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

class DocumentSqlMaker(val context: PEntityContext)(val document: PDocumentEntity) extends SqlMaker {
  def ddl = {
    "create"
  }

  def ddlLiteral = {
    UJavaString.stringLiteral(ddl)
  }

  def select = {
    "select %s from %s".format("columns", "table")
  }

  def selectLiteral = {
    UJavaString.stringLiteral(select)
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
