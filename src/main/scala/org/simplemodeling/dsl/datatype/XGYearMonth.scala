package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XGYearMonth() extends SDatatype {
  caption = <t>年月。</t>
  brief = <t>XMLのデータタイプgYearMonth。</t>
  description = <text>
  javax.xml.datatype.XMLGregorianCalendar
  </text>
}

object XGYearMonth extends XGYearMonth {
}
