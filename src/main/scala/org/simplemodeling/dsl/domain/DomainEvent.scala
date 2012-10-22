package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._

/*
 * derived from DomainEvent since Nov. 22, 2007
 * 
 * Dec. 20, 2008
 * 
 * @since  Sep. 11, 2008
 *  verion Sep. 18, 2011 
 * @verion Sep. 18, 2011 
 * @author ASAMI, Tomoharu
 */
abstract class DomainEvent(name: String, pkgname: String) extends DomainEntity(name, pkgname) with SEvent {
  def this() = this(null, null)

  val primary_actor = association.candidate("primaryActor")
  val secondary_actor = association.candidate("secondaryActor")
  val resource = association.candidate("resource")
  override def class_Name = "DomainEvent"
}
