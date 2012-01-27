package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * Sep. 15, 2008
 * Jan. 18, 2009
 */
class SMDomainSummary(val dslDomainSummary: DomainSummary) extends SMDomainEntity(dslDomainSummary) {
  override def kindName: String = "summary"
  override def powertypeName: String = null
}
