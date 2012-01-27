package org.simplemodeling.dsl.requirement

import org.simplemodeling.dsl._

/*
 * Dec. 18, 2008
 * Dec. 18, 2008
 */
class RequirementUsecaseStep(val requirementUsecase: RequirementUsecase) extends SUsecaseStep(requirementUsecase) {
}

object NullRequirementUsecaseStep extends RequirementUsecaseStep(NullRequirementUsecase)
