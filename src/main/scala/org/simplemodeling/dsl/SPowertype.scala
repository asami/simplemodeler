package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._

/*
 * @since   Dec. 20, 2008
 *  version Oct. 25, 2009
 *  version Sep. 19, 2011
 * @version Nov. 23, 2012
 * @author  ASAMI, Tomoharu
 */
class SPowertype(name: String, pkgname: String) extends SObject(name, pkgname) {
  val kinds = new ArrayBuffer[SPowertypeKind]

  def this() = this(null, null)

  final def kind(aKind: Symbol): SPowertypeKind = {
    return kind(new SPowertypeKind(aKind.name, None))
  }

  final def kind(aKind: String): SPowertypeKind = {
    return kind(new SPowertypeKind(aKind, None))
  }

  final def kind(aKind: SPowertypeKind): SPowertypeKind = {
    kinds += aKind
    return aKind;
  }
}

object NullPowertype extends SPowertype
