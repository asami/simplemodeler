package org.simplemodeling.SimpleModeler.entities.android

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Jul.  3, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidValueEntity(val androidContext: AndroidEntityContext) extends JavaObjectEntityBase(androidContext) with PValueEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new ValueJavaClassDefinition(androidContext, Nil, AndroidValueEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
