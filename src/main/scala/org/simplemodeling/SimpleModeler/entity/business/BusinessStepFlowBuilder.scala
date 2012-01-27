package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.util.StepFlowBuilder
import org.goldenport.value._

/*
 * Dec. 18, 2008
 * Dec. 18, 2010
 */
class BusinessStepFlowBuilder(
  root: GTreeNode[SMStep],
  dslStoryObject: SStoryObject,
  registerMark: SMStep => Unit,
  addUse: SMUse => Unit) extends StepFlowBuilder(root, dslStoryObject, registerMark, addUse) {

  override protected def make_Null_TaskStep: (STask, SMTaskStep) = {
    val task = NullBusinessTask
    val step = new SMBusinessTaskStep(NullBusinessTaskStep, task)
    (task, step)
  }
}
