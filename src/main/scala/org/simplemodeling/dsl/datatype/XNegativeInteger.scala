package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XNegativeInteger() extends SDatatype {
  caption = <t>0未満の無限精度整数。</t>
  brief = <t>XMLのデータタイプnegativeInteger。</t>
  description = <text>
  javax.math.BigInteger
  </text>
}

object XNegativeInteger extends XNegativeInteger {
}
