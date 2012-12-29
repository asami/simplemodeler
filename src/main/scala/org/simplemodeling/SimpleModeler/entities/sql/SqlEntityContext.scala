package org.simplemodeling.SimpleModeler.entities.sql

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._
import org.goldenport.Goldenport.{Application_Version, Application_Version_Build}
import org.goldenport.entity._
import org.goldenport.service.GServiceContext
import com.asamioffice.goldenport.text.{UString, UJavaString}

/**
 * @since   May.  3, 2012
 * @version Dec. 29, 2012
 * @author  ASAMI, Tomoharu
 */
class SqlEntityContext(ectx: GEntityContext, sctx: GServiceContext) extends PEntityContext(ectx, sctx) {
  var sqlKind = "mysql" // for RDS

  def createClassDefinition(entity: SqlEntityEntity): SqlClassDefinition = {
    sqlKind match {
      case "mysql" => new MySqlClassDefinition(this, Nil, entity)
      case _ => new Sql92SqlClassDefinition(this, Nil, entity)
    }
  }
}
