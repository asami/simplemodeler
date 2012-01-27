package org.simplemodeling.dsl.business

import org.simplemodeling.dsl._

/*
 * Dec. 13, 2008
 * Dec. 13, 2008
 */
class ExtendBusinessUsecaseStep(aExtensionPointName: String, val businessUsecases: Seq[(String, ExtensionBusinessUsecase)]) extends SExtendUsecaseStep(aExtensionPointName, businessUsecases) {
}
