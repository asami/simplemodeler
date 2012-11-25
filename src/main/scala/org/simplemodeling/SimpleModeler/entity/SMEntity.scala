package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   Sep. 15, 2008
 *  version Jun.  5, 2009
 * @version Nov. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SMEntity(val dslEntity: SEntity) extends SMObject(dslEntity) {
  override def typeName: String = "entity"
  // currently unused.
  override def isEntity = true

  def sql = dslEntity.sql
  def jdo = dslEntity.jdo
  def appEngine = dslEntity.appEngine
}

object SMNullEntity extends SMEntity(NullEntity)
