package org.simplemodeling.SimpleModeler.entity.requirement

import org.simplemodeling.dsl._
import org.simplemodeling.dsl.requirement._
import org.goldenport.sdoc._
import org.goldenport.sdoc.inline.SIAnchor
import org.goldenport.sdoc.inline.SElementRef
import org.goldenport.sdoc.inline.SHelpRef
import org.goldenport.values.{LayeredSequenceNumber, NullLayeredSequenceNumber}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.sdoc._

/*
 * @since   Dec. 18, 2008
 *  version Dec. 18, 2010
 * @version Nov.  5, 2012
 */
class SMRequirementTaskStep(val dslRequirementTaskStep: RequirementTaskStep, val dslRequirementTask: RequirementTask) extends SMTaskStep(dslRequirementTaskStep, dslRequirementTask) {
  import scala.collection.mutable.ArrayBuffer

  val requirementTasks = new ArrayBuffer[SMRequirementTask]

  override def usedEntities() = Nil

  override def includedStories() = requirementTasks

  override def resolve(f: String => SMObject): Boolean = {
    f(dslRequirementTaskStep.requirementTask.qualifiedName) match {
      case t: SMRequirementTask => requirementTasks += t; true
      case _ => false
    }
  }
}
