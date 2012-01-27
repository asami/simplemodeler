package org.simplemodeling.dsl.system

import org.simplemodeling.dsl._

/*
 * Jan.  5, 2009
 * Jan.  5, 2009
 */
class SystemComponent(aName: String, aPkgName: String) extends SComponent(aName, aPkgName) {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)
}

object NullSystemComponent extends SystemComponent(null, null)
