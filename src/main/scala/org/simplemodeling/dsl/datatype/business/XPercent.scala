package org.simplemodeling.dsl.datatype.business

import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * @since   Oct.  7, 2012
 * @version Oct.  7, 2012
 * @author  ASAMI, Tomoharu
 */
case class XPercent() extends SDatatype {
  caption = <t>パーセント。</t>
  brief = <t>パーセント。</t>
  description = <text>
  </text>
}

object XPercent extends XPercent {
}
