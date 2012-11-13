package org.simplemodeling.SimpleModeler.entities.simplemodel

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Nov. 13, 2012
 * @version Nov. 13, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMStateMachineState(val name: String, val value: Option[String]) extends SMMElement {
}

object SMMStateMachineState {
  /**
   * Used by SMMEntityEntity#powertype to create a powertype kind from table.
   */
  def create(entry: Seq[(String, String)]): SMMStateMachineState = {
    val name = NaturalLabel.getSlotName(entry) | "Unkonwn"
    val value = ValueLabel.findData(entry)
    val k = new SMMStateMachineState(name, value)
    k.update(entry)
    k
  }

  /**
   * Used by SMMEntityEntity#powertype to create a powertype kind in mindmap model.
   */
  def create(termname: (String, String)): SMMStateMachineState = {
    val (term, name) = termname
    new SMMStateMachineState(name, term.some)
  }
}
