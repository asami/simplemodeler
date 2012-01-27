package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * Sep. 11, 2008
 * Jan.  6, 2009
 */
class SService extends SObject {
//  type Descriptable_TYPE = SService
//  type Historiable_TYPE = SService
  // XXX SPort

  def mainOperation: SOperation = {
    getOperations.first
  }
}

class NullService extends SService
object NullService extends NullService
