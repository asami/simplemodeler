package org.simplemodeling.SimpleModeler.entities.java6

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Aug. 15, 2011
 *  version Aug. 20, 2011
 * @version Nov.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class Java6EntityPartEntity(val java6Context: Java6EntityContext) extends JavaObjectEntityBase(java6Context) with PEntityPartEntity {
  override protected def write_Content(out: BufferedWriter) {
    val aspects = List(new BeanValidation303Aspect, new Cdi299Aspect, new Di330Aspect, new CommonAnnotations250Aspect, new Jpa317Aspect)
    val klass = new EntityJavaClassDefinition(java6Context, aspects, Java6EntityPartEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
