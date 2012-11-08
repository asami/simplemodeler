package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Nov.  8, 2012
 * @version Nov.  9, 2012
 * @author  ASAMI, Tomoharu
 */
class DocumentTraitJavaClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  attr: PAttribute,
  owner: JavaClassDefinition,
  jmaker: JavaMaker) extends InterfaceJavaClassAttributeDefinition(pContext, aspects, attr, owner, jmaker) with DocumentJavaClassAttributeDefinitionBody {
  
  override protected def method_bean_single_entity(e: PEntityType) {
    def single_get {
      jm_public_abstract_method("%s get%s()", code_single_document_type(e), attrName.capitalize)
    }

    single_get
  }

  override protected def method_bean_multi_entity(e: PEntityType) {
    def multi_get {
      jm_public_abstract_method("%s get%s()", code_multi_document_type(e), attrName.capitalize)
    }

    multi_get
  }
}
