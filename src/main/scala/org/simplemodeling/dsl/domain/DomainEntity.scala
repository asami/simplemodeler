package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * derived from DomainEntity since Nov. 22, 2007
 * Nov. 12, 2010
 * @since   Sep. 10, 2008
 *  version Sep. 18, 2011
 * @version Oct. 21, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DomainEntity(name: String, pkgName: String) extends SEntity(name, pkgName) {
  def this() = this(null, null)
  override def class_Name = "DomainEntity"
}
