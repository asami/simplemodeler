package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Nov. 10, 2008
 * Nov. 19, 2008
 */
class SUse(aName: String) extends SRelationship(aName) {
  type Descriptable_TYPE = SUse
  type Historiable_TYPE = SUse
}
