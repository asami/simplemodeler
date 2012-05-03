package org.simplemodeling.SimpleModeler.entities.sql

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   May.  3, 2012
 * @version May.  3, 2012
 * @author  ASAMI, Tomoharu
 */
class SqlEntityEntity(aContext: SqlEntityContext) extends SqlObjectEntity(aContext) with PEntityEntity {
  def modelEntity: SMEntity = null // XXX

  override protected def write_Content(out: BufferedWriter) {
    val klass = new Sql92SqlClassDefinition(aContext, Nil, SqlEntityEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
