package org.simplemodeling.dsl.requirement

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._

/*
 * @since   Dec. 17, 2008
 * @version Sep. 22, 2009
 * @version Nov.  4, 2011
 * @author  ASAMI, Tomoharu
 */
trait RequirementStory extends SStoryObject {
  def invoke_worker_system(aService: DomainService)(theSteps: => Unit): SInvocationStep = {
    val step = new SInvocationStep
    step.primaryActorKind = SystemClientActorKind
    step.secondaryActorKind = SystemUnderDiscussionActorKind
    step.service = aService
    step.operation = aService.mainOperation
    step.requestDocument = step.operation.in
    step.responseDocument = step.operation.out
    execute_step(step, theSteps)
    step
  }

  def invoke_worker_system(aOperation: SOperation)(theSteps: => Unit): SInvocationStep = {
    val step = new SInvocationStep
    step.primaryActorKind = SystemClientActorKind
    step.secondaryActorKind = SystemUnderDiscussionActorKind
    step.service = aOperation.ownerService
    step.operation = aOperation
    step.requestDocument = step.operation.in
    step.responseDocument = step.operation.out
    execute_step(step, theSteps)
    step
  }
}
