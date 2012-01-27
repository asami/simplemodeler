package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XBase64Binary() extends SDatatype {
  caption = <t>Base64形式バイナリ。</t>
  brief = <t>XMLのデータタイプbase64Binary。</t>
  description = <text>
  byte配列
  </text>
}

object XBase64Binary extends XBase64Binary {
}
