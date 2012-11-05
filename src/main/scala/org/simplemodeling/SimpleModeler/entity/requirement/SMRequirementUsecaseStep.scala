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
 * @author  ASAMI, Tomoharu
 */
class SMRequirementUsecaseStep(val dslRequirementUsecaseStep: RequirementUsecaseStep, val dslRequirementUsecase: RequirementUsecase) extends SMUsecaseStep(dslRequirementUsecaseStep, dslRequirementUsecase) {
  import scala.collection.mutable.ArrayBuffer

  val requirementUsecases = new ArrayBuffer[SMRequirementUsecase]

  override def usedEntities() = Nil

  override def includedStories() = requirementUsecases

  override def resolve(f: String => SMObject): Boolean = {
    f(dslRequirementUsecaseStep.requirementUsecase.qualifiedName) match {
      case t: SMRequirementUsecase => requirementUsecases += t; true
      case _ => false
    }
  }
}
