package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Dec   3, 2008
 * Dec.  3, 2008
 */
class SRepeatPath(markRange: (String, String), aCondition: String) extends SAlternatePath(markRange, aCondition) {
  def this(aHeadMark: String, aCondition: String) = this((aHeadMark, ""), aCondition)
}
