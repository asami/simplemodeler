package org.simplemodeling.dsl

import scala.collection.mutable.LinkedHashMap

/*
 * @since   Dec. 20, 2008
 * @version Nov. 13, 2010
 * @author  ASAMI, Tomoharu
 */
class SStateMachineSet(val obj: SObject) {
  private val _stateMachines = new LinkedHashMap[String, SStateMachine]

  def stateMachines: Seq[(String, SStateMachine)] = _stateMachines.toList

  // from model itself
  def apply(aStateMachine: => SStateMachine): SStateMachine = {
    if (!obj.isMaster) {
      return new SStateMachine // Stub
    }
    create(aStateMachine)
  }

  def apply(aName: String, aStateMachine: => SStateMachine): SStateMachine = {
    if (!obj.isMaster) {
      return new SStateMachine // Stub
    }
    create(aName, aStateMachine)
  }

  //
  def create(aStateMachine: => SStateMachine): SStateMachine = {
    if (!obj.isMaster) {
      return new SStateMachine // Stub
    }
    val sm = aStateMachine
    _stateMachines += (sm.name -> aStateMachine)
    aStateMachine
  }

  def create(aName: String, aStateMachine: => SStateMachine): SStateMachine = {
    if (!obj.isMaster) {
      return new SStateMachine // Stub
    }
    _stateMachines += (aName -> aStateMachine)
    aStateMachine
  }
}
