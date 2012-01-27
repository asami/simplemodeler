package org.simplemodeling.dsl.business

import org.simplemodeling.dsl._

/*
 * Dec. 22, 2008
 * Dec. 22, 2008
 */
class ObjectScopeBusinessTask(aName: String, aPkgName: String) extends BusinessTask(aName, aPkgName) {
  override def isObjectScope: Boolean = true
}
