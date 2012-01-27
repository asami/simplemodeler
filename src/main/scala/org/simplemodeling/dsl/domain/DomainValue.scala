package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * derived from DomainValue since Nov. 22, 2007
 *Nov. 13, 2010
 * @since   Sep. 10, 2008
 * @version Sep. 19, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class DomainValue(name: String, pkgname: String) extends SValue(name, pkgname) {
  def this() = this(null, null)
}
