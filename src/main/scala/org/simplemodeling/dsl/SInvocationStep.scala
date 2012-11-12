package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * @since   Dec.  5, 2008
 *  version Dec. 12, 2008
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class SInvocationStep extends SStep(null) {
  var service: SService = NullService
  var operation: SOperation = NullOperation
  // TODO for scala dsl usage, Option should not be used.
  var requestDocument: Option[SAttributeType] = None
  var responseDocument: Option[SAttributeType] = None
  var informalOperationName = ""

  def requestDocumentOption = requestDocument
  def responseDocumentOption = responseDocument

/*
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
*/
}
