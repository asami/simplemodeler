package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.dsl.business._

/*
 * @since   Nov.  4, 2012
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
class SMBusinessActor(val dslActor: BusinessActor) extends SMBusinessEntity(dslActor) {
  override def kindName: String = "business actor"
}
