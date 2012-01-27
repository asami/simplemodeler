package org.simplemodeling.dsl.domain.values

import org.goldenport.entities.smartdoc._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._

/*
 * derived from PartyName since Apr. 3, 2007
 *
 * @since   Sep. 11, 2008
 * @version Nov. 13, 2010
 * @author  ASAMI, Tomoharu
 */
class PartyName extends DomainValue {
  caption = "人または組織の名前"
  brief = ""
  description = <text>サブクラスとしてPersonNameとOrganizationNameを持っている。人の名前はPersonName, 組織の名前はOrganizationNameによって記述する。
  </text>
}

object PartyName extends PartyName {
}
