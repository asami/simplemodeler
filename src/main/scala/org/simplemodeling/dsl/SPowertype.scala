package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._

/*
 * Oct. 25, 2009
 * 
 * @since   Dec. 20, 2008
 * @version Sep. 19, 2011 
 * @author  ASAMI, Tomoharu
 */
class SPowertype(name: String, pkgname: String) extends SObject(name, pkgname) {
  val kinds = new ArrayBuffer[SPowertypeKind]
  var editable = false

  def this() = this(null, null)

  final def kind(aKind: Symbol): SPowertypeKind = {
    return kind(new SPowertypeKind(aKind))
  }

  final def kind(aKind: String): SPowertypeKind = {
    return kind(new SPowertypeKind(aKind))
  }

  final def kind(aKind: SPowertypeKind): SPowertypeKind = {
    kinds += aKind
    return aKind;
  }
}

object NullPowertype extends SPowertype
