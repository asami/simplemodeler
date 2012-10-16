package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   Oct. 16, 2012
 * @version Oct. 16, 2012
 * @author  ASAMI, Tomoharu
 */
class SMTrait(val dslTrait: STrait) extends SMObject(dslTrait) {
  override def typeName: String = "trait"

//  def sql = dslTrait.sql
//  def jdo = dslTrait.jdo
//  def appEngine = dslTrait.appEngine
}

object SMNullTrait extends SMTrait(NullTrait)
