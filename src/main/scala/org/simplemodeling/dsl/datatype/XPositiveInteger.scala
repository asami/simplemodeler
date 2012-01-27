package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XPositiveInteger() extends SDatatype {
  caption = <t>1以上の無限精度整数。</t>
  brief = <t>XMLのデータタイプpositiveInteger。</t>
  description = <text>
  java.math.BigDecimal
  </text>
}

object XPositiveInteger extends XPositiveInteger {
}
