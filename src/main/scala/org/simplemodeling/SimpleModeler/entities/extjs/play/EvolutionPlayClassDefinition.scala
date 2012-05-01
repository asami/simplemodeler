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
 * @version Apr. 26, 2012
 * @author  ASAMI, Tomoharu
 */
class EvolutionPlayClassDefinition(
  context: PEntityContext,
  pobject: PObjectEntity
) extends PlayClassDefinition(context, pobject) {
  private val _table_names = new ArrayBuffer[String]() // XXX

  override protected def class_open_body {
    sm_code("""# --- !Ups

CREATE TABLE User (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isAdmin boolean NOT NULL,
    PRIMARY KEY (id)
);

""")
  }

  override protected def class_close_body {
    sm_code("""# --- !Downs

DROP TABLE IF EXISTS User;

""")
    for (t <- _table_names) {
      sm_pln("DROP TABLE IF EXISTS %s;", t)
    }
  }

  override protected def package_methods_Entity(entity: SMDomainEntity) {
    println("EvolutionPlayClassDefinition: model entity")
  }

  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    println("EvolutionPlayClassDefinition: platform entity")
  }
}
