package org.simplemodeling.SimpleModeler.entities.extjs.play

import scalaz._
import Scalaz._
import scala.collection.mutable.ArrayBuffer
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.asamioffice.goldenport.text.TextMaker
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 21, 2012
 * @version Apr. 21, 2012
 * @author  ASAMI, Tomoharu
 */
class EvolutionPlayClassDefinition(
  context: PEntityContext,
  pobject: PObjectEntity
) extends PlayClassDefinition(context, pobject) {
  private val _table_names = new ArrayBuffer[String]() // XXX

  override protected def class_open_body {
    sm_code("""# --- !Ups

""")
  }

  override protected def class_close_body {
    sm_code("""# --- !Downs

""")
    for (t <- _table_names) {
      sm_pln("DROP TABLE IF EXISTS %s;", t)
    }
  }

  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    println("EvolutionPlayClassDefinition")
  }
}
