package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XLong() extends SDatatype {
  caption = <t>64bit整数。</t>
  brief = <t>XMLのデータタイプlong。</t>
  description = <text>
  long, java.lang.Long
  </text>
}

object XLong extends XLong {
}
