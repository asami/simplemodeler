package org.simplemodeling.dsl.domain.values

import org.simplemodeling.dsl._

/*
 * derived from OrganizationName since Oct. 21, 2006
 *
 * Nov.  5, 2008
 * Nov.  6, 2008
 */
class OrganizationName extends PartyName {
  caption = "組織の名前。"
  brief = ""
  description = <text>
  基底クラスはPartyName。
  </text>
}

object OrganizationName extends OrganizationName {
}
