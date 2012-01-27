package org.simplemodeling.SimpleModeler.entities.g3

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import java.io.BufferedWriter

/*
 * @since   Aug. 25, 2011
 * @version Aug. 25, 2011
 * @author  ASAMI, Tomoharu
 */
class G3ScriptEntity(val g3Context: G3EntityContext) extends ScalaObjectEntityBase(g3Context) {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new G3ScriptScalaClassDefinition(scalaContext, Nil, G3ScriptEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
