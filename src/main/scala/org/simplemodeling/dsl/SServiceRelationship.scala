package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Nov. 19, 2008
 * Nov. 21, 2008
 */
class SServiceRelationship(val service: SService) extends SRelationship {
  type Descriptable_TYPE = SServiceRelationship
  type Historiable_TYPE = SServiceRelationship

  require (service != null)
  target = service
}
