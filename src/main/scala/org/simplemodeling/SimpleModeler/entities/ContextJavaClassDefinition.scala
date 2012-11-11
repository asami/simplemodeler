package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessUsecase
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessTask

/*
 * @since   Jul. 14, 2011
 * @version Nov. 11, 2012
 * @author  ASAMI, Tomoharu
 */
class ContextJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  isSingleton = true
  customBaseName = Some("ContextBase")

  customAttributes += new PAttribute("factory", new PObjectReferenceType(factoryName, packageName))
  customAttributes += new PAttribute("moduleName", PStringType)

  override protected def head_imports_Extension {
    jm_import("org.simplemodeling.SimpleModeler.runtime.*")
    head_imports_Inject_Inject
  }

  override protected def object_auxiliary {
  }
}
