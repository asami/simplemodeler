package org.simplemodeling.SimpleModeler.entities.java6

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Aug. 15, 2011
 *  version Aug. 20, 2011
 * @version Nov. 10, 2012
 * @author  ASAMI, Tomoharu
 */
class Java6ServiceEntity(val java6Context: Java6EntityContext) extends JavaObjectEntityBase(java6Context) with PServiceEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new ServiceJavaClassDefinition(java6Context, Nil, Java6ServiceEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
