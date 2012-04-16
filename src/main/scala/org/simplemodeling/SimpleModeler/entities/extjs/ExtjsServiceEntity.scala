package org.simplemodeling.SimpleModeler.entities.extjs

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Mar. 31, 2012
 * @version Apr. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsServiceEntity(extjsContext: ExtjsEntityContext) extends ExtjsObjectEntity(extjsContext) with PServiceEntity {
  kindName = "controller"
}
