package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * Oct. 12, 2008
 * Jan. 18, 2009
 */
class SMService(val dslService: SService) extends SMObject(dslService) {
  override def typeName: String = "service"
}
