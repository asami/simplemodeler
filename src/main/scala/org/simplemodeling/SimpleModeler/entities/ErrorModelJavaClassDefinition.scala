package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessUsecase
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessTask

/*
 * @since   Aug.  7, 2011
 * @version Aug. 14, 2011
 * @author  ASAMI, Tomoharu
 */
class ErrorModelJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  isSingleton = true
}
