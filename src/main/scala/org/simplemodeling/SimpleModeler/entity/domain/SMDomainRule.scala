package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * Oct. 12, 2008
 * Oct. 18, 2008
 */
class SMDomainRule(val dslDomainRule: DomainRule) extends SMRule(dslDomainRule) {
  override def kindName: String = null
  override def powertypeName: String = null
}
