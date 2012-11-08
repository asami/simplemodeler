package org.simplemodeling.SimpleModeler.entities

import org.apache.commons.lang3.StringUtils

/*
 * @since   Nov.  8, 2012
 * @version Nov.  8, 2012
 * @author  ASAMI, Tomoharu
 */
class DocumentTraitJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends TraitJavaClassDefinition(pContext, aspects, pobject) {
  isImmutable = true
  customBaseName = baseObject.map(x => pContext.entityDocumentName(x.reference))

  override protected def attribute(attr: PAttribute) = {
    new DocumentTraitJavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker)
  }
}
