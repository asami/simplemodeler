package org.simplemodeling.SimpleModeler.entities

/*
 * Builder Scala Class Attribute Definition
 * 
 * @since   Aug. 19, 2011
 * @version Nov.  7, 2012
 * @author  ASAMI, Tomoharu
 */
// XXX DocumentBuilderScalaClassAttributeDefinition
class BuilderScalaClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  attr: PAttribute,
  owner: ScalaClassDefinition,
  jmaker: ScalaMaker) extends ScalaClassAttributeDefinition(pContext, aspects, attr, owner, jmaker) {

  override protected def variable_plain_entity_attribute(e: PEntityType) {
    variable_plain_Attribute_Instance_Variable(e.entity.documentName, varName);
  }

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    sm_public_instance_variable(attr, typename, varname);
  }

  override protected def method_bean_single_entity(e: PEntityType) {
    sm_public_get_method(e.entity.documentName, attrName)
    sm_public_set_method(attrName, e.entity.documentName, paramName, varName)
  }

  override protected def method_bean_multi_entity(e: PEntityType) {
    val elementtype = e.entity.documentName
    val listtype = "List<%s>".format(elementtype)

    def multi_get {
      sm_public_method("%s get%s()", listtype, attrName.capitalize) {
        sm_if_else(varName + " != null") {
          sm_pln("return Collections.unmodifiableList(%s);", varName)
        }
        sm_else {
          sm_pln("return Collections.emptyList();")
        }
      }
    }

    def multi_set {
      sm_public_method("void set%s(%s %s)", attrName.capitalize, listtype, paramName) {
        sm_assign_new_ArrayList(varName, elementtype)
        sm_pln("%s.addAll(%s);", varName, paramName)
      }

      sm_public_method("void set%s(%s %s)", attrName.capitalize, elementtype, paramName) {
        sm_assign_new_ArrayList(varName, elementtype)
        sm_pln("%s.add(%s);", varName, paramName)
      }
    }

    def multi_add {
      sm_public_method("void add%s(%s %s)", attrName.capitalize, listtype, paramName) {
        sm_if(varName + " != null") {
          sm_assign_new_ArrayList(varName, elementtype);
        }
        sm_pln("%s.addAll(%s);", varName, paramName)
      }
      sm_public_method("void add%s(%s %s)", attrName.capitalize, elementtype, paramName) {
        sm_if(varName + " != null") {
          sm_assign_new_ArrayList(varName, elementtype);
        }
        sm_pln("%s.add(%s);")
      }
    }

    multi_get
    multi_set
    multi_add
  }

  override def method_with_plain_single_entity(e: PEntityType) {
    sm_public_with_method(owner.name, attrName, e.entity.documentName, paramName, varName)    
  }

  override def method_with_plain_multi_entity(e: PEntityType) {
    sm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, java_type, paramName) {
      sm_if(varName + " != null") {
        sm_assign_new_ArrayList(varName, java_element_type);
      }
      sm_pln("%s.addAll(%s);", varName, paramName)
      sm_return_this
    }
    sm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, java_element_type, paramName) {
      sm_if(varName + " != null") {
        sm_assign_new_ArrayList(varName, java_element_type);
      }
      sm_pln("%s.add(%s);")
      sm_return_this
    }
  }
}
