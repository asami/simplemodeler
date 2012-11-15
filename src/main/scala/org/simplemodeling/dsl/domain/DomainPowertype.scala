package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * @since   Dec. 20, 2008
 *  version Nov. 12, 2010
 *  version Sep. 19, 2011
 * @version Nov. 14, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DomainPowertype(name: String, pkgname: String) extends SPowertype(name, pkgname) {
  def this() = this(null, null)

  override def class_Name = "DomainPowertype"
}
