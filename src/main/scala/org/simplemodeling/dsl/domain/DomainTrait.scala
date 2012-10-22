package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl.STrait

/*
 * @since   Oct. 15, 2012
 * @version Oct. 21, 2012
 * @author  ASAMi, Tomoharu
 */
class DomainTrait(name: String, pkgName: String) extends STrait(name, pkgName) {
  def this() = this(null, null)
  override def class_Name = "DomainTrait"
}
