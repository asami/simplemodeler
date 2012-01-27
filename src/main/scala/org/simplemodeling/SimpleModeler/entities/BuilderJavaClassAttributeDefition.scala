package org.simplemodeling.SimpleModeler.entities

/*
 * Builder Java Class Attribute Definition
 * 
 * @since   Jul.  8, 2011
 * @version Aug.  7, 2011
 * @author  ASAMI, Tomoharu
 */
// XXX DocumentBuilderJavaClassAttributeDefinition
class BuilderJavaClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  attr: PAttribute,
  owner: JavaClassDefinition,
  jmaker: JavaMaker) extends JavaClassAttributeDefinition(pContext, aspects, attr, owner, jmaker) {

  override protected def variable_plain_entity_attribute(e: PEntityType) {
    variable_plain_Attribute_Instance_Variable(e.entity.documentName, varName);
  }

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    jm_public_instance_variable(attr, typename, varname);
  }

  override protected def method_bean_single_entity(e: PEntityType) {
    jm_public_get_method(e.entity.documentName, attrName)
    jm_public_set_method(attrName, e.entity.documentName, paramName, varName)
  }

  override protected def method_bean_multi_entity(e: PEntityType) {
    val elementtype = e.entity.documentName
    val listtype = "List<%s>".format(elementtype)

    def multi_get {
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
      jm_public_method("void set%s(%s %s)", attrName.capitalize, listtype, paramName) {
        jm_assign_new_ArrayList(varName, elementtype)
        jm_pln("%s.addAll(%s);", varName, paramName)
      }

      jm_public_method("void set%s(%s %s)", attrName.capitalize, elementtype, paramName) {
        jm_assign_new_ArrayList(varName, elementtype)
        jm_pln("%s.add(%s);", varName, paramName)
      }
    }

    def multi_add {
      jm_public_method("void add%s(%s %s)", attrName.capitalize, listtype, paramName) {
        jm_if(varName + " != null") {
          jm_assign_new_ArrayList(varName, elementtype);
        }
        jm_pln("%s.addAll(%s);")
      }
      jm_public_method("void add%s(%s %s)", attrName.capitalize, elementtype, paramName) {
        jm_if(varName + " != null") {
          jm_assign_new_ArrayList(varName, elementtype);
        }
        jm_pln("%s.add(%s);")
      }
    }

    multi_get
    multi_set
    multi_add
  }

  override def method_with_plain_single_entity(e: PEntityType) {
    jm_public_with_method(owner.name, attrName, e.entity.documentName, paramName, varName)    
  }

  override def method_with_plain_multi_entity(e: PEntityType) {
    jm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, java_type, paramName) {
      jm_if(varName + " != null") {
        jm_assign_new_ArrayList(varName, java_element_type);
      }
      jm_pln("%s.addAll(%s);")
      jm_return_this
    }
    jm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, java_element_type, paramName) {
      jm_if(varName + " != null") {
        jm_assign_new_ArrayList(varName, java_element_type);
      }
      jm_pln("%s.add(%s);")
      jm_return_this
    }
  }
}
