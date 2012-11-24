package org.simplemodeling.SimpleModeler.entities.sql

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Nov.  2, 2012
 * @version Nov. 24, 2012
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

  private def _column(attr: PAttribute) = {
    if (attr.isSingle) {
      val columnname = context.sqlColumnName(attr)
      println("SqlMaker#_column(%s/%s) = %s".format(entity.name, attr.name, attr))
      val name = context.asciiName(attr)
      Some("T." + columnname + " as " + name)
    } else {
      None
    }
  }

  private def _joins = {
    val attrs = {
      entity.wholeAttributesWithoutId.
      filter(_.isEntityReference).
      filter(_.isSingle)
    }
    val ts = (1 to attrs.length).map(x => "T" + x)
    val a: Seq[String] = for ((attr, t) <- attrs zip ts) yield {
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
