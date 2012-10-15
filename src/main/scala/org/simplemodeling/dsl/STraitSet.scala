package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._

/*
 * @since   Oct. 15, 2012
 * @version Oct. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class STraitSet {
  private val _traits = new ArrayBuffer[STrait]

  def apply(tr: STrait): STrait = {
    _traits += tr
    tr
  }
}
