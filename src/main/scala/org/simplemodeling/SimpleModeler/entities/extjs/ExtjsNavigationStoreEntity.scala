package org.simplemodeling.SimpleModeler.entities.extjs

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Jun.  3, 2012
 * @version Jun.  3, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsNavigationStoreEntity(aContext: ExtjsEntityContext) extends ExtjsObjectEntity(aContext) {
  kindName = "store"

  override protected def write_Content(out: BufferedWriter) {
    val klass = new NavigationStoreExtjsClassDefinition(aContext, Nil, ExtjsNavigationStoreEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
