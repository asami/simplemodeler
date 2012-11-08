package org.simplemodeling.SimpleModeler.entities

/*
 * Builder Java Class Attribute Definition
 * 
 * @since   Jul.  8, 2011
 *  version Aug.  7, 2011
 * @version Nov.  9, 2012
 * @author  ASAMI, Tomoharu
 */
// XXX DocumentBuilderJavaClassAttributeDefinition
class BuilderJavaClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  attr: PAttribute,
  owner: JavaClassDefinition,
  jmaker: JavaMaker) extends JavaClassAttributeDefinition(pContext, aspects, attr, owner, jmaker) with JavaClassAttributeCodeUtils {
  override protected def variable_plain_entity_attribute(e: PEntityType) {
    jm_mark("// BuilderJavaClassAttributeDefinition#variable_plain_entity_attribute")
    variable_plain_Attribute_Instance_Variable(code_single_document_type(e), varName);
  }

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    jm_mark("// BuilderJavaClassAttributeDefinition#variable_plain_Attribute_Instance_Variable")
    jm_public_instance_variable(attr, typename, varname);
  }

  override protected def method_bean_single_entity(e: PEntityType) {
    jm_mark("// BuilderJavaClassAttributeDefinition#method_bean_single_entity")
    jm_public_get_method(code_single_document_type(e), attrName)
    jm_mark("// BuilderJavaClassAttributeDefinition#method_bean_single_entity")
    jm_public_set_method(attrName, code_single_document_type(e), paramName, varName)
  }

  override protected def method_bean_multi_entity(e: PEntityType) {
    val elementtype = code_single_document_type(e)
    val listtype = code_multi_document_type(e)

    def multi_get {
      jm_mark("// BuilderJavaClassAttributeDefinition#method_bean_multi_entity")
      jm_public_method("%s get%s()", listtype, attrName.capitalize) {
        jm_if_else(varName + " != null") {
          jm_pln("return Collections.unmodifiableList(%s);", varName)
        }
        jm_else {
          jm_pln("return Collections.emptyList();")
        }
      }
    }

    def multi_set {
      jm_mark("// BuilderJavaClassAttributeDefinition#method_bean_multi_entity")
      jm_public_method("void set%s(%s %s)", attrName.capitalize, listtype, paramName) {
        jm_assign_this_new_ArrayList(varName, elementtype)
        jm_pln("this.%s.addAll(%s);", varName, paramName)
      }

      jm_mark("// BuilderJavaClassAttributeDefinition#method_bean_multi_entity")
      jm_public_method("void set%s(%s %s)", attrName.capitalize, elementtype, paramName) {
        jm_assign_this_new_ArrayList(varName, elementtype)
        jm_pln("this.%s.add(%s);", varName, paramName)
      }
    }

    def multi_add {
      jm_mark("// BuilderJavaClassAttributeDefinition#method_bean_multi_entity")
      jm_public_method("void add%s(%s %s)", attrName.capitalize, listtype, paramName) {
        jm_ensure_this_new_ArrayList(varName, elementtype);
        jm_pln("this.%s.addAll(%s);", varName, paramName)
      }
      jm_mark("// BuilderJavaClassAttributeDefinition#method_bean_multi_entity")
      jm_public_method("void add%s(%s %s)", attrName.capitalize, elementtype, paramName) {
        jm_ensure_this_new_ArrayList(varName, elementtype);
        jm_pln("this.%s.add(%s);", varName, paramName)
      }
    }

    multi_get
    multi_set
    multi_add
  }

  override def method_with_plain_single_entity(e: PEntityType) {
    jm_mark("// BuilderJavaClassAttributeDefinition#method_with_plain_single_entity")
    jm_public_with_method(owner.name, attrName, code_single_document_type(e), paramName, varName)    
  }

  override def method_with_plain_multi_entity(e: PEntityType) {
    val elementtype = code_single_document_type(e)
    val containertype = code_multi_document_type(e)
    jm_mark("// BuilderJavaClassAttributeDefinition#method_with_plain_multi_entity")
    jm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, containertype, paramName) {
      jm_ensure_this_new_ArrayList(varName, elementtype)
      jm_pln("this.%s.addAll(%s);", varName, paramName)
      jm_return_this
    }
    jm_mark("// BuilderJavaClassAttributeDefinition#method_with_plain_multi_entity")
    jm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, elementtype, paramName) {
      jm_ensure_this_new_ArrayList(varName, elementtype)
      jm_pln("this.%s.add(%s);", varName, paramName)
      jm_return_this
    }
  }
}
