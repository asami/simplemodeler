package org.simplemodeling.dsl.domain

import org.simplemodeling.dsl._
import org.goldenport.sdoc.SDoc
import com.asamioffice.goldenport.text.UString

/*
 * Sep. 12, 2008
 * Oct. 18, 2008
 */
class DResourceType extends SDescriptable {
  type Descriptable_TYPE = DResourceType
  var value: DResourceTypeValue = _

  def is(aValue: DResourceTypeValue): DResourceType = {
    value = aValue
    this
  }
}

object DResourceType {
  val stock = new DResourceTypeValue("stock")
}

class DResourceTypeValue(val name: String) {
  def label: String = UString.capitalize(name)
}
