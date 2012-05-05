package org.simplemodeling.SimpleModeler.entities.sql

import scala.xml.{Node, Elem}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.FileDataSource
import org.goldenport.entity.content.GContent
import org.goldenport.entity.locator.EntityLocator
import org.goldenport.sdoc.structure._
import org.goldenport.entities.workspace.TreeWorkspaceNode
import org.goldenport.value.GTreeBase
import org.simplemodeling.SimpleModeler.entities.PRealmEntity
import org.simplemodeling.SimpleModeler.entities.PEntityEntity

/*
 * @since   May.  5, 2012
 * @version May.  5, 2012
 * @author  ASAMI, Tomoharu
 */
class SqlRealmEntity(aIn: GDataSource, aOut: GDataSource, val sqlContext: SqlEntityContext) extends PRealmEntity(aIn, aOut, sqlContext) {
  def this(aDataSource: GDataSource, aContext: SqlEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: SqlEntityContext) = this(null, aContext)
  
  def getSqlEntity(entity: PEntityEntity): SqlEntityEntity = {
    new SqlEntityEntity(sqlContext) {
      name = sqlContext.sqlName(entity)
      modelObject = entity.modelEntity
    }
  }
}
/*
class SqlRealmEntityClass extends GEntityClass {
  type Instance_TYPE = SqlRealmEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "sql.d"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new SqlRealmEntity(aDataSource, aContext))
}
*/
