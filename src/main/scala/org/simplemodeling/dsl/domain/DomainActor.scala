package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl

/*
 * derived from DomainActor since Nov. 22, 2007
 * Mar. 17, 2009
 * 
 * @since   Sep. 11, 2008
 * @version Sep. 18, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class DomainActor(name: String, pkgname: String) extends DomainAgent(name, pkgname) {
  def this() = this(null, null)
}
