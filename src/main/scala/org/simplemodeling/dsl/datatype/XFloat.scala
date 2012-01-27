package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XFloat() extends SDatatype {
  caption = <t>16bit浮動小数点数。</t>
  brief = <t>XMLのデータタイプfloat。</t>
  description = <text>
  float, java.lang.Float
  </text>
}

object XFloat extends XFloat {
}
