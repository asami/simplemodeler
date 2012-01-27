package org.simplemodeling.dsl.datatype

import org.simplemodeling.dsl._

/*
 * @since   Nov.  6, 2008
 * @version Nov. 12, 2010
 * @author  ASAMI, Tomoharu
 */
case class XLanguage() extends SDatatype {
  caption = <t>言語。</t>
  brief = <t>XMLのデータタイプlanguage。</t>
  description = <text>
  java.util.Locale
  </text>
}

object XLanguage extends XLanguage {
}
