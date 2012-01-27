package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * Sep. 15, 2008
 * Jan. 18, 2009
 */
class SMValue(val dslValue: SValue) extends SMObject(dslValue) {
  override def typeName: String = "value"
}
