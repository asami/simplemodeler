package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.value._

/*
 * Dec. 14, 2008
 * Dec. 15, 2008
 */
class SMExtensionSegment(val dslExtensionSegment: SExtensionSegment) {
  val flow = new PlainTree[SMStep]
  final def name= dslExtensionSegment.name
}
