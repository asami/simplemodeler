package org.simplemodeling.SimpleModeler.entities.sql

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
    "select " + _columns + " from " + tablename + _joins
  }

  private def _columns = {
    entity.wholeAttributes.map(_column).mkString(", ")
  }

  private def _column(attr: PAttribute) = {
    val columnname = context.sqlColumnName(attr)
    val name = context.asciiName(attr)
    if (name == columnname) name
    else columnname + " as " + name
  }

  private def _joins = {
    val attrs = entity.wholeAttributesWithoutId.filter(_.isEntityReference)
    val ts = (1 to attrs.length).map(x => "T" + x)
    for ((attr, t) <- attrs zip ts) yield {
      "left outer join " + context.sqlTableName(attr) + " " + t + " on " +
      "T." + context.sqlColumnName(attr) + "=" + "t." + context.sqlJoinColumnName(attr)
    }.mkString("\n", " ", "\n")
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
