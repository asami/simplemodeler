package org.simplemodeling.dsl.business

import org.simplemodeling.dsl._

/*
 * @since   Nov.  4, 2012
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
class BusinessEvent(aName: String, aPkgName: String) extends SEntity(aName, aPkgName) with BusinessEntity with SEvent {
  def this() = this(null, null)
  def this(aName: String) = this(aName, null)
}

object NullBusinessEvent extends BusinessEvent(null, null)
