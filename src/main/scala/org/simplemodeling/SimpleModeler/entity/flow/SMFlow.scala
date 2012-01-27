package org.simplemodeling.SimpleModeler.entity.flow

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.flow._
import org.simplemodeling.SimpleModeler.entity._

/*
 * package flow is a temporaly to evaluate flow model.
 * 
 * @since   Mar. 20, 2011
 * @version Mar. 21, 2011
 * @author  ASAMI, Tomoharu
 */
class SMFlow(val dslFlow: Flow) extends SMObject(dslFlow) {
  override def typeName: String = "flow"

  val flowMachine = new SMFlowMachine(dslFlow, this)
  flowMachine.ports ++= ports
}
