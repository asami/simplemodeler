package org.simplemodeling.dsl.datatype

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * derived from DateTime since Oct. 13, 2006
 *
 * @since   Sep. 10, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XDateTime() extends SDatatype {
  caption = <t>日付＋時間。</t>
  brief = <t>XMLのデータタイプdateTime。</t>
  description = <text>
  javax.xml.datatype.XMLGregorianCalendar
  </text>
}

object XDateTime extends XDateTime {
}
