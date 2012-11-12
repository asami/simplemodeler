package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * @since   Oct. 24, 2008
 *  version Sep. 21, 2009
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class SOperation(aName: String, val in: Option[SAttributeType], val out: Option[SAttributeType]) extends SElement(aName) {
  type Descriptable_TYPE = SOperation
  type Historiable_TYPE = SOperation
  var ownerService: SService = NullService

  def this(aName: String) = this(aName, None, None)

  def context_is(aContent: => Unit) {
    aContent
  }
}

class NullOperation extends SOperation(null, None, None)
object NullOperation extends NullOperation
