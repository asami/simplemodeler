package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}

/*
 * @since   Dec.  5, 2008
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class SMInvocationStep(val dslInvocationStep: SInvocationStep) extends SMStep(dslInvocationStep) {
  final def isOperation = dslInvocationStep.operation != NullOperation
  final def isService = dslInvocationStep.service != NullService
  final def operationTerm = dslInvocationStep.operation.term
  final def serviceTerm = dslInvocationStep.service.term
  final def informalOperationName = dslInvocationStep.informalOperationName

  override protected def copy_Node(): SMInvocationStep = {
    new SMInvocationStep(dslInvocationStep)
  }

  final def isBidirectional = {
    dslInvocationStep.responseDocument != NullDocument
  }

  final def isRequestDocument = {
    dslInvocationStep.requestDocument != NullDocument
  }

  final def isResponseDocument = {
    dslInvocationStep.responseDocument != NullDocument
  }

  final def getRequestDocumentTerm: SDoc = {
    def to_term(anOperation: SOperation) = {
      val doc = anOperation.in.get // XXX 
      new SIAnchor(doc.term) unresolvedRef_is new SElementRef(doc.packageName, doc.name)
    }

    if (isOperation) {
      to_term(dslInvocationStep.operation)
    } else if (isService) {
      to_term(dslInvocationStep.service.mainOperation)
    } else {
      "Unkonw"
    }
  }

  final def getResponseDocumentTerm: SDoc = {
    def to_term(anOperation: SOperation) = {
      val doc = anOperation.out.get // XXX
      new SIAnchor(doc.term) unresolvedRef_is new SElementRef(doc.packageName, doc.name)
    }

    if (isOperation) {
      to_term(dslInvocationStep.operation)
    } else if (isService) {
      to_term(dslInvocationStep.service.mainOperation)
    } else {
      "Unkonw"
    }
  }

  final def getVerbTerm: SDoc = {
    if (isOperation) {
      val oper = dslInvocationStep.operation
      new SIAnchor(operationTerm) unresolvedRef_is new SElementRef(oper.name, oper.name) // XXX
    } else if (isService) {
      val service = dslInvocationStep.service
      new SIAnchor(serviceTerm) unresolvedRef_is new SElementRef(service.name, service.name) // XXX
    } else if (informalOperationName != "") {
      informalOperationName
    } else {
      "Unkonw"
    }
  }
}
