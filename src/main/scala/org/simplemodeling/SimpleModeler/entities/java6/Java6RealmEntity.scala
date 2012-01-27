package org.simplemodeling.SimpleModeler.entities.java6

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
 * @since   Aug. 15, 2011
 * @version Aug. 19, 2011
 * @author  ASAMI, Tomoharu
 */
class Java6RealmEntity(aIn: GDataSource, aOut: GDataSource, aContext: GEntityContext) extends PRealmEntity(aIn, aOut, aContext) {
  def this(aDataSource: GDataSource, aContext: GEntityContext) = this(aDataSource, aDataSource, aContext)
  def this(aContext: GEntityContext) = this(null, aContext)
}

class Java6RealmEntityClass extends GEntityClass {
  type Instance_TYPE = Java6RealmEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "java.d"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new Java6RealmEntity(aDataSource, aContext))
}
