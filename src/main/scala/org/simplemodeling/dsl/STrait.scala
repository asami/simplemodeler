package org.simplemodeling.dsl

/*
 * @since   Oct. 15, 2012
 * @version Oct. 16, 2012
 * @author  ASAMI, Tomoharu
 */
class STrait(name: String, pkgname: String) extends SEntity(name, pkgname) {
  def this() = this(null, null)
}

object NullTrait extends STrait
