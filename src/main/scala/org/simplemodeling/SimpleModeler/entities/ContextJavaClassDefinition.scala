package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessUsecase
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessTask

/*
 * @since   Jul. 14, 2011
 * @version Aug. 14, 2011
 * @author  ASAMI, Tomoharu
 */
class ContextJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  isSingleton = true
  customAttributes += new PAttribute("factory", new PObjectReferenceType(factoryName, packageName))
  customAttributes += new PAttribute("moduleName", PStringType)

  override protected def object_auxiliary {
  }
}
