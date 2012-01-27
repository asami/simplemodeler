package org.simplemodeling.SimpleModeler.entities.android

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import java.io.BufferedWriter

/*
 * @since   Aug.  7, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidAgentEntity(val androidContext: AndroidEntityContext) extends JavaObjectEntityBase(androidContext) with PAgentEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new AndroidAgentJavaClassDefinition(androidContext, Nil, AndroidAgentEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
