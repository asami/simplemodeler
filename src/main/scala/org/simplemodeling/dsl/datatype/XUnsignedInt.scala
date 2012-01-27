package org.simplemodeling.dsl.datatype

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * derived from UnsingedInt since Dec. 7, 2006
 *
 * @since   Sep. 10, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XUnsignedInt() extends SDatatype {
  caption = <t>0以上の32bit整数。</t>
  brief = <t>XMLのデータタイプunsignedInt。</t>
  description = <text>
  long, java.lang.Long
  </text>
}

object XUnsignedInt extends XUnsignedInt {
}
