package org.simplemodeling.dsl.datatype.business

import org.goldenport.sdoc._
import org.simplemodeling.dsl._

/*
 * @since   Oct.  7, 2012
 * @version Oct.  7, 2012
 * @author  ASAMI, Tomoharu
 */
case class XMoney() extends SDatatype {
  caption = <t>金額。</t>
  brief = <t>金額。</t>
  description = <text>
  </text>
}

object XMoney extends XMoney {
}
