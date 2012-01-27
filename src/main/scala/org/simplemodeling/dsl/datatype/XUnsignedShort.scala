package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XUnsignedShort() extends SDatatype {
  caption = <t>0以上の16bit整数。</t>
  brief = <t>XMLのデータタイプunsignedShort。</t>
  description = <text>
  int, java.lang.Integer
  </text>
}

object XUnsignedShort extends XUnsignedShort {
}
