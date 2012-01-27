package org.simplemodeling.dsl.datatype

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * derived from Date since Dec. 5, 2006
 *
 * @since   Sep. 10, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XDate() extends SDatatype {
  caption = <t>日付。</t>
  brief = <t>XMLのデータタイプdate。</t>
  description = <text>
  javax.xml.datatype.XMLGregorianCalendar
  </text>
}

object XDate extends XDate {
}
