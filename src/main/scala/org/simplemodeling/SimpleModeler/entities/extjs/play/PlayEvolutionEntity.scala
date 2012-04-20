package org.simplemodeling.SimpleModeler.entities.extjs.play

import java.io.BufferedWriter
import com.asamioffice.goldenport.text.JavaScriptTextMaker
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.extjs._

/**
 * @since   Apr. 21, 2012
 * @version Apr. 21, 2012
 * @author  ASAMI, Tomoharu
 */
class PlayEvolutionEntity(pContext: ExtjsEntityContext) extends PObjectEntity(pContext) {
  val fileSuffix = "sql"

  override protected def write_Content(out: BufferedWriter) {
    val klass = new EvolutionPlayClassDefinition(pContext, PlayEvolutionEntity.this)
    klass.build()
    out.append(klass.toText)
    out.flush
  }  
}
