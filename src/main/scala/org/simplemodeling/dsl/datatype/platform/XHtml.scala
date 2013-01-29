package org.simplemodeling.dsl.datatype.platform

import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * @since   Jan. 29, 2013
 * @version Jan. 29, 2013
 * @author  ASAMI, Tomoharu
 */
case class XHtml() extends SDatatype {
  caption = <t>HTML。</t>
  brief = <t>HTML。</t>
  description = <text>
  </text>
}

object XHtml extends XHtml {
}
