package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XDecimal() extends SDatatype {
  caption = <t>無限精度数値。</t>
  brief = <t>XMLのデータタイプdecimal。</t>
  description = <text>
  java.math.BigDecimal
  </text>
}

object XDecimal extends XDecimal {
}
