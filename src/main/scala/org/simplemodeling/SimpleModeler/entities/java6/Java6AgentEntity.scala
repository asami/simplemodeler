package org.simplemodeling.SimpleModeler.entities.java6

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import java.io.BufferedWriter


/*
 * @since   Aug. 15, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
class Java6AgentEntity(val java6Context: Java6EntityContext) extends JavaObjectEntityBase(java6Context) {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new AgentJavaClassDefinition(javaContext, Nil, Java6AgentEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
