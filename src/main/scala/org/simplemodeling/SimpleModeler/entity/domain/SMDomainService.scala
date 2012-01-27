package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * Oct. 12, 2008
 * Oct. 18, 2008
 */
class SMDomainService(val dslDomainService: DomainService) extends SMService(dslDomainService) {
  override def kindName: String = null
  override def powertypeName: String = null
}
