package org.simplemodeling.dsl.datatype.platform

import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * @since   Jan. 29, 2013
 * @version Jan. 29, 2013
 * @author  ASAMI, Tomoharu
 */
case class XUuid() extends SDatatype {
  caption = <t>UUID。</t>
  brief = <t>UUID。</t>
  description = <text>
  </text>
}

object XUuid extends XUuid {
}
