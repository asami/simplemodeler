package org.simplemodeling.SimpleModeler.entities.sql

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   May.  3, 2012
 *  version May.  3, 2012
 * @version Nov.  1, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SqlObjectEntity(val sqlContext: SqlEntityContext) extends PObjectEntity(sqlContext) {
  val fileSuffix = "sql"
}
