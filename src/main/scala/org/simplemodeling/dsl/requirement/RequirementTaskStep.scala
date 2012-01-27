package org.simplemodeling.dsl.requirement

import org.simplemodeling.dsl._

/*
 * Dec. 18, 2008
 * Dec. 18, 2008
 */
class RequirementTaskStep(val requirementTask: RequirementTask) extends STaskStep(requirementTask) {
}

object NullRequirementTaskStep extends RequirementTaskStep(NullRequirementTask)
