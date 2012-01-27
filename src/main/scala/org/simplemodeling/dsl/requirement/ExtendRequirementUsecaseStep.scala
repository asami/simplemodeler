package org.simplemodeling.dsl.requirement

import org.simplemodeling.dsl._

/*
 * Dec. 17, 2008
 * Dec. 17, 2008
 */
class ExtendRequirementUsecaseStep(aExtensionPointName: String, val requirementUsecases: Seq[(String, ExtensionRequirementUsecase)]) extends SExtendUsecaseStep(aExtensionPointName, requirementUsecases) {
}
