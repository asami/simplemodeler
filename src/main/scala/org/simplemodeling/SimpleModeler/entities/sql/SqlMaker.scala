package org.simplemodeling.SimpleModeler.entities.sql

import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Nov.  2, 2012
 * @version Nov. 21, 2012
 * @author  ASAMI, Tomoharu
 */
trait SqlMaker {
  def select: String
  def selectLiteral: String
}

class EntitySqlMaker(entity: PEntityEntity) extends SqlMaker {
  def select = {
    "select %s from %s".format("columns", "table")
  }

  def selectLiteral = {
    UJavaString.stringLiteral(select)
  }
}

class DocumentSqlMaker(document: PDocumentEntity) extends SqlMaker {
  def select = {
    "select %s from %s".format("columns", "table")
  }

  def selectLiteral = {
    UJavaString.stringLiteral(select)
  }
}
