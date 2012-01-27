package org.simplemodeling.SimpleModeler.entities.java6

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Aug. 15, 2011
 * @version Aug. 20, 2011
 * @author  ASAMI, Tomoharu
 */
class Java6ValueEntity(aContext: Java6EntityContext) extends JavaObjectEntityBase(aContext) with PValueEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new ValueJavaClassDefinition(aContext, Nil, Java6ValueEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
