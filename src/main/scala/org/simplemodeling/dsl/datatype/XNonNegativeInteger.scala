package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XNonNegativeInteger() extends SDatatype {
  caption = <t>0以上の無限精度整数。</t>
  brief = <t>XMLのデータタイプnonNegativeInteger。</t>
  description = <text>
  java.math.BigInteger
  </text>
}

object XNonNegativeInteger extends XNonNegativeInteger {
}
