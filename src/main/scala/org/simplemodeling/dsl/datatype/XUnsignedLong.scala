package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XUnsignedLong() extends SDatatype {
  caption = <t>0以上の64bit整数。</t>
  brief = <t>XMLのデータタイプunsignedLong。</t>
  description = <text>
  java.math.BigInteger
  </text>
}

object XUnsignedLong extends XUnsignedLong {
}
