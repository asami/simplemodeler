package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * Nov. 17, 2008
 * Dec.  5, 2008
 */
class SOperationSet {
  private val _operations = new ArrayBuffer[SOperation]
  private val _candidates = new ArrayBuffer[SOperationCandidate]

  def operations: Seq[SOperation] = _operations

  // from model itself
  def apply(aName: String, aRequest: SDocument, aResponse: SDocument): SOperation = {
    create(aName, aRequest, aResponse)
  }

  // from model itself or external user
  def apply(aName: String): SOperation = {
    val mayOper = _operations.find(_.name == aName)
    if (mayOper.isDefined) { // external use
      mayOper.get
    } else { // model itself
      val oper = new SOperation(aName)
      _operations += oper
      oper
    }
  }    

  //
  def create(aName: String, aRequest: SDocument, aResponse: SDocument): SOperation = {
    val oper = new SOperation(aName, aRequest, aResponse)
    _operations += oper
    oper
  }

  // from SOperationCandidate
  final def create(aCandidate: SOperationCandidate, aName: String, aRequest: SDocument, aResponse: SDocument): SOperation = {
    aCandidate.done = true
    create(aName, aRequest, aResponse)
  }
}
