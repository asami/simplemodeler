package org.simplemodeling.SimpleModeler.entity.requirement

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.requirement._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.value._
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.util.StepFlowBuilder

/*
 * @since   Dec. 10, 2008
 *  version Dec. 18, 2010
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
class SMRequirementTask(val dslRequirementTask: RequirementTask) extends SMTask(dslRequirementTask) {
  override def typeName: String = "task"

  val userRequirementUsecases = new ArrayBuffer[SMRequirementUsecase]

  override final def new_StepFlowBuilder(
    root: GTreeNode[SMStep],
    dslStoryObject: SStoryObject,
    registerMark: SMStep => Unit,
    addUse: SMUse => Unit): StepFlowBuilder = {
    new RequirementStepFlowBuilder(root, dslStoryObject, registerMark, addUse)
  }

/* 2008-12-09
  def build(aStep: SMTaskStep) {
    build_basic_flow(aStep)
  }
*/
  final def userRequirementUsecasesLiteral: SDoc = {
    objects_literal(userRequirementUsecases)
  }
}

object NullSMRequirementTask extends SMRequirementTask(NullRequirementTask)
