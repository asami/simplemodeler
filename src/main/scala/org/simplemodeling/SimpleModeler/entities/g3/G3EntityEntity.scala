package org.simplemodeling.SimpleModeler.entities.g3

import java.io.BufferedWriter
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/*
 * @since   Jul. 11, 2011
 * @version Jul. 11, 2011
 * @author  ASAMI, Tomoharu
 */
class G3EntityEntity(val g3Context: G3JavaEntityContext) extends JavaObjectEntityBase(g3Context) with PEntityEntity {
  override protected def write_Content(out: BufferedWriter) {
    val klass = new JavaClassDefinition(g3Context, Nil, G3EntityEntity.this) {
      useDocument = true
    }
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
