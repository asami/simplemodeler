package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.business._

/*
 * @since   Nov.  4, 2012
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
class SMBusinessEntity(val dslBusinessEntity: BusinessEntity) extends SMEntity(dslBusinessEntity) {
  override def typeName: String = "business entity"
}
