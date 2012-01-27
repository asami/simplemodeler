package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Nov. 19, 2008
 * Nov. 21, 2008
 */
class SRuleRelationship(val rule: SRule) extends SRelationship {
  type Descriptable_TYPE = SRuleRelationship
  type Historiable_TYPE = SRuleRelationship

  require (rule != null)
  target = rule
}
