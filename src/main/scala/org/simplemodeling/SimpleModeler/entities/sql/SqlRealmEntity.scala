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
import org.simplemodeling.SimpleModeler.entities.PDocumentEntity
import org.goldenport.entity.content.EntityContent

/*
 * @since   May.  5, 2012
 *  version May.  6, 2012
 * @version Nov.  2, 2012
 * @author  ASAMI, Tomoharu
 */
class SqlRealmEntity(aIn: GDataSource, aOut: GDataSource, val sqlContext: SqlEntityContext) extends PRealmEntity(aIn, aOut, sqlContext) {
  def this(aDataSource: GDataSource, aContext: SqlEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: SqlEntityContext) = this(null, aContext)

  def getEntityEntity(entity: PEntityEntity): SqlEntityEntity = {
    println("SqlRealmEntity: start")
    dump()
    println("SqlRealmEntity: end")
    val me = entity.modelEntity
    val pn = sqlContext.makePathname(me.qualifiedName)
    getContent(pn) match {
      case Some(c: EntityContent) => c.entity.asInstanceOf[SqlEntityEntity]
      case _ => sys.error("not found")
    }
  }

  /**
   * Current behaior of SimpleModel2ProgramRealmTransformerBase is insuffient
   * about generating of SqlDocumentEntity.
   * Consequently, this method always return None.
   * SimpleModel2ProgramRealmTransformerBase creates PDocumentEntity.
   * This method invoked by DocumentJavaClassDefinition via PEntityContext.getSqlEntity.
   */
  def getDocumentEntity(entity: PDocumentEntity): Option[SqlDocumentEntity] = {
    println("SqlRealmEntity: start")
    dump()
    println("SqlRealmEntity: end")
    entity.modelEntityOption.flatMap(me => {
      println("SqlRealmEntity#getDocumentEntity: " + me)
      val pn = sqlContext.makePathname(me.qualifiedName)
      println("SqlRealmEntity#getDocumentEntity: " + pn)
      getContent(pn).collect {
        case c: EntityContent => c
      } collect {
        case d: SqlDocumentEntity => d
      }
    })
  }
}
/*
class SqlRealmEntityClass extends GEntityClass {
  type Instance_TYPE = SqlRealmEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "sql.d"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new SqlRealmEntity(aDataSource, aContext))
}
*/


