package org.simplemodeling.dsl

/*
 * @since   Oct. 15, 2012
 * @version Oct. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class STrait(name: String, pkgname: String) extends SObject(name, pkgname) {
  def this() = this(null, null)
}
