package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Dec   3, 2008
 * Dec.  4, 2008
 */
class SJumpPath(aMarkRange: (String, String), aCondition: String, val destination: String) extends SAlternatePath(aMarkRange, aCondition) {

  def this(aHeadMark: String, aCondition: String, aDestination: String) = this((aHeadMark, ""), aCondition, aDestination)
}
