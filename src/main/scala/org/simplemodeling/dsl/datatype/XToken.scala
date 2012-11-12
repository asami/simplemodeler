package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov. 12, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
case class XToken() extends SDatatype {
  caption = <t>文字列。</t>
  brief = <t>XMLのデータタイプtoken。</t>
  description = <text>
  java.lang.String
  </text>
}

object XToken extends XToken {
}
