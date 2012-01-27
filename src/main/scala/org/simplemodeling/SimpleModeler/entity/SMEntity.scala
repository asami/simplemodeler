package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   Sep. 15, 2008
 * @version Jun.  5, 2009
 * @author  ASAMI, Tomoharu
 */
class SMEntity(val dslEntity: SEntity) extends SMObject(dslEntity) {
  override def typeName: String = "entity"

  def sql = dslEntity.sql
  def jdo = dslEntity.jdo
  def appEngine = dslEntity.appEngine
}

object SMNullEntity extends SMEntity(NullEntity)
