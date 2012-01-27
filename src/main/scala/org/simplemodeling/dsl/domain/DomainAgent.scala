package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * Mar. 17, 2009
 * @since   Sep. 11, 2008
 * @version Sep. 11, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class DomainAgent(name: String, pkgname: String) extends DomainEntity(name, pkgname) with SAgent {
  def this() = this(null, null)
}
