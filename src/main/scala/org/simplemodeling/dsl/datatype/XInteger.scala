package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XInteger() extends SDatatype {
  caption = <t>無限精度整数。</t>
  brief = <t>XMLのデータタイプinteger。</t>
  description = <text>
  java.math.BigInteger
  </text>
}

object XInteger extends XInteger {
}
