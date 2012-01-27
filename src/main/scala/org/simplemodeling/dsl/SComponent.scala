package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Jan.  5, 2009
 * Jan.  5, 2009
 */
abstract class SComponent(aName: String, aPkgName: String) extends SObject(aName, aPkgName) {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)
}

object NullComponent extends SComponent
