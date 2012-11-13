package org.simplemodeling.SimpleModeler.entities.java6

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import java.io.BufferedWriter

/*
 * @since   Nov. 14, 2012
 * @version Nov. 14, 2012
 * @author  ASAMI, Tomoharu
 */
class Java6StateMachineEntity(java6Context: Java6EntityContext) extends JavaObjectEntityBase(java6Context) with PStateMachineEntity {
  override protected def write_Content(out: BufferedWriter) {
    val aspects = List(new BeanValidation303Aspect, new Cdi299Aspect, new Di330Aspect, new CommonAnnotations250Aspect, new Jpa317Aspect)
    val klass = new StateMachineJavaClassDefinition(java6Context, aspects, Java6StateMachineEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }
}
