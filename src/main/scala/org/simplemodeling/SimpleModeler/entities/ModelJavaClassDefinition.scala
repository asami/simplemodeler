package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessUsecase
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessTask

/*
 * @since   Jul. 14, 2011
 *  version Aug. 14, 2011
 * @version Dec. 13, 2011
 * @author  ASAMI, Tomoharu
 */
class ModelJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  isSingleton = true
  customAttributes += new PAttribute("gContext", new PObjectReferenceType(contextName, packageName), true, true)
  customAttributes += new PAttribute("gFactory", new PObjectReferenceType(factoryName, packageName), true, false)
  customAttributes += new PAttribute("gRepository", new PObjectReferenceType(repositoryName, packageName), true, true)

  override protected def head_imports_Extension {
    head_imports_Inject_Inject
  }

  override protected def constructors_copy_constructor = null
  override protected def constructors_plain_constructor = null
}
