package org.simplemodeling.dsl.business

import org.simplemodeling.dsl._

/*
 * Nov.  8, 2008
 * Dec.  8, 2008
 */
class BusinessTask(aName: String, aPkgName: String) extends STask(aName, aPkgName) with BusinessStory {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)
}

object NullBusinessTask extends BusinessTask(null, null)
