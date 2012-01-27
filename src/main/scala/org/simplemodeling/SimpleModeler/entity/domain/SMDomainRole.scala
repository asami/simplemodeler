package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * Sep. 15, 2008
 * Feb. 27, 2009
 */
class SMDomainRole(val dslDomainRole: DomainRole) extends SMDomainEntity(dslDomainRole) with SMRole {
//  override def powertypeName: String = null
}
