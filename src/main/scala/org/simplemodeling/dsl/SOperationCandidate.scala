package org.simplemodeling.dsl

/*
 * Nov. 17, 2008
 * Nov. 17, 2008
 */
class SOperationCandidate(val name: String, val operations: SOperationSet) {
  var done = false

  def apply(aName: String, aRequest: SDocument, aResponse: SDocument): SOperation = {
    operations.create(this, aName, aRequest, aResponse)
  }
}
