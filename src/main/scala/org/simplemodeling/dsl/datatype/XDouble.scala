package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XDouble() extends SDatatype {
  caption = <t>32bit浮動小数点数。</t>
  brief = <t>XMLのデータタイプdouble。</t>
  description = <text>
  double, java.lang.Double
  </text>
}

object XDouble extends XDouble {
}
