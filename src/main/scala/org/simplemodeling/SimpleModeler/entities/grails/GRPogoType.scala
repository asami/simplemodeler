package org.simplemodeling.SimpleModeler.entities.grails

import java.io._
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource

/*
 * Jan. 27, 2009
 * Jan. 28, 2009
 */
abstract class GRPogoType {
  def name: String
  def isEntity = false
}

class GRStringType extends GRPogoType {
  def name = "String"
}

class GRDateType extends GRPogoType {
  def name = "Date"
}

class GRUrlType extends GRPogoType {
  def name = "URL"
}

class GREntityType(val name: String, val packageName: String) extends GRPogoType {
  def this(anEntity: GRPogoEntity) = this(anEntity.name, anEntity.packageName)

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
  override def isEntity = true
}
