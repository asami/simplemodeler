package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * @since   Oct. 16, 2012
 * @version Oct. 16, 2012
 * @author  ASAMI, Tomoharu
 */
class STraitRelationship(val mixinTrait: STrait) extends SRelationship {
  type Descriptable_TYPE = STraitRelationship
  type Historiable_TYPE = STraitRelationship

  require (mixinTrait != null && mixinTrait != NullTrait)
  target = mixinTrait
}
