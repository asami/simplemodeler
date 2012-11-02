package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Nov.  1, 2012
 * @version Nov.  2, 2012
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
}
