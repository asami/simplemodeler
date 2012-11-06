package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Nov.  6, 2012
 * @version Nov.  6, 2012
 * @author  ASAMI, Tomoharu
 */
class InterfaceJavaClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  attr: PAttribute,
  owner: JavaClassDefinition,
  jmaker: JavaMaker) extends JavaClassAttributeDefinition(pContext, aspects, attr, owner, jmaker) {
  override def variable_id {}
  override def variable_plain {}

  override protected def method_bean_single_plain() {
    single_value_attribute_abstract_method
  }

  override protected def method_bean_single_byte() {
    mapping_multi_value_attribute_abstract_method("%s.byteValue()", "%s.shortValue()") // XXX app engine ?
  }

  override protected def method_bean_single_integer() {
    mapping_multi_value_attribute_abstract_method("new BigInteger(%s)", "%s.toString()")
  }

  override protected def method_bean_single_decimal() {
    mapping_multi_value_attribute_abstract_method("new BigDecimal(%s)", "%s.toString()")
  }

  override def method_bean_single_entity_Simple(e: PEntityType) {
    single_value_attribute_abstract_method
  }

  override def method_bean_single_entity_Composition_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Aggregation_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Association_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Composition_Id_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Aggregation_Id_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Association_Id_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Composition_Id_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Aggregation_Id_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Association_Id_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_single_entity_Query_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

//     jm_public_void_method("set%s(%s %s)", attrName.capitalize, javaType, paramName) {

  // Part
  override protected def method_bean_single_part(p: PEntityPartType) {
    jm_public_get_abstract_method(javaType, attrName, erPartVarName)
    if (attr.attributeType == PBooleanType) {
      jm_public_is_abstract_method(attrName, varName)
    }
    if (is_settable()) {
      jm_public_set_abstract_method(attrName, javaType, paramName, erPartVarName)
    }
  }

  override protected def method_bean_single_powertype(e: PPowertypeType) {
    jm_public_get_abstract_method(javaType, attrName, varName); // erPowerVarName); 
    if (attr.attributeType == PBooleanType) {
      jm_public_is_abstract_method(attrName, varName)
    }
    if (is_settable()) {
      jm_public_set_abstract_method(attrName, javaType, paramName, varName); // erPowerVarName);
    }
  }

  override protected def method_bean_multi_plain() {
    multi_value_attribute_abstract_method
  }

  override protected def method_bean_multi_byte() {
    mapping_single_value_attribute_abstract_method("%s.byteValue()", "%s.shortValue()")
  }

  override protected def method_bean_multi_integer() {
    mapping_single_value_attribute_abstract_method("new BigInteger(%s)", "%s.toString()")
  }    

  override protected def method_bean_multi_decimal() {
    mapping_single_value_attribute_abstract_method("new BigDecimal(%s)", "%s.toString()")
  }

  override def method_bean_multi_entity_Simple(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_multi_entity_Composition_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_multi_entity_Aggregation_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_multi_entity_Association_Reference_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_multi_entity_Composition_Id_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_multi_entity_Aggregation_Id_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_multi_entity_Association_Id_Property(e: PEntityType) {
    sys.error("not supported yet")
  }

  override def method_bean_multi_entity_Composition_Id_Reference_Property(e: PEntityType) {
    jm_pln("fill_%s();".format(attrName))
    jm_if_else(erAssocVarName + " != null") {
      jm_pln("return Collections.unmodifiableList(%s);", erAssocVarName)
    }
    jm_else {
      jm_pln("return Collections.emptyList();")
    }
  }

  override def method_bean_multi_entity_Aggregation_Id_Reference_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Association_Id_Reference_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Query_Property(e: PEntityType) {
  }

  // part
  override protected def method_bean_multi_part(p: PEntityPartType) {
    jm_public_abstract_method("public " + javaType + " get" + attrName.capitalize + " ()")
    if (is_settable) {
      jm_public_void_abstract_method("set" + attrName.capitalize + "(" + javaType + " " + paramName + ")")
      jm_public_void_abstract_method("public void add" + attrName.capitalize + "(" + javaElementType + " " + paramName + ")")
    }
  }

  override protected def method_bean_multi_powertype(e: PPowertypeType) {
    jm_public_get_list_abstract_method_prologue(javaType, attrName, varName) 
    if (is_settable()) {
      jm_public_set_list_abstract_method(attrName, javaElementType, paramName, varName) // erPowerVarName)
      jm_public_add_list_element_abstract_method(attrName, javaElementType, paramName, varName) // erPowerVarName)
    }
  }

  /*
   * method_with
   */
  override def method_with {}
}

