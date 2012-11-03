package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}

/*
 * @since   Dec.  2, 2008
 *  version Dec. 10, 2008
 * @version Nov.  4, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SMTask(val dslTask: STask) extends SMStoryObject(dslTask) {
//  override def kindName: String = dslTask.taskKind.label

/* 2008-12-09
  def build(aStep: SMTaskStep)
*/
}
