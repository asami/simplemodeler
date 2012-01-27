package org.simplemodeling.SimpleModeler.entity.business

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.value._
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entity.util.StepFlowBuilder

/*
 * Dec.  7, 2008
 * Dec. 18, 2010
 */
class SMBusinessTask(val dslBusinessTask: BusinessTask) extends SMTask(dslBusinessTask) {
  override def typeName: String = "businessTask"

  val userBusinessUsecases = new ArrayBuffer[SMBusinessUsecase]
  val realizationRequirementUsecases = new ArrayBuffer[SMRequirementUsecase]

/* 2008-12-09
  def build(aStep: SMTaskStep) {
    build_basic_flow(aStep)
  }
*/

  override final def new_StepFlowBuilder(
    root: GTreeNode[SMStep],
    dslStoryObject: SStoryObject,
    registerMark: SMStep => Unit,
    addUse: SMUse => Unit): StepFlowBuilder = {
    new BusinessStepFlowBuilder(root, dslStoryObject, registerMark, addUse)
  }

  final def userBusinessUsecasesLiteral: SDoc = {
    objects_literal(userBusinessUsecases)
  }
}

object NullSMBusinessTask extends SMBusinessTask(NullBusinessTask)
