package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * Nov. 18, 2008
 * Nov. 19, 2008
 */
class SServiceSet {
  private val _services = new ArrayBuffer[SServiceRelationship]

  def services: Seq[SServiceRelationship] = _services

  // from model itself
  def apply(aService: SService): SServiceRelationship = {
    create(aService)
  }

  //
  def create(aService: SService): SServiceRelationship = {
    val service = new SServiceRelationship(aService)
    _services += service
    service
  }
}
