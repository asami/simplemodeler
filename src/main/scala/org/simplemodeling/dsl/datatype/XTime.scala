package org.simplemodeling.dsl.datatype

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * derived from Time since Dec. 5, 2006
 *
 * @since   Sep. 10, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XTime() extends SDatatype {
  caption = <t>時間。</t>
  brief = <t>XMLのデータタイプtime。</t>
  description = <text>
  javax.xml.datatype.XMLGregorianCalendar
  </text>
}

object XTime extends XTime {
}
