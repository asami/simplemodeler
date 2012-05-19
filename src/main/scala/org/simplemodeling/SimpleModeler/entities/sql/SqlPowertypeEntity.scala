package org.simplemodeling.SimpleModeler.entities.sql

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   May. 17, 2012
 * @version May. 17, 2012
 * @author  ASAMI, Tomoharu
 */
class SqlPowertypeEntity(sqlContext: SqlEntityContext) extends SqlObjectEntity(sqlContext) with PPowertypeEntity {
}
