package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Nov.  1, 2012
 *  version Dec. 23, 2012
 * @version Feb.  2, 2013
 * @author  ASAMI, Tomoharu
 */
trait SqlHelper {
  self: GenericClassDefinition =>

  protected def sql_create(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.create
  }

  protected def sql_create(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.create
  }

  protected def sql_create(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_create(x)
      case x: PDocumentEntity => sql_create(x)
    }
  }

  protected def sql_create_literal(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.createLiteral
  }

  protected def sql_create_literal(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.createLiteral
  }

  protected def sql_create_literal(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_create_literal(x)
      case x: PDocumentEntity => sql_create_literal(x)
    }
  }

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

  protected def sql_select_grid_literal(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity, GridVisibility.isVisible(entity) _)
    maker.selectLiteral
  }

  protected def sql_select_grid_literal(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc, GridVisibility.isVisible(doc) _)
    maker.selectLiteral
  }

  protected def sql_select_grid_literal(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_select_grid_literal(x)
      case x: PDocumentEntity => sql_select_grid_literal(x)
    }
  }

  protected def sql_select_fetch_literal(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.selectFetchLiteral
  }

  protected def sql_select_fetch_literal(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.selectFetchLiteral
  }

  protected def sql_select_fetch_literal(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_select_fetch_literal(x)
      case x: PDocumentEntity => sql_select_fetch_literal(x)
    }
  }

  protected def sql_update(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.update
  }

  protected def sql_update(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.update
  }

  protected def sql_update(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_update(x)
      case x: PDocumentEntity => sql_update(x)
    }
  }

  protected def sql_update_literal(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.updateLiteral
  }

  protected def sql_update_literal(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.updateLiteral
  }

  protected def sql_update_literal(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_update_literal(x)
      case x: PDocumentEntity => sql_update_literal(x)
    }
  }

  protected def sql_delete(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.delete
  }

  protected def sql_delete(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.delete
  }

  protected def sql_delete(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_delete(x)
      case x: PDocumentEntity => sql_delete(x)
    }
  }

  protected def sql_delete_literal(entity: PEntityEntity): String = {
    val maker = pContext.sqlMaker(entity)
    maker.deleteLiteral
  }

  protected def sql_delete_literal(doc: PDocumentEntity): String = {
    val maker = pContext.sqlMaker(doc)
    maker.deleteLiteral
  }

  protected def sql_delete_literal(o: PObjectEntity): String = {
    o match {
      case x: PEntityEntity => sql_delete_literal(x)
      case x: PDocumentEntity => sql_delete_literal(x)
    }
  }
}
