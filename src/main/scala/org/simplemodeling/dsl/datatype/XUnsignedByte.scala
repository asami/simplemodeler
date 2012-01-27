package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XUnsignedByte() extends SDatatype {
  caption = <t>0以上の8bit整数。</t>
  brief = <t>XMLのデータタイプunsignedByte。</t>
  description = <text>
  short, java.lang.Short
  </text>
}

object XUnsignedByte extends XUnsignedByte {
}
