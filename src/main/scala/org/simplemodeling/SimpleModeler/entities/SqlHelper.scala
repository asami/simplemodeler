package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Nov.  1, 2012
 * @version Nov.  1, 2012
 * @author  ASAMI, Tomoharu
 */
trait SqlHelper {
  self: GenericClassDefinition =>

  protected def sql_select(entity: PEntityEntity): String = {
    val sqlentity = self.pContext.getSqlEntity(entity)
    sqlentity.toText
  }

  protected def sql_select(doc: PDocumentEntity): String = {
    val sqlentity = self.pContext.getSqlEntity(doc)
    sqlentity.toText
  }

  protected def sql_select(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_select(x)
      case x: PDocumentEntity => sql_select(x)
    }
  }
}
