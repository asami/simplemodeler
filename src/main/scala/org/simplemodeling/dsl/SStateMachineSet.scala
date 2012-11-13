package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Dec. 20, 2008
 *  version Nov. 13, 2010
 * @version Nov. 14, 2012
 * @author  ASAMI, Tomoharu
 */
class SStateMachineSet(val obj: SObject) {
  private val _stateMachines = new ArrayBuffer[SStateMachineRelationship]

  def stateMachines: Seq[SStateMachineRelationship] = _stateMachines

  // from model itself
  def apply(aStateMachine: => SStateMachine): SStateMachineRelationship = {
//    if (!obj.isMaster) {
//      return new SStateMachineRelationship // Stub
//    }
    create(aStateMachine)
  }

  def apply(aName: String, aStateMachine: => SStateMachine): SStateMachineRelationship = {
//    if (!obj.isMaster) {
//      return new SStateMachineRelationship // Stub
//    }
    create(aName, aStateMachine)
  }

  //
  def create(aStateMachine: => SStateMachine): SStateMachineRelationship = {
//    if (!obj.isMaster) {
//      return new SStateMachineRelationship // Stub
//    }
    val smr = new SStateMachineRelationship(aStateMachine, aStateMachine.name)
    _stateMachines += smr
    smr
  }

  def create(aName: String, aStateMachine: => SStateMachine): SStateMachineRelationship = {
//    if (!obj.isMaster) {
//      return new SStateMachineRelationship // Stub
//    }
    val smr = new SStateMachineRelationship(aStateMachine, aName)
    _stateMachines += smr
    smr
  }
}
