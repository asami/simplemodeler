package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer

/*
 * @since   Sep. 11, 2008
 *  version Jan.  6, 2009
 * @version Nov.  9, 2012
 * @author  ASAMI, Tomoharu
 */
class SService(aName: String, aPkgName: String) extends SObject(aName, aPkgName) {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)

  def mainOperation: SOperation = {
    getOperations.first
  }
}

class NullService extends SService
object NullService extends NullService
