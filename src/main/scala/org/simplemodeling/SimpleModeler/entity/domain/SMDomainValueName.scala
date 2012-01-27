package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * @since   Oct. 12, 2008
 * @version Apr. 23, 2009
 * @author  ASAMI, Tomoharu
 */
class SMDomainValueName(val dslDomainValueName: DomainValueName) extends SMDomainValue(dslDomainValueName) {
  override def kindName: String = "name"
  override def powertypeName: String = null
}
