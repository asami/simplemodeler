package org.simplemodeling.dsl.domain.values

import org.goldenport.entities.smartdoc._
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._

/*
 * derived from PartyAddress since Oct. 13, 2006
 *
 * @since   Sep. 11, 2008
 * @version Nov. 13, 2010
 * @author  ASAMI, Tomoharu
 */
class PartyAddress extends DomainValue {
  caption = "人や組織の住所"
  brief = ""
  description = <text>
  </text>
}

object PartyAddress extends PartyAddress {
}
