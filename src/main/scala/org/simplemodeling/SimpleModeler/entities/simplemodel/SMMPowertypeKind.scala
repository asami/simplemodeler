package org.simplemodeling.SimpleModeler.entities.simplemodel

import scalaz._, Scalaz._
import org.simplemodeling.dsl.util.PropertyRecord
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Nov. 13, 2012
 * @version Nov. 26, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMPowertypeKind(val name: String, val value: Option[String]) extends SMMElement {
}

object SMMPowertypeKind {
  /**
   * Used by SMMEntityEntity#powertype to create a powertype kind from table.
   */
  def create(entry: Seq[PropertyRecord]): SMMPowertypeKind = {
    val name = NaturalLabel.getSlotName(entry) | "Unkonwn"
    val value = ValueLabel.findData(entry)
    val k = new SMMPowertypeKind(name, value)
    k.update(entry)
    k
  }

  /**
   * Used by SMMEntityEntity#powertype to create a powertype kind in mindmap model.
   */
  def create(name: String): SMMPowertypeKind = {
    new SMMPowertypeKind(name, None)
  }
}
