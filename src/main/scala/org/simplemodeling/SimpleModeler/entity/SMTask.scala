package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}

/*
 * Dec.  2, 2008
 * Dec. 10, 2008
 */
abstract class SMTask(val dslTask: STask) extends SMStoryObject(dslTask) {
  override def kindName: String = dslTask.taskKind.label

/* 2008-12-09
  def build(aStep: SMTaskStep)
*/
}
