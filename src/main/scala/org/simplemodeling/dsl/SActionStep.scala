package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Dec.  5, 2008
 * Dec.  6, 2008
 */
class SActionStep(anAction: String) extends SStep(anAction) {
  var action: String = anAction
/*
  def action_is(anAction: String): SStep = {
    require (anAction != null)
    require (action == "")
    action = anAction
    this
  }
*/
}
