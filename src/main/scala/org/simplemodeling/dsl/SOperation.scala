package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * @since   Oct. 24, 2008
 * @version Sep. 21, 2009
 * @author  ASAMI, Tomoharu
 */
class SOperation(aName: String, val in: SDocument, val out: SDocument) extends SElement(aName) {
  type Descriptable_TYPE = SOperation
  type Historiable_TYPE = SOperation
  var ownerService: SService = NullService

  def this(aName: String) = this(aName, NullDocument, NullDocument)

  def context_is(aContent: => Unit) {
    aContent
  }
}

class NullOperation extends SOperation(null, NullDocument, NullDocument)
object NullOperation extends NullOperation
