package org.simplemodeling.dsl

import scala.collection.mutable.HashMap

/*
 * Mar.  1, 2009
 * Mar.  1, 2009
 */
class SStateRepository {
  private val _states = new HashMap[String, SState]

  final def register(anState: SState): Boolean = {
    val qName = anState.qualifiedName
    if (_states.contains(qName)) {
      false
    } else {
      _states.put(qName, anState)
      true
    }
  }

  final def getState(qName: String): SState = {
    _states.get(qName) match {
      case Some(obj: SState) => obj
      case None => error("no state = " + qName)
    }
  }
}

object SStateRepository extends SStateRepository
