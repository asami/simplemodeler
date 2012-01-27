package org.simplemodeling.SimpleModeler.entities.gaej

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
 * @since   Apr. 10, 2009
 * @version Apr. 10, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeJavaRealmEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends GTreeContainerEntityBase(aIn, aOut, aContext) {
  type DataSource_TYPE = GDataSource
  override type TreeNode_TYPE = GTreeContainerEntityNode
  override def is_Text_Output = true

  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)

  override protected def open_Entity_Create() {
    set_root(new TreeWorkspaceNode(null, this))
  }
}

class GaeJavaRealmEntityClass extends GEntityClass {
  type Instance_TYPE = GaeJavaRealmEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "gaej.d"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeJavaRealmEntity(aDataSource, aContext))
}
