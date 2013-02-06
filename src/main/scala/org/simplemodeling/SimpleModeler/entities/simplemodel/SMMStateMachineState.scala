package org.simplemodeling.SimpleModeler.entities.simplemodel

import scalaz._, Scalaz._
import org.simplemodeling.dsl.util.PropertyRecord
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Nov. 13, 2012
 *  version Dec.  6, 2012
 * @version Feb.  6, 2013
 * @author  ASAMI, Tomoharu
 */
class SMMStateMachineState(
  val name: String,
  val value: Option[String],
  val lifecycle: Option[String]
) extends SMMElement {
}

object SMMStateMachineState {
  /**
   * Used by SMMEntityEntity#powertype to create a powertype kind from table.
   */
  def create(entry: Seq[PropertyRecord]): SMMStateMachineState = {
    val name = NaturalLabel.getSlotName(entry) | "Unkonwn"
    val value = ValueLabel.findData(entry)
    val lifecycle = LifecycleLabel.findData(entry)
//    println("SMMStateMachineState#create(%s) = %s".format(name, value))
    val k = new SMMStateMachineState(name, value, lifecycle)
    k.update(entry)
    k
  }

  /**
   * Used by SMMEntityEntity#powertype to create a powertype kind in mindmap model.
   */
  def create(termname: (String, String)): SMMStateMachineState = {
    val (term, name) = termname
    new SMMStateMachineState(name, term.some, None)
  }
}
