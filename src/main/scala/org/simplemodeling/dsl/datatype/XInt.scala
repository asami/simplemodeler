package org.simplemodeling.dsl.datatype

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * @since   Sep. 10, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XInt() extends SDatatype {
  caption = <t>32bit整数。</t>
  brief = <t>XMLのデータタイプint。</t>
  description = <text>
  int, java.lang.Integer
  </text>
}

object XInt extends XInt {
}
