package org.simplemodeling.SimpleModeler.entities.g3

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
class G3ApplicationEntity(val g3Context: G3EntityContext) extends ScalaObjectEntityBase(g3Context) {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new G3ApplicationScalaClassDefinition(scalaContext, Nil, G3ApplicationEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
