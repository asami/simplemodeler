package org.simplemodeling.SimpleModeler.entities.extjs

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Dec. 25, 2012
 * @version Dec. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsTraitEntity(extjsContext: ExtjsEntityContext) extends ExtjsObjectEntity(extjsContext) with PTraitEntity {
  kindName = DEFAULT_MODEL_KIND

  override def class_Definition = {
    new ModelExtjsClassDefinition(extjsContext, Nil, ExtjsTraitEntity.this)
  }
}
