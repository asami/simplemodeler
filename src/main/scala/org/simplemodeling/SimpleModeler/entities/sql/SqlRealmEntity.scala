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
import org.goldenport.entity.content.EntityContent

/*
 * @since   May.  5, 2012
 * @version May.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class SqlRealmEntity(aIn: GDataSource, aOut: GDataSource, val sqlContext: SqlEntityContext) extends PRealmEntity(aIn, aOut, sqlContext) {
  def this(aDataSource: GDataSource, aContext: SqlEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: SqlEntityContext) = this(null, aContext)

  // XXX pull up PRealmEntity
  def getEntityEntity(entity: PEntityEntity): SqlEntityEntity = {
    println("SqlRealmEntity: start")
    print
    println("SqlRealmEntity: end")
    val me = entity.modelEntity
    val pn = sqlContext.makePathname(me.qualifiedName)
    getContent(pn) match {
      case Some(c: EntityContent) => c.entity.asInstanceOf[SqlEntityEntity]
      case _ => sys.error("not found")
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
