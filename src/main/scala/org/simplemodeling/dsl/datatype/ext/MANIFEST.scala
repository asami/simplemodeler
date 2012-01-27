package org.simplemodeling.dsl.datatype.ext

import org.simplemodeling.dsl._

/*
 * @since   Nov.  4, 2009
 * @version Nov.  4, 2009
 * @author  ASAMI, Tomoharu
 */
class MANIFEST extends SManifest {
  caption = "拡張データタイプ。"
  brief = ""
  description = <text>
  </text>
  objects()
}

object asami extends SPerson {
  family_name = "浅海"
  given_name = "智晴"
}
