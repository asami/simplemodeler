package org.simplemodeling.dsl.business

import scalaz._, Scalaz._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
// import org.simplemodeling.dsl.StepActorKind._

/*
 * @since   Dec.  8, 2008
 *  version Nov.  4, 2011
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
trait BusinessStory extends SStoryObject {
  def invoke_client_worker(aService: DomainService)(theSteps: => Unit): SInvocationStep = {
    val step = new SInvocationStep
    step.primaryActorKind = BusinessClientActorKind
    step.secondaryActorKind = BusinessWorkerActorKind
    step.service = aService
    step.operation = aService.mainOperation
    step.requestDocument = step.operation.in
    step.responseDocument = step.operation.out
    execute_step(step, theSteps)
    step
  }

  def invoke_client_worker(aOperation: SOperation)(theSteps: => Unit): SInvocationStep = {
    val step = new SInvocationStep
    step.primaryActorKind = BusinessClientActorKind
    step.secondaryActorKind = BusinessWorkerActorKind
    step.service = aOperation.ownerService
    step.operation = aOperation
    step.requestDocument = step.operation.in
    step.responseDocument = step.operation.out
    execute_step(step, theSteps)
    step
  }

  def invoke_client_worker(anOperationName: String): SInvocationStep = {
    val step = new SInvocationStep
    step.primaryActorKind = BusinessClientActorKind
    step.secondaryActorKind = BusinessWorkerActorKind
    step.informalOperationName = anOperationName
    execute_step(step)
    step
  }

  def invoke_client_worker(anOperationName: String, aRequestDocument: DomainDocument, aResponseDocument: DomainDocument)(theSteps: => Unit): SInvocationStep = {
    val step = new SInvocationStep
    step.primaryActorKind = BusinessClientActorKind
    step.secondaryActorKind = BusinessWorkerActorKind
    step.requestDocument = aRequestDocument.some
    step.responseDocument = aResponseDocument.some
    step.informalOperationName = anOperationName
    execute_step(step, theSteps)
    step
  }

  def invoke_worker_system(aService: DomainService)(theSteps: => Unit): SInvocationStep = {
    val step = new SInvocationStep
    step.primaryActorKind = BusinessWorkerActorKind
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
    step.primaryActorKind = BusinessWorkerActorKind
    step.secondaryActorKind = SystemUnderDiscussionActorKind
    step.service = aOperation.ownerService
    step.operation = aOperation
    step.requestDocument = step.operation.in
    step.responseDocument = step.operation.out
    execute_step(step, theSteps)
    step
  }

  def invoke_worker_system(anOperation: String, aRequestDocument: DomainDocument, aResponseDocument: DomainDocument)(theSteps: => Unit): SInvocationStep = {
    val step = new SInvocationStep
    step.primaryActorKind = BusinessWorkerActorKind
    step.secondaryActorKind = SystemUnderDiscussionActorKind
    step.requestDocument = aRequestDocument.some
    step.responseDocument = aResponseDocument.some
    step.informalOperationName = anOperation
    execute_step(step, theSteps)
    step
  }

  def invoke_client_worker_system(aService: DomainService)(theSteps: => Unit): SInvocationStep = {
    invoke_client_worker(aService) {
      invoke_worker_system(aService) {
	theSteps
      }
    }
  }

  def invoke_client_worker_system(aOperation: SOperation)(theSteps: => Unit): SInvocationStep = {
    invoke_client_worker(aOperation) {
      invoke_worker_system(aOperation) {
	theSteps
      }
    }
  }

  def invoke_client_worker_system(anAction: String, aRequestDocument: DomainDocument, aResponseDocument: DomainDocument)(theSteps: => Unit): SInvocationStep = {
    invoke_client_worker(anAction, aResponseDocument, aResponseDocument) {
      invoke_worker_system(anAction, aRequestDocument, aResponseDocument) {
	theSteps
      }
    }
  }
}
