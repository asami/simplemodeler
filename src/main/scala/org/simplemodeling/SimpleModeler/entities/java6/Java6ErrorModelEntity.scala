package org.simplemodeling.SimpleModeler.entities.java6

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import java.io.BufferedWriter

/*
 * @since   Aug. 15, 2011
 * @version Aug. 19, 2011
 * @author  ASAMI, Tomoharu
 */
class Java6ErrorModelEntity(aContext: Java6EntityContext) extends Java6ObjectEntity(aContext) with PErrorModelEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new ErrorModelJavaClassDefinition(javaContext, Nil, Java6ErrorModelEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
