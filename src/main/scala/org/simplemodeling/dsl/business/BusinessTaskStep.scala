package org.simplemodeling.dsl.business

import org.simplemodeling.dsl._

/*
 * Dec.  7, 2008
 * Dec.  8, 2008
 */
class BusinessTaskStep(val businessTask: BusinessTask) extends STaskStep(businessTask) {
}

object NullBusinessTaskStep extends BusinessTaskStep(NullBusinessTask)
