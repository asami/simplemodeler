package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * Jan. 20, 2009
 * Jan. 20, 2009
 */
class SMDomainPowertype(val dslDomainPowertype: DomainPowertype) extends SMPowertype(dslDomainPowertype) {
  override def kindName: String = null
  override def powertypeName: String = null
}
