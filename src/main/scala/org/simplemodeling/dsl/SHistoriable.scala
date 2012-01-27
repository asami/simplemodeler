package org.simplemodeling.dsl

import org.goldenport.sdoc._
import org.goldenport.sdoc.parts.SHistory

/*
 * Oct. 26, 2008
 * Oct. 26, 2008
 */
trait SHistoriable {
  type Historiable_TYPE
  val history = new SHistory
}
