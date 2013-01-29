package org.simplemodeling.dsl.datatype.platform

import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * @since   Jan. 29, 2013
 * @version Jan. 29, 2013
 * @author  ASAMI, Tomoharu
 */
case class XEverforthid() extends SDatatype {
  caption = <t>Everforth ID。</t>
  brief = <t>Everforth ID。</t>
  description = <text>
  </text>
}

object XEverforthid extends XUuid {
}
