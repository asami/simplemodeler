package org.simplemodeling.dsl

/*
 * Oct. 18, 2008
 * @since   Sep. 11, 2008
 * @version Sep. 19, 2011
 * @author  ASAMI, Tomoharu
 */
class SRule(name: String, pkgname: String) extends SObject(name, pkgname) {
  def this() = this(null, null)
}
