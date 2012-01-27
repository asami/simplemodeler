package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * Feb. 27, 2009
 * Feb. 27, 2009
 */
trait SMRole extends SMObject {
  override def kindName: String = "role"
}

object SMNullRole extends SMObject(NullRole) with SMRole
