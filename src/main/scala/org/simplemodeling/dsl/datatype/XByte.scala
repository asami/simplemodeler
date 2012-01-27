package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XByte() extends SDatatype {
  caption = <t>8bit整数。</t>
  brief = <t>XMLのデータタイプbyte。</t>
  description = <text>
  byte, java.lang.Byte
  </text>
}

object XByte extends XByte {
}
