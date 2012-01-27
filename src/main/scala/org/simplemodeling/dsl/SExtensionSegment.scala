package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc.SDoc
import org.goldenport.value._

/*
 * Dec. 12, 2008
 * Dec. 12, 2008
 */
class SExtensionSegment(val name: String) {
  val flow = new PlainTree[SStep]

  final def cursor = flow.cursor
}
