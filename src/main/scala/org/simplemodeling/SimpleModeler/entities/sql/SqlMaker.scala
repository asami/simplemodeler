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
