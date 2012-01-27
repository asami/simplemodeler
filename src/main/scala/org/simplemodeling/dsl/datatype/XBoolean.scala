package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XBoolean() extends SDatatype {
  caption = <t>boolean。</t>
  brief = <t>XMLのデータタイプboolean。</t>
  description = <text>
  boolean, java.lang.Boolean
  </text>
}

object XBoolean extends XBoolean {
}
