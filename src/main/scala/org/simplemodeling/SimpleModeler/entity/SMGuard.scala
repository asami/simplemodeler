package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.value.GKey
import org.goldenport.sdoc.{SDoc, SEmpty}
import com.asamioffice.goldenport.text.UString

/*
 * Mar. 15, 2009
 * Mar. 15, 2009
 */
class SMGuard(val text: String) {
}

class SMNullGuard extends SMGuard("")
object SMNullGuard extends SMNullGuard
