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
class AndroidEntityEntity(aContext: AndroidEntityContext) extends AndroidObjectEntity(aContext) with PEntityEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new AndroidEntityJavaClassDefinition(aContext, Nil, AndroidEntityEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
