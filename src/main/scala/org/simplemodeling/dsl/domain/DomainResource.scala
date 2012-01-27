package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * derived from DomainResource since Nov. 22, 2007
 * 
 * Oct. 18, 2008
 *
 * @since   Sep. 11, 2008
 * @version Sep. 18, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class DomainResource(name: String, pkgname: String) extends DomainEntity(name, pkgname) {
  def this() = this(null, null)

  val resource_type = new DResourceType
  val resource_unit_type = new DResourceUnitType

  val stock = DResourceType.stock
  val Stock = stock
  val individual = DResourceUnitType.individual
  val Individual = individual
}
