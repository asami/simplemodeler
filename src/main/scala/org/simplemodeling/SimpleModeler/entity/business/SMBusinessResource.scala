package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.business._

/*
 * @since   Nov.  4, 2012
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
class SMBusinessResource(val dslBusinessResource: BusinessResource) extends SMBusinessEntity(dslBusinessResource) {
  override def kindName: String = "business resource"
}
