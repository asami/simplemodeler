package org.simplemodeling.SimpleModeler.entities.extjs

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

/*
 * @since   Mar. 31, 2012
 * @version May. 31, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsRealmEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends PRealmEntity(aIn, aOut, aContext) {
  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)
}

class ExtjsRealmEntityClass extends GEntityClass {
  type Instance_TYPE = ExtjsRealmEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "extjs.d"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new ExtjsRealmEntity(aDataSource, aContext))
}
