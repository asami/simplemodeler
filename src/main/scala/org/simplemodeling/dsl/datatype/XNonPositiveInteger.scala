package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XNonPositiveInteger() extends SDatatype {
  caption = <t>0以下の無限精度整数。</t>
  brief = <t>XMLのデータタイプnonPositiveInteger。</t>
  description = <text>
  java.math.BigDecimal
  </text>
}

object XNonPositiveInteger extends XNonPositiveInteger {
}
