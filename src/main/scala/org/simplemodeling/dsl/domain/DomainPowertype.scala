package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * Nov. 12, 2010
 * 
 * @since   Dec. 20, 2008
 * @version Sep. 19, 2011 
 * @author  ASAMI, Tomoharu
 */
abstract class DomainPowertype(name: String, pkgname: String) extends SPowertype(name, pkgname) {
  def this() = this(null, null)
}
