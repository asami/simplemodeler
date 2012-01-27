package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._

/*
 * Oct. 22, 2008
 * @since   Sep. 10, 2008
 * @version Sep. 19, 2011
 * @author  ASAMI, Tomoharu
 */
class SValue(name: String, pkgname: String) extends SObject(name, pkgname) with SAttributeType {
  var datatype: SDatatype = null // XXX SNull
  var invariants: SData => Boolean = null

  def this() = this(null, null)
}
