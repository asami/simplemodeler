package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Dec.  5, 2008
 * Dec. 12, 2008
 */
class SInvocationStep extends SStep(null) {
  var service: SService = NullService
  var operation: SOperation = NullOperation
  var requestDocument: SDocument = NullDocument
  var responseDocument: SDocument = NullDocument
  var informalOperationName = ""

  def requestDocumentOption: Option[SDocument] = {
    require (requestDocument != null)
    if (requestDocument == NullDocument) None
    else Some(requestDocument)
  }

  def responseDocumentOption = {
    require (responseDocument != null)
    if (responseDocument == NullDocument) None
    else Some(responseDocument)
  }
}
