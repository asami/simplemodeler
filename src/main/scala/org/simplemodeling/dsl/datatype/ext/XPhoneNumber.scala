package org.simplemodeling.dsl.datatype.ext

import org.simplemodeling.dsl._

/*
 * @since   Oct. 18, 2009
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XPhoneNumber() extends SDatatype {
  caption = <t>文字列。</t>
  brief = <t></t>
  description = <text>
  com.google.appengine.api.datastore.PhoneNumber
  </text>
}

object XPhoneNumber extends XPhoneNumber {
}
