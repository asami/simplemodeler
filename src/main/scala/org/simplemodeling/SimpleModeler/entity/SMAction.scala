package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.value.GKey
import org.goldenport.sdoc.{SDoc, SEmpty}
import com.asamioffice.goldenport.text.UString

/*
 * Mar. 15, 2009
 * Mar. 16, 2009
 */
class SMAction {
  var text = ""
}

class SMNullAction extends SMAction
object SMNullAction extends SMNullAction
