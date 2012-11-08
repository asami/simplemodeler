package org.simplemodeling.SimpleModeler.entities

/*
 * Value Java Class Attribute Definition
 * 
 * @since   Jul.  6, 2011
 * @version Nov.  9, 2012
 * @author  ASAMI, Tomoharu
 */
class DocumentJavaClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  attr: PAttribute,
  owner: JavaClassDefinition,
  jmaker: JavaMaker) extends JavaClassAttributeDefinition(pContext, aspects, attr, owner, jmaker) with DocumentJavaClassAttributeDefinitionBody

trait DocumentJavaClassAttributeDefinitionBody {
  self: JavaClassAttributeDefinition =>

  override protected def variable_plain_entity_attribute(e: PEntityType) {
    variable_plain_Attribute_Instance_Variable(e.entity.documentName, varName);
  }

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    jm_public_final_instance_variable(attr, typename, varname);
  }

  override protected def method_bean_single_entity(e: PEntityType) {
    def single_get {
      jm_public_method("%s get%s()", code_single_document_type(e), attrName.capitalize) {
        jm_return(varName);
      }
    }

    single_get
  }

  override protected def method_bean_multi_entity(e: PEntityType) {
    def multi_get {
      jm_public_method("%s get%s()", code_multi_document_type(e), attrName.capitalize) {
        jm_if_else(varName + " != null") {
          jm_pln("return Collections.unmodifiableList(%s);", varName)
        }
        jm_else {
          jm_pln("return Collections.emptyList();")
        }
      }
    }

    multi_get
  }
}

