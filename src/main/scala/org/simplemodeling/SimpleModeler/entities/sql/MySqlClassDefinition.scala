package org.simplemodeling.SimpleModeler.entities.sql

import scalaz._
import Scalaz._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Dec. 29, 2012
 * @version Dec. 29, 2012
 * @author  ASAMI, Tomoharu
 */
class MySqlClassDefinition(
  aContext: SqlEntityContext,
  anAspects: Seq[SqlAspect],
  sqlObject: SqlObjectEntity,
  maker: JavaMaker = null
) extends SqlClassDefinition(aContext, anAspects, sqlObject, maker) {
  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new MySqlClassAttributeDefinition(aContext, anAspects, attr, this, jm_maker)
  }

  override protected def create_Epilogue {
    jm_p("DEFAULT CHARSET=utf8")
  }
}
