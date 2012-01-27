package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XDuration() extends SDatatype {
  caption = <t>経過時間。</t>
  brief = <t>XMLのデータタイプduration。</t>
  description = <text>
  javax.xml.datatype.XMLGregorianCalendar
  </text>
}

object XDuration extends XDuration {
}
