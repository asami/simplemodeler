package org.simplemodeling.SimpleModeler.entities.sql

import scalaz._
import Scalaz._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   May.  3, 2012
 * @version May.  3, 2012
 * @author  ASAMI, Tomoharu
 */
class Sql92SqlClassDefinition(
  aContext: SqlEntityContext,
  anAspects: Seq[SqlAspect],
  sqlObject: SqlObjectEntity,
  maker: JavaMaker = null
) extends SqlClassDefinition(aContext, anAspects, sqlObject, maker) {
  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new Sql92SqlClassAttributeDefinition(aContext, anAspects, attr, this)
  }
}
