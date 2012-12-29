package org.simplemodeling.SimpleModeler.entities.sql

import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Dec. 29, 2012
 * @version Dec. 29, 2012
 * @author  ASAMI, Tomoharu
 */
class MySqlClassAttributeDefinition(
  aContext: SqlEntityContext,
  anAspects: Seq[SqlAspect],
  attr: PAttribute,
  owner: SqlClassDefinition,
  maker: JavaMaker = null
) extends SqlClassAttributeDefinition(aContext, anAspects, attr, owner, maker) {
}
