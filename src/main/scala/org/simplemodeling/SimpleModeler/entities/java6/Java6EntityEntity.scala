package org.simplemodeling.SimpleModeler.entities.java6

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Aug. 15, 2011
 *  version Aug. 20, 2011
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class Java6EntityEntity(val java6Context: Java6EntityContext) extends JavaObjectEntityBase(java6Context) with PEntityEntity {
  override protected def write_Content(out: BufferedWriter) {
    val aspects = List(BeanValidation303Aspect, Cdi299Aspect, Di330Aspect, CommonAnnotations250Aspect, Jpa317Aspect)
    val klass = new EntityJavaClassDefinition(java6Context, aspects, Java6EntityEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
