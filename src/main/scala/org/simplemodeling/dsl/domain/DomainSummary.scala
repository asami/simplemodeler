package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl

/*
 * derived from DomainSummary since Nov. 22, 2007
 * 
 * Mar. 17, 2009
 * 
 * @since   Sep. 15, 2008
 * @version Sep. 18, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class DomainSummary(name: String, pkgname: String) extends DomainEntity(name, pkgname) {
  def this() = this(null, null)
}
