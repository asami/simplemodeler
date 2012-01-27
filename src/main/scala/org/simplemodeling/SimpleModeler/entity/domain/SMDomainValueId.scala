package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * @since   Oct. 12, 2008
 * @version Apr. 23, 2009
 * @author  ASAMI, Tomoharu
 */
class SMDomainValueId(val dslDomainValueId: DomainValueId) extends SMDomainValue(dslDomainValueId) {
  override def kindName: String = "id"
  override def powertypeName: String = null
}
