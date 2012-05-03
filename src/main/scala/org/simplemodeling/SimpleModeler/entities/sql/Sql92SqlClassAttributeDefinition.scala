package org.simplemodeling.SimpleModeler.entities.sql

import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   May.  3, 2012
 * @version May.  3, 2012
 * @author  ASAMI, Tomoharu
 */
class Sql92SqlClassAttributeDefinition(
  aContext: SqlEntityContext,
  anAspects: Seq[SqlAspect],
  attr: PAttribute,
  owner: SqlClassDefinition,
  maker: JavaMaker = null
) extends SqlClassAttributeDefinition(aContext, anAspects, attr, owner, maker) {
}
