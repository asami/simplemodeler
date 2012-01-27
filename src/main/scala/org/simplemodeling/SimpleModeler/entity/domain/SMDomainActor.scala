package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.dsl.domain._

/*
 * Sep. 15, 2008
 * Jan. 18, 2008
 */
class SMDomainActor(val dslActor: DomainActor) extends SMDomainEntity(dslActor) {
  override def kindName: String = "actor"
  override def powertypeName: String = null
}
