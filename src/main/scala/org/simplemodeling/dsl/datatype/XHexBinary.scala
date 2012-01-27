package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XHexBinary() extends SDatatype {
  caption = <t>Hex形式バイナリ。</t>
  brief = <t>XMLのデータタイプhexBinary。</t>
  description = <text>
  byte配列
  </text>
}

object XHexBinary extends XHexBinary {
}
