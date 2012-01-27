package org.simplemodeling.SimpleModeler.entity

import org.simplemodeling.dsl.SObject
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource

/*
 * Sep. 13, 2008
 * Oct. 18, 2008
 */
class SObjectDataSource(aContext: GEntityContext)(val objects: SObject*) extends GDataSource(aContext) {
  override def is_Exist = true
}
