package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XAnyURI() extends SDatatype {
  caption = <t>URI (Uniformed Resource Identifier)。</t>
  brief = <t>XMLのデータタイプAnyURI。</t>
  description = <text>
  java.net.URI
  </text>
}

object XAnyURI extends XAnyURI {
}
