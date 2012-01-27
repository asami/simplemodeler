package org.simplemodeling.dsl.datatype

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * derived from GDay since Dec. 5, 2006
 *
 * @since   Sep. 10, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XGDay() extends SDatatype {
  caption = <t>日。</t>
  brief = <t>XMLのデータタイプgDay。</t>
  description = <text>
  javax.xml.datatype.XMLGregorianCalendar
  </text>
}

object XGDay extends XGDay {
}
