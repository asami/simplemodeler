package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl

/*
 * @since   Nov. 25, 2012
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DomainAssociationEntity(name: String, pkgname: String) extends DomainEntity(name, pkgname) {
  def this() = this(null, null)
}
