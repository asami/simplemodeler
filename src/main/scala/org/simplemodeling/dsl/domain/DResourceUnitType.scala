package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._
import org.goldenport.sdoc.SDoc

/*
 * Sep. 12, 2008
 * Oct. 18, 2008
 */
class DResourceUnitType extends SDescriptable {
  type Descriptable_TYPE = DResourceUnitType
  var value: DResourceUnitTypeValue = _

  def is(aValue: DResourceUnitTypeValue): DResourceUnitType = {
    value = aValue
    this
  }
}

object DResourceUnitType {
  val individual = new DResourceUnitTypeValue("individual")
}

class DResourceUnitTypeValue(val name: String) {
}
