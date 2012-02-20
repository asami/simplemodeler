package org.simplemodeling.SimpleModeler.entities.java6

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import java.io.BufferedWriter

/*
 * @since   Aug. 15, 2011
 * @version Feb. 20, 2012
 * @author  ASAMI, Tomoharu
 */
class Java6PowertypeEntity(java6Context: Java6EntityContext) extends JavaObjectEntityBase(java6Context) with PPowertypeEntity {
  override protected def write_Content(out: BufferedWriter) {
    val aspects = List(new BeanValidation303Aspect, new Cdi299Aspect, new Di330Aspect, new CommonAnnotations250Aspect, new Jpa317Aspect)
    val klass = new PowertypeJavaClassDefinition(java6Context, aspects, Java6PowertypeEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
