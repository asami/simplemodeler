package org.simplemodeling.SimpleModeler.entities.java6

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Aug. 15, 2011
 *  version Aug. 20, 2011
 *  version Dec. 15, 2011
 *  version Feb. 20, 2012
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class Java6EntityEntity(val java6Context: Java6EntityContext) extends JavaObjectEntityBase(java6Context) with PEntityEntity {
  override protected def write_Content(out: BufferedWriter) {
    val aspects = List(new BeanValidation303Aspect, new Cdi299Aspect, new Di330Aspect, new CommonAnnotations250Aspect, new Jpa317Aspect)
    val klass = new EntityJavaClassDefinition(java6Context, aspects, Java6EntityEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }

  override def toString() = {
    "Java6EntityEntity(%s)".format(name)
  }
}

