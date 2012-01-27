package org.simplemodeling.SimpleModeler.entity.flow

import scala.collection.mutable.{LinkedHashMap, ArrayBuffer, HashSet}
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.flow._
import org.goldenport.value._
import org.goldenport.sdoc._
import org.simplemodeling.SimpleModeler.sdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Mar. 21, 2011
 * @version Mar. 26, 2011
 * @author  ASAMI, Tomoharu
 */
class SMFlowMachine(val dslFlow: Flow, val ownerObject: SMObject) extends SMElement(dslFlow) {
  val ports = new ArrayBuffer[SMPort]
  val steps = new ArrayBuffer[SMFlowStep]

  for (n <- dslFlow.mainLine) {
    val s = new SMFlowStep(n)
    steps += s
  }
}

class SMFlowStep(val dslStep: FlowNode) {
  val operator = dslStep.operator.get
  val name = operator.name
  val inputs = operator.inputs.map(new SMEntityType(_))
  val outputs = operator.outputs.map(new SMEntityType(_))
  def entities = inputs ::: outputs
}
