package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Dec   2, 2008
 * Dec.  3, 2008
 */
class SExceptionPath(markRange: (String, String), aCondition: String) extends SPath(markRange, aCondition) {
  def this(aHeadMark: String, aCondition: String) = this((aHeadMark, ""), aCondition)

  var terminateProcedure: String = ""
}
