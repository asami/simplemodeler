package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * @since   Nov.  8, 2008
 * @version Nov. 12, 2010
 * @version Nov.  4, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class SStep(aName: String) extends SElement(aName) {
  type Descriptable_TYPE = SStep
  type Historiable_TYPE = SStep
  var mark: String = ""
  var primaryActor: SAgent = NullAgent
  var secondaryActor: SAgent = NullAgent
  var primaryActorKind: StepActorKind = NullStepActorKind
  var secondaryActorKind: StepActorKind = NullStepActorKind

  def this() = this(null)

  def mark_is(aMark: String): SStep = {
    require (aMark != null)
    require (mark == "")
    mark = aMark
    this
  }

}

object NullStep extends SStep

abstract class StepActorKind

object NullStepActorKind extends StepActorKind
object BusinessClientActorKind extends StepActorKind
object BusinessWorkerActorKind extends StepActorKind
object SystemClientActorKind extends StepActorKind
object SystemUnderDiscussionActorKind extends StepActorKind
/*
case class NullStepActorKind() extends StepActorKind
case class BusinessClientActorKind() extends StepActorKind
case class BusinessWorkerActorKind() extends StepActorKind
case class SystemClientActorKind() extends StepActorKind
case class SystemUnderDiscussionActorKind() extends StepActorKind

object StepActorKind extends StepActorKind {
  val NullStepActor = new NullStepActorKind
  val BusinessClient = new BusinessClientActorKind
  val BusinessWorker = new BusinessWorkerActorKind
  val SystemClient = new SystemClientActorKind
  val SystemUnderDiscussion = new SystemUnderDiscussionActorKind
}
*/