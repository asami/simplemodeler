package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Mar. 21, 2011
 * @version Mar. 21, 2011
 * @author  ASAMI, Tomoharu
 */
class SPortSet(val isMaster: Boolean) {
  private val _ports = new ArrayBuffer[SPort]

  def ports: Seq[SPort] = _ports

  def apply(aName: Symbol, aEntity: Class[_], inout: SPortInputOutput): SPort = {
    val entity = aEntity.newInstance().asInstanceOf[SEntity]
    apply(aName, entity, inout)
  }

  def apply(aName: Symbol, aEntity: SEntity, inout: SPortInputOutput = SPortInOut): SPort = {
    create(aName.name, aEntity, inout)
  }

  def create(aName: String, aEntity: SEntity, inout: SPortInputOutput): SPort = {
    val port = new SPort(aName, aEntity)
    _ports += port
    port
  }
}
