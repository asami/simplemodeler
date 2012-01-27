package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * Oct. 12, 2008
 * Jan. 18, 2009
 */
class SMRule(val dslRule: SRule) extends SMObject(dslRule) {
  override def typeName: String = "rule"
}
