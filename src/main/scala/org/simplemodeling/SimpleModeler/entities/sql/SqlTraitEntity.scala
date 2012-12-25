package org.simplemodeling.SimpleModeler.entities.sql

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 25, 2012
 * @version Dec. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SqlTraitEntity(sqlContext: SqlEntityContext) extends SqlObjectEntity(sqlContext) with PTraitEntity {
  override def class_Definition = {
    new Sql92SqlClassDefinition(sqlContext, Nil, SqlTraitEntity.this)
  }
}
