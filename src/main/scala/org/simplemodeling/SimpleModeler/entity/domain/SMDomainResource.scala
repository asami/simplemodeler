package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * Sep. 15, 2008
 * Jan. 18, 2009
 */
class SMDomainResource(val dslDomainResource: DomainResource) extends SMDomainEntity(dslDomainResource) {
  override def kindName: String = "resource"

  override def powertypeName: String = {
    if (dslDomainResource.resource_type.value == null) null
    else dslDomainResource.resource_type.value.label
  }
}
