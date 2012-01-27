package org.simplemodeling.dsl.datatype.ext

import org.simplemodeling.dsl._

/*
 * @since   Oct. 18, 2009
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XLink() extends SDatatype {
  caption = <t>URLリンク。</t>
  brief = <t></t>
  description = <text>

com.google.appengine.api.datastore.Link

</text>
}

object XLink extends XLink {
}
