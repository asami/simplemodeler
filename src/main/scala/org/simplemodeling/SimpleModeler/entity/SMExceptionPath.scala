package org.simplemodeling.SimpleModeler.entity

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef

/*
 * Dec.  3, 2008
 * Dec.  4, 2008
 */
class SMExceptionPath(dslPath: SExceptionPath) extends SMPath(dslPath) {
  val targetSteps = new ArrayBuffer[SMStep]
  def terminateProcedure = dslPath.terminateProcedure
}
