package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XShort() extends SDatatype {
  caption = <t>16bit整数。</t>
  brief = <t>XMLのデータタイプshort。</t>
  description = <text>
  short, java.lang.Short
  </text>
}

object XShort extends XShort {
}
