package org.simplemodeling.dsl.domain.values

import org.goldenport.entities.smartdoc._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._

/*
 * derived from Money since Oct. 14, 2006
 *
 * @since   Sep. 11, 2008
 * @version Nov. 13, 2010
 * @author  ASAMI, Tomoharu
 */
class Money extends DomainValue {
  caption = "金額。"
  brief = ""
  description = <text>販売価格や口座残高などの金額を記述する。</text>
}

object Money extends Money {
}
