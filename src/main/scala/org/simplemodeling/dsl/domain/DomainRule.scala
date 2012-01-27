package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * derived from DomainRule since Nov. 22, 2007
 * Nov. 13, 2010
 * @since   Sep. 11, 2008
 * @version Sep. 19, 2011 
 * @author  ASAMI, Tomoharu
 */
abstract class DomainRule(name: String, pkgname: String) extends SRule(name, pkgname) {
  def this() = this(null, null)
}
