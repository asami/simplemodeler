package org.simplemodeling.SimpleModeler.entities.sql

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Nov.  1, 2012
 * @version Nov.  1, 2012
 * @author  ASAMI, Tomoharu
 */
class SqlDocumentEntity(aContext: SqlEntityContext) extends SqlObjectEntity(aContext) with PDocumentEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new Sql92SqlClassDefinition(aContext, Nil, SqlDocumentEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
