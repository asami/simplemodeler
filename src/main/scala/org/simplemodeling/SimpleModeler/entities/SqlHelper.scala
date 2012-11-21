package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Nov.  1, 2012
 * @version Nov. 21, 2012
 * @author  ASAMI, Tomoharu
 */
trait SqlHelper {
  self: GenericClassDefinition =>

  protected def sql_select(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.select
  }

  protected def sql_select(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.select
  }

  protected def sql_select(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_select(x)
      case x: PDocumentEntity => sql_select(x)
    }
  }

  protected def sql_select_literal(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.selectLiteral
  }

  protected def sql_select_literal(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.selectLiteral
  }

  protected def sql_select_literal(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_select_literal(x)
      case x: PDocumentEntity => sql_select_literal(x)
    }
  }
}
