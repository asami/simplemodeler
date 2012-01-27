package org.simplemodeling.dsl.domain.values

import org.simplemodeling.dsl._

/*
 * derived from PersonName since Oct. 13, 2006
 *
 * Sep. 11, 2008
 * Nov.  6, 2008
 */
class PersonName extends PartyName {
  caption = "人の名前。"
  brief = ""
  description = <text>
  基底クラスはPartyName。
  </text>
}

object PersonName extends PersonName {
}
