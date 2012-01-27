package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._

/*
 * @since   Aug.  7, 2009
 * @version Aug.  7, 2009
 * @author  ASAMI, Tomoharu
 */
class SMComponent(val dslComponent: SComponent) extends SMObject(dslComponent) {
  override def typeName: String = "component"
}

object SMNullComponent extends SMComponent(NullComponent)
