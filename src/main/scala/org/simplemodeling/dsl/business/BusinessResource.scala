package org.simplemodeling.dsl.business

import org.simplemodeling.dsl._

/*
 * @since   Nov.  4, 2012
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
class BusinessResource(aName: String, aPkgName: String) extends SEntity(aName, aPkgName) with BusinessEntity {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)
}

object NullBusinessResource extends BusinessResource(null, null)
