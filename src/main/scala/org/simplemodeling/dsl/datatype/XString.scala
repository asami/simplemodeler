package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XString() extends SDatatype {
  caption = <t>文字列。</t>
  brief = <t>XMLのデータタイプstring。</t>
  description = <text>
  java.lang.String
  </text>
}

object XString extends XString {
}
