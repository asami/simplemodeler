package org.simplemodeling.SimpleModeler.entities

import scala.xml.{Node, Elem}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.FileDataSource
import org.goldenport.entity.content.GContent
import org.goldenport.entity.locator.EntityLocator
import org.goldenport.sdoc.structure._
import org.goldenport.entities.workspace.TreeWorkspaceNode
import org.goldenport.value.GTreeBase

/*
 * @since   Apr. 22, 2011
 * @version Apr. 22, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class PRealmEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GTreeContainerEntityBase(aIn, aOut, aContext) {
  type DataSource_TYPE = GDataSource
  override type TreeNode_TYPE = GTreeContainerEntityNode
  override def is_Text_Output = true

  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)

  override protected def open_Entity_Create() {
    set_root(new TreeWorkspaceNode(null, this))
  }
}

abstract class PRealmEntityClass extends GEntityClass {
}
