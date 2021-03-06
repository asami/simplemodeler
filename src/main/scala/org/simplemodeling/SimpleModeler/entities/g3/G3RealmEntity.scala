package org.simplemodeling.SimpleModeler.entities.g3

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
 * @since   Jul. 11, 2011
 * @version Jul. 11, 2011
 * @author  ASAMI, Tomoharu
 */
class G3RealmEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends PRealmEntity(aIn, aOut, aContext) {
  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)
}

class G3RealmEntityClass extends GEntityClass {
  type Instance_TYPE = G3RealmEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "g3.d"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new G3RealmEntity(aDataSource, aContext))
}
