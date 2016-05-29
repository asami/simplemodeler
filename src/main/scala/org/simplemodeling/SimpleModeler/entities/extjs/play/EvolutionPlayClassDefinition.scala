package org.simplemodeling.SimpleModeler.entities.extjs.play

import scalaz._
import Scalaz._
import scala.collection.mutable.ArrayBuffer
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.asamioffice.goldenport.text.TextMaker
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.sql._

/**
 * @since   Apr. 21, 2012
 *  version May. 14, 2012
 *  version Dec. 26, 2012
 * @version May.  7, 2016
 * @author  ASAMI, Tomoharu
 */
class EvolutionPlayClassDefinition(
  context: PEntityContext,
  pobject: PObjectEntity
) extends PlayClassDefinition(context, pobject) {
  protected def class_open_body = null
  protected def class_close_body = null

  override protected def package_methods_Entity(entity: SMDomainEntity) {
    println("EvolutionPlayClassDefinition: model entity")
  }

  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    println("EvolutionPlayClassDefinition: platform entity")
  }

  override protected def object_auxiliary {
    val entities = entities_in_module
    sm_pln("""# --- !Ups""")
    sm_pln()
    entities.foreach(_create_table)
    sm_pln("""# --- !Downs""")
    sm_pln()
    entities.foreach(_drop_table)
  }

  private def _create_table(entity: PEntityEntity) {
    val sqlentity = pContext.getSqlEntity(entity)
    println("Evolution: " + sqlentity.toText)
    sm_pln(sqlentity.toText)
  }

  private def _drop_table(entity: PEntityEntity) {
    var name = pContext.sqlTableName(entity)
    sm_pln("DROP TABLE IF EXISTS %s;", name)
  }
}
