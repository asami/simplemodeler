package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl

/*
 * Oct. 18, 2008
 * @since   Sep. 11, 2008
 *  version Oct. 26, 2011
 * @version Oct. 21, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DomainValueId(name: String, pkgname: String) extends DomainValue(name, pkgname) {
  def this() = this(null, null)
  override def class_Name = "DomainValueId"
}
