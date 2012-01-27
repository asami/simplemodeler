package org.simplemodeling.SimpleModeler.builder

import org.simplemodeling.SimpleModeler.entities.simplemodel.SMMEntityEntity
import org.simplemodeling.dsl.SObject
import org.simplemodeling.SimpleModeler.entities.simplemodel.ElementKind

/*
 * @since   Dec. 11, 2011
 *  version Dec. 13, 2011
 * @version Jan. 24, 2012
 * @author  ASAMI, Tomoharu
 */
trait SimpleModelBuilder {
  def dslObjects: List[SObject] // XXX
  def createObject(kind: ElementKind, name: String): SMMEntityEntity
  def createObject(kind: String, name: String): SMMEntityEntity
  def makeAttributesForDerivedObject(obj: SMMEntityEntity)
  def makeAttributesForBaseObject(obj: SMMEntityEntity)
//  def makeNarrativeAttributes(obj: SMMEntityEntity)  
}