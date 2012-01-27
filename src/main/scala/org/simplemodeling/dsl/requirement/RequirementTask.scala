package org.simplemodeling.dsl.requirement

import org.simplemodeling.dsl._

/*
 * Dec. 10, 2008
 * Dec. 17, 2008
 */
class RequirementTask(aName: String, aPkgName: String) extends STask(aName, aPkgName) {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)
}

object NullRequirementTask extends RequirementTask(null, null)
