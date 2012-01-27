package org.simplemodeling.SimpleModeler.entity.domain

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * Sep. 15, 2008
 * Jan. 18, 2009
 */
class SMDomainEvent(val dslDomainEvent: DomainEvent) extends SMDomainEntity(dslDomainEvent) {
  override def kindName: String = "event"
//  override def powertypeName: String = "atomic"

  val resourceTransitions = new ArrayBuffer[SMTransition]
}
