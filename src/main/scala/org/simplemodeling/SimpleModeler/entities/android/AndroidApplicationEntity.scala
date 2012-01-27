package org.simplemodeling.SimpleModeler.entities.android

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import java.io.BufferedWriter

/*
 * @since   Aug.  7, 2011
 * @version Aug.  7, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidApplicationEntity(val androidContext: AndroidEntityContext) extends JavaObjectEntityBase(androidContext) {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new AndroidApplicationJavaClassDefinition(androidContext, Nil, AndroidApplicationEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
