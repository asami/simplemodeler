package org.simplemodeling.dsl.datatype.ext

import org.simplemodeling.dsl._

/*
 * @since   Oct. 18, 2009
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XCategory() extends SDatatype {
  caption = <t>カテゴリ(タグ)。</t>
  brief = <t></t>
  description = <text>
  com.google.appengine.api.datastore.Category
  </text>
}

object XCategory extends XCategory {
}
