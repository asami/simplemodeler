package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * derived from DomainRole since Nov. 25, 2007
 * 
 * Nov. 12, 2010
 * 
 * @since   Sep. 11, 2008
 *  version Sep. 18, 2011 
 * @version Oct. 21, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class DomainRole(name: String, pkgname: String) extends DomainAgent(name, pkgname) with SRole {
  def this() = this(null, null)
  override def class_Name = "DomainRole"
}
