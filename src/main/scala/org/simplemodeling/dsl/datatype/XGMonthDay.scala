package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XGMonthDay() extends SDatatype {
  caption = <t>月日。</t>
  brief = <t>XMLのデータタイプgMonthDay。</t>
  description = <text>
  javax.xml.datatype.XMLGregorianCalendar
  </text>
}

object XGMonthDay extends XGMonthDay {
}
