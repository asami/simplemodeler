package org.simplemodeling.dsl.business

import org.simplemodeling.dsl._

/*
 * Dec. 10, 2008
 * Dec. 10, 2008
 */
class BusinessUsecaseStep(val businessUsecase: BusinessUsecase) extends SUsecaseStep(businessUsecase) {
}

object NullBusinessUsecaseStep extends BusinessUsecaseStep(NullBusinessUsecase)
