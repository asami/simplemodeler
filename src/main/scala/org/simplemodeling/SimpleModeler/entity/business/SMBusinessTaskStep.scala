package org.simplemodeling.SimpleModeler.entity.business

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.business._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.sdoc._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Dec.  7, 2008
 *  version Dec. 18, 2010
 * @version Nov.  5, 2012
 * @author  ASAMI, Tomoharu
 */
class SMBusinessTaskStep(val dslBusinessTaskStep: BusinessTaskStep, val dslBusinessTask: BusinessTask) extends SMTaskStep(dslBusinessTaskStep, dslBusinessTask) {
  import scala.collection.mutable.ArrayBuffer

  val businessTasks = new ArrayBuffer[SMBusinessTask]

  override def usedEntities() = Nil

  override def includedStories() = businessTasks

  override def resolve(f: String => SMObject): Boolean = {
    f(dslBusinessTaskStep.businessTask.qualifiedName) match {
      case t: SMBusinessTask => businessTasks += t; true
      case _ => false
    }
  }
}
