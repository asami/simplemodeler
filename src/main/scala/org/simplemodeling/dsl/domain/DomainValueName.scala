package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl

/*
 * Oct. 18, 2008
 * @since   Sep. 11, 2008
 * @version Sep. 19, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class DomainValueName(name: String, pkgname: String) extends DomainValue(name, pkgname) {
  def this() = this(null, null)
}
