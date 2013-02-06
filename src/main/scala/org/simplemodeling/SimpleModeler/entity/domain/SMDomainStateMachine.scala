package org.simplemodeling.SimpleModeler.entity.domain

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.dsl.domain._

/*
 * @since   Jan. 20, 2009
 *  version Nov. 14, 2012
 * @version Feb.  6, 2013
 * @author  ASAMI, Tomoharu
 */
class SMDomainStateMachine(val dslDomainStateMachine: DomainStateMachine) extends SMStateMachine(dslDomainStateMachine) {
//  override def kindName: String = null
//  override def statemachineName: String = null

  override def toString() = {
    "SMDomainStateMachine(" + states + ")"
  }
}
