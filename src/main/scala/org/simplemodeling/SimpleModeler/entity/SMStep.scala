package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}

/*
 * Nov.  9, 2008
 * Dec.  8, 2008
 */
abstract class SMStep(val dslStep: SStep) extends SMElement(dslStep) {
  var sequenceNumber: Int = 0
  var enterSequenceNumber: Int = 0
  var leaveSequenceNumber: Int = 0
  var layeredSequenceNumber: LayeredSequenceNumber = NullLayeredSequenceNumber
  final def isPrimaryActor = dslStep.primaryActor != NullAgent
  final def isSecondaryActor = dslStep.primaryActor != NullAgent
  final def primaryActorQName = dslStep.primaryActor.qualifiedName
  final def secondaryActorQName = dslStep.primaryActor.qualifiedName
  final def primaryActorKind = dslStep.primaryActorKind
  final def secondaryActorKind = dslStep.secondaryActorKind
  final def mark = dslStep.mark

  override protected def new_Node(aName: String): SMStep = {
    error("SMStep = " + aName)
  }

  final def getPrimaryActorTerm: SDoc = {
    val actor = dslStep.primaryActor
    new SIAnchor(actor.term) unresolvedRef_is new SElementRef(actor.packageName, actor.name)
  }

  final def getSecondaryActorTerm: SDoc = {
    val actor = dslStep.secondaryActor
    new SIAnchor(dslStep.secondaryActor.term) unresolvedRef_is new SElementRef(actor.packageName, actor.name)
  }

}

object NullSMStep extends SMStep(NullStep)
