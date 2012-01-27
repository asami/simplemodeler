package org.simplemodeling.dsl.flow

import scala.annotation.tailrec
import scala.collection.mutable.HashMap
import scala.collection.mutable.LinkedHashMap
import org.simplemodeling.dsl._

/**
 * @since   Jan.  4, 2011
 * @version Mar. 26, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class Flow extends SObject {
  private var _start: Option[FlowNode] = None

  protected final def set_start[T <: FlowNode](node: T): T = {
    _start = Some(node)
    node
  }

  def mainLine: List[FlowNode] = {
    (_start match {
      case Some(n) => n.makeLine
      case None => Nil
    }).filter(_.operator.isDefined)
  }
}

abstract class Flow11[IN1 <: SEntity, OUT1 <: SEntity](implicit val min1: Manifest[IN1], implicit val mout1: Manifest[OUT1]) extends Flow {
  val in1 = new Port[IN1](this)
  val out1 = new Port[OUT1](this)

  def post1 = in1
  def get1 = out1

  port('in1, min1.erasure, SPortIn)
  port('out1, mout1.erasure, SPortOut)

  def start: FlowNode1[IN1] = {
    set_start(new FlowNode1[IN1]())
  }

  def start(port: Port[IN1]) = {
    set_start(new FlowNode1[IN1]())
  }
}

abstract class Flow32[IN1 <: SEntity, IN2 <: SEntity, IN3 <: SEntity, OUT1 <: SEntity, OUT2 <: SEntity] extends Flow {
  val in1 = new Port[IN1](this)
  val in2 = new Port[IN2](this)
  val in3 = new Port[IN3](this)
  val out1 = new Port[OUT1](this)
  val out2 = new Port[OUT2](this)

  def start: FlowNode1[IN1] = {
    set_start(new FlowNode1[IN1]())
  }

  def start2 = {
    set_start(new FlowNode2[IN1, IN2]())
  }

  def start3 = {
    set_start(new FlowNode3[IN1, IN2, IN3]())
  }
}

abstract class FlowNode(val prev: Option[FlowNode] = None) {
  var next: Option[FlowNode] = None
  var operator: Option[Operator] = None

  for (n <- prev) {
    n.next = Some(this)
  }

  def makeLine: List[FlowNode] = {
    next match {
      case None => List(this)
      case Some(n) => this :: n.makeLine
    }
  }
}

class FlowNode1[FIN1 <: SEntity](prev: Option[FlowNode] = None) extends FlowNode(prev) {
  def op1[FOUT1 <: SEntity](op: Operator11[FIN1, FOUT1]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op2[FOUT1 <: SEntity, FOUT2 <: SEntity](op: Operator12[FIN1, FOUT1, FOUT2]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op3[FOUT1 <: SEntity, FOUT2 <: SEntity, FOUT3 <: SEntity](op: Operator13[FIN1, FOUT1, FOUT2, FOUT3]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op11[FOUT1 <: SEntity](op: Operator11[FIN1, FOUT1]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op12[FOUT1 <: SEntity, FOUT2 <: SEntity](op: Operator12[FIN1, FOUT1, FOUT2]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op21[FIN2 <: SEntity, FOUT1 <: SEntity](op: Operator21[FIN1, FIN2, FOUT1]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def end {
  }

  def end[FOUT1 <: SEntity](port: Port[FOUT1]) {
  }
}

class FlowNode2[FIN1 <: SEntity, FIN2 <: SEntity](prev: Option[FlowNode] = None) extends FlowNode(prev) {
  def op1[FOUT1 <: SEntity](op: Operator21[FIN1, FIN2, FOUT1]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op2[FOUT1 <: SEntity, FOUT2 <: SEntity](op: Operator22[FIN1, FIN2, FOUT1, FOUT2]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op3[FOUT1 <: SEntity, FOUT2 <: SEntity, FOUT3 <: SEntity](op: Operator23[FIN1, FIN2, FOUT1, FOUT2, FOUT3]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def end {
  }
}

class FlowNode3[FIN1 <: SEntity, FIN2 <: SEntity, FIN3 <: SEntity](prev: Option[FlowNode] = None) extends FlowNode(prev) {
  def op1[FOUT1 <: SEntity](op: Operator31[FIN1, FIN2, FIN3, FOUT1]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op2[FOUT1 <: SEntity, FOUT2 <: SEntity](op: Operator32[FIN1, FIN2, FIN3, FOUT1, FOUT2]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def op3[FOUT1 <: SEntity, FOUT2 <: SEntity, FOUT3 <: SEntity](op: Operator33[FIN1, FIN2, FIN3, FOUT1, FOUT2, FOUT3]): FlowNode1[FOUT1] = {
    operator = Some(op);
    new FlowNode1[FOUT1](Some(this))
  }

  def end {
  }
}
