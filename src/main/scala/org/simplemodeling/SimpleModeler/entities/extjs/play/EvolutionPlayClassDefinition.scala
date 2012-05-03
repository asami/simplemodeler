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

/**
 * @since   Apr. 21, 2012
 * @version May.  3, 2012
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
    entities |>| _create_table
    sm_pln("""# --- !Downs""")
    sm_pln()
    entities |>| _drop_table
  }

  private def _create_table(entity: PEntityEntity) {
    var name = pContext.sqlName(entity)
    sm_block("CREATE TABLE %s (", name)(");") {
      entity.wholeAttributes.init |>| _column_colon
      entity.wholeAttributes.last |> _column
    }
  }

  private def _column_colon(attr: PAttribute) {
    sm_p(pContext.sqlColumn(attr))
    sm_pln(",")
  }

  private def _column(attr: PAttribute) {
    sm_pln(pContext.sqlColumn(attr))
  }

/*
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isAdmin boolean NOT NULL,
    PRIMARY KEY (id)
);
*/

  private def _drop_table(entity: PEntityEntity) {
    var name = pContext.sqlName(entity)
    sm_pln("DROP TABLE IF EXISTS %s;", name)
  }
}
