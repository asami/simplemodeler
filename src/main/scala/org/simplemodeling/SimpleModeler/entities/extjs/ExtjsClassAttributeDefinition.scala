package org.simplemodeling.SimpleModeler.entities.extjs

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * @since   Apr.  4, 2012
 * @version Apr.  7, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[ExtjsAspect],
  attr: PAttribute,
  owner: ExtjsClassDefinition,
  jmaker: ExtjsTextMaker) extends GenericClassAttributeDefinition(pContext, aspects, attr, owner) with ExtjsMakerHolder {
  type ATTR_DEF = ExtjsClassAttributeDefinition

  ej_open(ejmaker, aspects)

  override protected def head_imports_Extension {
    jm_import("org.json.*")
  }

  override def constant_property {
    jm_public_static_final_String_literal(propertyConstantName, propertyLiteral)
  }

  override protected def variable_plain_Inject_Annotation {
    jm_pln("@Inject")
  }

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    jm_private_instance_variable(attr, typename, varname);
  }

  override protected def variable_plain_Transient_Instance_Variable(typename: String, varname: String) {
    jm_private_transient_instance_variable(attr, typename, varname);
  }

  override protected def method_bean_single_plain() {
    single_value_attribute_method
  }

  protected final def mapping_single_value_attribute_method(getter: String, setter: String) {
    jm_public_get_or_null_method(javaType, attrName, getter, attrName)
    if (attr.attributeType == PBooleanType) {
      jm_public_is_method(attrName, varName);
    }
    if (is_settable()) {
      jm_public_set_or_null_method(attrName, javaType, paramName, setter)
      jm_public_with_or_null_method(owner.name, attrName, javaType, paramName, setter)
    }
  }

  protected final def single_value_attribute_method() {
    jm_public_get_method(javaType, attrName, varName)
    if (attr.attributeType == PBooleanType) {
      jm_public_is_method(attrName, varName)
    }
    if (is_settable()) {
      jm_public_set_method(attrName, javaType, paramName, varName)
    }
  }

  override protected def method_bean_single_byte() {
    mapping_multi_value_attribute_method("%s.byteValue()", "%s.shortValue()") // XXX app engine ?
  }

  override protected def method_bean_single_integer() {
    mapping_multi_value_attribute_method("new BigInteger(%s)", "%s.toString()")
  }    

  override protected def method_bean_single_decimal() {
    mapping_multi_value_attribute_method("new BigDecimal(%s)", "%s.toString()")
  }

  def method_bean_single_entity_Simple(e: PEntityType) {
    single_value_attribute_method
  }

  def method_bean_single_entity_Composition_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Aggregation_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Association_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Composition_Id_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Aggregation_Id_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Association_Id_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Composition_Id_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Aggregation_Id_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Association_Id_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_single_entity_Query_Property(e: PEntityType) {
    error("not supported yet")
  }

//     jm_public_void_method("set%s(%s %s)", attrName.capitalize, javaType, paramName) {

  // Part
  override protected def method_bean_single_part(p: PEntityPartType) {
    jm_public_get_method(javaType, attrName, erPartVarName)
    if (attr.attributeType == PBooleanType) {
      jm_public_is_method(attrName, varName)
    }
    if (is_settable()) {
      jm_public_set_method(attrName, javaType, paramName, erPartVarName)
    }
  }

  override protected def method_bean_single_powertype(e: PPowertypeType) {
    jm_public_get_method(javaType, attrName, varName); // erPowerVarName); 
    if (attr.attributeType == PBooleanType) {
      jm_public_is_method(attrName, varName)
    }
    if (is_settable()) {
      jm_public_set_method(attrName, javaType, paramName, varName); // erPowerVarName);
    }
  }

  override protected def method_bean_multi_plain() {
    multi_value_attribute_method
  }

  override protected def method_bean_multi_byte() {
    mapping_single_value_attribute_method("%s.byteValue()", "%s.shortValue()")
  }

  override protected def method_bean_multi_integer() {
    mapping_single_value_attribute_method("new BigInteger(%s)", "%s.toString()")
  }    

  override protected def method_bean_multi_decimal() {
    mapping_single_value_attribute_method("new BigDecimal(%s)", "%s.toString()")
  }

  def method_bean_multi_entity_Simple(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_multi_entity_Composition_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_multi_entity_Aggregation_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_multi_entity_Association_Reference_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_multi_entity_Composition_Id_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_multi_entity_Aggregation_Id_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_multi_entity_Association_Id_Property(e: PEntityType) {
    error("not supported yet")
  }

  def method_bean_multi_entity_Composition_Id_Reference_Property(e: PEntityType) {
    jm_pln("fill_%s();".format(attrName))
    jm_if_else(erAssocVarName + " != null") {
      jm_pln("return Collections.unmodifiableList(%s);", erAssocVarName)
    }
    jm_else {
      jm_pln("return Collections.emptyList();")
    }
  }

  def method_bean_multi_entity_Aggregation_Id_Reference_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Association_Id_Reference_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Query_Property(e: PEntityType) {
  }

  // part
  override protected def method_bean_multi_part(p: PEntityPartType) {
    jm_public_method("public " + javaType + " get" + attrName.capitalize + " ()") {
      jm_pln("fill_%s();", attrName)
      jm_if_else(erPartVarName + " != null") {
        jm_pln("return Collections.unmodifiableList(%s);", erPartVarName)
      }
      jm_else {
        jm_pln("return Collections.emptyList();")
      }
    }
    if (is_settable) {
      jm_public_void_method("set" + attrName.capitalize + "(" + javaType + " " + paramName + ")") {
        jm_assign_this(erPartVarName, "new ArrayList<%s>(%s)", javaElementType, paramName)  
      }
      jm_public_void_method("public void add" + attrName.capitalize + "(" + javaElementType + " " + paramName + ")") {
        jm_if_null("this." + erPartVarName) {
          jm_assign_this(erPartVarName, "new ArrayList<%s>()", javaElementType) 
        }
        jm_pln("this.%s.add(%s);", erPartVarName, paramName) 
      }
    }
  }

  override protected def method_bean_multi_powertype(e: PPowertypeType) {
    jm_public_get_list_method_prologue(javaType, attrName, varName) { // erPowerVarName) {
      jm_pln("fill_%s()", attrName)
    }
    if (is_settable()) {
      jm_public_set_list_method(attrName, javaElementType, paramName, varName) // erPowerVarName)
      jm_public_add_list_element_method(attrName, javaElementType, paramName, varName) // erPowerVarName)
    }
  }

  def multi_value_attribute_method {
    jm_public_get_list_method(javaType, attrName, erPersistentVarName)
    if (is_settable()) {
      jm_public_set_list_method(attrName, javaElementType, paramName, erPersistentVarName)
      jm_public_add_list_element_method(attrName, javaElementType, paramName, erPersistentVarName)
    }
  }

  def mapping_multi_value_attribute_method(getter: String, setter: String) {
    jm_public_method("%s get%s()", javaType, attrName.capitalize) {
      jm_if_else_not_null(attrName) {
        jm_var_List_new_ArrayList("result", attr.elementTypeName); 
        jm_for(persistent_element_type, "elem", attrName) {
          jm_pln("result.add(%s);", getter.format("elem"))
        }
        jm_return("result")
      }
      jm_else {
        jm_return("Collections.emptyList()");
      }
    }
    if (is_settable()) {
      jm_public_set_list_method(attrName, javaElementType, paramName, erPartVarName)
      jm_public_add_list_element_method(attrName, javaElementType, paramName, erPartVarName)
    }
  }

  /*
   * method_as
   */
  override def method_as_string_multi_entity(e: PEntityType) {
    
  }

  override def method_as_string_multi_part(e: PEntityPartType) {
    
  }

  override def method_as_string_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_as_string_multi_byte {
    
  }

  override def method_as_string_multi_integer {
    
  }

  override def method_as_string_multi_decimal {
    
  }

  override def method_as_string_multi_plain {
    
  }

  override def method_as_string_single_entity(e: PEntityType) {
    
  }

  override def method_as_string_single_part(e: PEntityPartType) {
    
  }

  override def method_as_string_single_powertype(e: PPowertypeType) {
    
  }

  override def method_as_string_single_byte {
    
  }

  override def method_as_string_single_integer {
    
  }

  override def method_as_string_single_decimal {
    
  }

  override def method_as_string_single_plain {
    
  }

  // as_xml
  override def method_as_xml_multi_entity(e: PEntityType) {
    
  }

  override def method_as_xml_multi_part(e: PEntityPartType) {
    
  }

  override def method_as_xml_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_as_xml_multi_byte {
    
  }

  override def method_as_xml_multi_integer {
    
  }

  override def method_as_xml_multi_decimal {
    
  }

  override def method_as_xml_multi_plain {
    
  }

  override def method_as_xml_single_entity(e: PEntityType) {
    
  }

  override def method_as_xml_single_part(e: PEntityPartType) {
    
  }

  override def method_as_xml_single_powertype(e: PPowertypeType) {
    
  }

  override def method_as_xml_single_byte {
    
  }

  override def method_as_xml_single_integer {
    
  }

  override def method_as_xml_single_decimal {
    
  }

  override def method_as_xml_single_plain {
    
  }

  // as_json
  override def method_as_json_multi_entity(e: PEntityType) {
    
  }

  override def method_as_json_multi_part(e: PEntityPartType) {
    
  }

  override def method_as_json_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_as_json_multi_byte {
    
  }

  override def method_as_json_multi_integer {
    
  }

  override def method_as_json_multi_decimal {
    
  }

  override def method_as_json_multi_plain {
    
  }

  override def method_as_json_single_entity(e: PEntityType) {
    
  }

  override def method_as_json_single_part(e: PEntityPartType) {
    
  }

  override def method_as_json_single_powertype(e: PPowertypeType) {
    
  }

  override def method_as_json_single_byte {
    
  }

  override def method_as_json_single_integer {
    
  }

  override def method_as_json_single_decimal {
    
  }

  override def method_as_json_single_plain {
    
  }

  // as_csv
  override def method_as_csv_multi_entity(e: PEntityType) {
    
  }

  override def method_as_csv_multi_part(e: PEntityPartType) {
    
  }

  override def method_as_csv_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_as_csv_multi_byte {
    
  }

  override def method_as_csv_multi_integer {
    
  }

  override def method_as_csv_multi_decimal {
    
  }

  override def method_as_csv_multi_plain {
    
  }

  override def method_as_csv_single_entity(e: PEntityType) {
    
  }

  override def method_as_csv_single_part(e: PEntityPartType) {
    
  }

  override def method_as_csv_single_powertype(e: PPowertypeType) {
    
  }

  override def method_as_csv_single_byte {
    
  }

  override def method_as_csv_single_integer {
    
  }

  override def method_as_csv_single_decimal {
    
  }

  override def method_as_csv_single_plain {
    
  }

  // as_urlencode
  override def method_as_urlencode_multi_entity(e: PEntityType) {
    
  }

  override def method_as_urlencode_multi_part(e: PEntityPartType) {
    
  }

  override def method_as_urlencode_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_as_urlencode_multi_byte {
    
  }

  override def method_as_urlencode_multi_integer {
    
  }

  override def method_as_urlencode_multi_decimal {
    
  }

  override def method_as_urlencode_multi_plain {
    
  }

  override def method_as_urlencode_single_entity(e: PEntityType) {
    
  }

  override def method_as_urlencode_single_part(e: PEntityPartType) {
    
  }

  override def method_as_urlencode_single_powertype(e: PPowertypeType) {
    
  }

  override def method_as_urlencode_single_byte {
    
  }

  override def method_as_urlencode_single_integer {
    
  }

  override def method_as_urlencode_single_decimal {
    
  }

  override def method_as_urlencode_single_plain {
    
  }

  /*
   * method_by
   */
  override def method_by_string_multi_entity(e: PEntityType) {
    
  }

  override def method_by_string_multi_part(e: PEntityPartType) {
    
  }

  override def method_by_string_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_by_string_multi_byte {
    
  }

  override def method_by_string_multi_integer {
    
  }

  override def method_by_string_multi_decimal {
    
  }

  override def method_by_string_multi_plain {
    
  }

  override def method_by_string_single_entity(e: PEntityType) {
    
  }

  override def method_by_string_single_part(e: PEntityPartType) {
    
  }

  override def method_by_string_single_powertype(e: PPowertypeType) {
    
  }

  override def method_by_string_single_byte {
    
  }

  override def method_by_string_single_integer {
    
  }

  override def method_by_string_single_decimal {
    
  }

  override def method_by_string_single_plain {
    
  }

  // by_xml
  override def method_by_xml_multi_entity(e: PEntityType) {
    
  }

  override def method_by_xml_multi_part(e: PEntityPartType) {
    
  }

  override def method_by_xml_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_by_xml_multi_byte {
    
  }

  override def method_by_xml_multi_integer {
    
  }

  override def method_by_xml_multi_decimal {
    
  }

  override def method_by_xml_multi_plain {
    
  }

  override def method_by_xml_single_entity(e: PEntityType) {
    
  }

  override def method_by_xml_single_part(e: PEntityPartType) {
    
  }

  override def method_by_xml_single_powertype(e: PPowertypeType) {
    
  }

  override def method_by_xml_single_byte {
    
  }

  override def method_by_xml_single_integer {
    
  }

  override def method_by_xml_single_decimal {
    
  }

  override def method_by_xml_single_plain {
    
  }

  // by_json
  override def method_by_json_multi_entity(e: PEntityType) {
    
  }

  override def method_by_json_multi_part(e: PEntityPartType) {
    
  }

  override def method_by_json_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_by_json_multi_byte {
    
  }

  override def method_by_json_multi_integer {
    
  }

  override def method_by_json_multi_decimal {
    
  }

  override def method_by_json_multi_plain {
    
  }

  override def method_by_json_single_entity(e: PEntityType) {
//    jm_method("public %s with%s_json(JSONObject json) throws JSONException", owner.name, attrName.capitalize) {
//      jm_assign_this(attrName, "%s.getFactory().create%s(json)", owner.factoryName, e.entity.documentName)
//      jm_return_this
//    } 
  }

  override def method_by_json_single_part(e: PEntityPartType) {
    
  }

  override def method_by_json_single_powertype(e: PPowertypeType) {
    
  }

  override def method_by_json_single_byte {
    
  }

  override def method_by_json_single_integer {
    
  }

  override def method_by_json_single_decimal {
    
  }

  override def method_by_json_single_plain {
  }

  // by_csv
  override def method_by_csv_multi_entity(e: PEntityType) {
    
  }

  override def method_by_csv_multi_part(e: PEntityPartType) {
    
  }

  override def method_by_csv_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_by_csv_multi_byte {
    
  }

  override def method_by_csv_multi_integer {
    
  }

  override def method_by_csv_multi_decimal {
    
  }

  override def method_by_csv_multi_plain {
    
  }

  override def method_by_csv_single_entity(e: PEntityType) {
    
  }

  override def method_by_csv_single_part(e: PEntityPartType) {
    
  }

  override def method_by_csv_single_powertype(e: PPowertypeType) {
    
  }

  override def method_by_csv_single_byte {
    
  }

  override def method_by_csv_single_integer {
    
  }

  override def method_by_csv_single_decimal {
    
  }

  override def method_by_csv_single_plain {
    
  }

  // by_urlencode
  override def method_by_urlencode_multi_entity(e: PEntityType) {
    
  }

  override def method_by_urlencode_multi_part(e: PEntityPartType) {
    
  }

  override def method_by_urlencode_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_by_urlencode_multi_byte {
    
  }

  override def method_by_urlencode_multi_integer {
    
  }

  override def method_by_urlencode_multi_decimal {
    
  }

  override def method_by_urlencode_multi_plain {
    
  }

  override def method_by_urlencode_single_entity(e: PEntityType) {
    
  }

  override def method_by_urlencode_single_part(e: PEntityPartType) {
    
  }

  override def method_by_urlencode_single_powertype(e: PPowertypeType) {
    
  }

  override def method_by_urlencode_single_byte {
    
  }

  override def method_by_urlencode_single_integer {
    
  }

  override def method_by_urlencode_single_decimal {
    
  }

  override def method_by_urlencode_single_plain {
    
  }
  
  /*
   * method_with
   */
  override def method_with_plain_multi_entity(e: PEntityType) {
    val vname = "this." + varName
    jm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, javaType, paramName) {
      jm_if(vname + " != null") {
        jm_assign_new_ArrayList(vname, javaElementType);
      }
      jm_pln("this.%s.addAll(%s);", vname, paramName)
      jm_return_this
    }
    jm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, javaElementType, paramName) {
      jm_if(vname + " != null") {
        jm_assign_new_ArrayList(vname, javaElementType);
      }
      jm_pln("this.%s.add(%s);", vname, paramName)
      jm_return_this
    }
  }

  override def method_with_plain_multi_part(e: PEntityPartType) {
    
  }

  override def method_with_plain_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_with_plain_multi_byte {
    
  }

  override def method_with_plain_multi_integer {
    
  }

  override def method_with_plain_multi_decimal {
    
  }

  override def method_with_plain_multi_plain {
    val vname = "this." + varName
    jm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, javaType, paramName) {
      jm_if(vname + " != null") {
        jm_assign_new_ArrayList(vname, javaElementType);
      }
      jm_pln("%s.addAll(%s);", vname, paramName)
      jm_return_this
    }
    jm_public_method("%s with%s(%s %s)", owner.name, attrName.capitalize, javaElementType, paramName) {
      jm_if(vname + " != null") {
        jm_assign_new_ArrayList(vname, javaElementType);
      }
      jm_pln("%s.add(%s);", vname, paramName)
      jm_return_this
    }
  }

  override def method_with_plain_single_entity(e: PEntityType) {
    jm_public_with_method(owner.name, attrName, e.entity.name, paramName, varName)
  }

  override def method_with_plain_single_part(e: PEntityPartType) {
    
  }

  override def method_with_plain_single_powertype(e: PPowertypeType) {
    
  }

  override def method_with_plain_single_byte {
    
  }

  override def method_with_plain_single_integer {
    
  }

  override def method_with_plain_single_decimal {
    
  }

  override def method_with_plain_single_plain {
    jm_public_with_method(owner.name, attrName, javaType, paramName, varName)
  }

  override def method_with_string {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_with_string_multi_entity(e)
        case p: PEntityPartType => method_with_string_multi_part(p)
        case p: PPowertypeType  => method_with_string_multi_powertype(p)
        case v: PByteType       => method_with_string_multi_byte
        case v: PIntegerType    => method_with_string_multi_integer
        case v: PDecimalType    => method_with_string_multi_decimal
        case _                  => method_with_string_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_with_string_single_entity(e)
        case p: PEntityPartType => method_with_string_single_part(p)
        case p: PPowertypeType  => method_with_string_single_powertype(p)
        case v: PByteType       => method_with_string_single_byte
        case v: PIntegerType    => method_with_string_single_integer
        case v: PDecimalType    => method_with_string_single_decimal
        case _                  => method_with_string_single_plain
      }
    }
    
  }

  override def method_with_string_multi_entity(e: PEntityType) {
    
  }

  override def method_with_string_multi_part(e: PEntityPartType) {
    
  }

  override def method_with_string_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_with_string_multi_byte {
    
  }

  override def method_with_string_multi_integer {
    
  }

  override def method_with_string_multi_decimal {
    
  }

  override def method_with_string_multi_plain {
    
  }

  override def method_with_string_single_entity(e: PEntityType) {
    
  }

  override def method_with_string_single_part(e: PEntityPartType) {
    
  }

  override def method_with_string_single_powertype(e: PPowertypeType) {
    
  }

  override def method_with_string_single_byte {
    
  }

  override def method_with_string_single_integer {
    
  }

  override def method_with_string_single_decimal {
    
  }

  override def method_with_string_single_plain {
    
  }

  // with_xml
  override def method_with_xml_multi_entity(e: PEntityType) {
    
  }

  override def method_with_xml_multi_part(e: PEntityPartType) {
    
  }

  override def method_with_xml_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_with_xml_multi_byte {
    
  }

  override def method_with_xml_multi_integer {
    
  }

  override def method_with_xml_multi_decimal {
    
  }

  override def method_with_xml_multi_plain {
    
  }

  override def method_with_xml_single_entity(e: PEntityType) {
    
  }

  override def method_with_xml_single_part(e: PEntityPartType) {
    
  }

  override def method_with_xml_single_powertype(e: PPowertypeType) {
    
  }

  override def method_with_xml_single_byte {
    
  }

  override def method_with_xml_single_integer {
    
  }

  override def method_with_xml_single_decimal {
    
  }

  override def method_with_xml_single_plain {
    
  }

  // with_json
  override def method_with_json_multi_entity(e: PEntityType) {
    
  }

  override def method_with_json_multi_part(e: PEntityPartType) {
    
  }

  override def method_with_json_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_with_json_multi_byte {
    
  }

  override def method_with_json_multi_integer {
    
  }

  override def method_with_json_multi_decimal {
    
  }

  override def method_with_json_multi_plain {
    
  }

  override def method_with_json_single_entity(e: PEntityType) {
    
  }

  override def method_with_json_single_part(e: PEntityPartType) {
    
  }

  override def method_with_json_single_powertype(e: PPowertypeType) {
    
  }

  override def method_with_json_single_byte {
    
  }

  override def method_with_json_single_integer {
    
  }

  override def method_with_json_single_decimal {
    
  }

  override def method_with_json_single_plain {
    
  }

  // with_csv
  override def method_with_csv_multi_entity(e: PEntityType) {
    
  }

  override def method_with_csv_multi_part(e: PEntityPartType) {
    
  }

  override def method_with_csv_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_with_csv_multi_byte {
    
  }

  override def method_with_csv_multi_integer {
    
  }

  override def method_with_csv_multi_decimal {
    
  }

  override def method_with_csv_multi_plain {
    
  }

  override def method_with_csv_single_entity(e: PEntityType) {
    
  }

  override def method_with_csv_single_part(e: PEntityPartType) {
    
  }

  override def method_with_csv_single_powertype(e: PPowertypeType) {
    
  }

  override def method_with_csv_single_byte {
    
  }

  override def method_with_csv_single_integer {
    
  }

  override def method_with_csv_single_decimal {
    
  }

  override def method_with_csv_single_plain {
    
  }

  // with_urlencode
  override def method_with_urlencode_multi_entity(e: PEntityType) {
    
  }

  override def method_with_urlencode_multi_part(e: PEntityPartType) {
    
  }

  override def method_with_urlencode_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_with_urlencode_multi_byte {
    
  }

  override def method_with_urlencode_multi_integer {
    
  }

  override def method_with_urlencode_multi_decimal {
    
  }

  override def method_with_urlencode_multi_plain {
    
  }

  override def method_with_urlencode_single_entity(e: PEntityType) {
    
  }

  override def method_with_urlencode_single_part(e: PEntityPartType) {
    
  }

  override def method_with_urlencode_single_powertype(e: PPowertypeType) {
    
  }

  override def method_with_urlencode_single_byte {
    
  }

  override def method_with_urlencode_single_integer {
    
  }

  override def method_with_urlencode_single_decimal {
    
  }

  override def method_with_urlencode_single_plain {
    
  }
  
  /*
   * macro_to
   */
  override def method_to_string_multi_entity(e: PEntityType) {
    
  }

  override def method_to_string_multi_part(e: PEntityPartType) {
    
  }

  override def method_to_string_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_to_string_multi_byte {
    
  }

  override def method_to_string_multi_integer {
    
  }

  override def method_to_string_multi_decimal {
    
  }

  override def method_to_string_multi_plain {
    
  }

  override def method_to_string_single_entity(e: PEntityType) {
    
  }

  override def method_to_string_single_part(e: PEntityPartType) {
    
  }

  override def method_to_string_single_powertype(e: PPowertypeType) {
    
  }

  override def method_to_string_single_byte {
    
  }

  override def method_to_string_single_integer {
    
  }

  override def method_to_string_single_decimal {
    
  }

  override def method_to_string_single_plain {
    
  }

  // to_xml
  override def method_to_xml_multi_entity(e: PEntityType) {
    
  }

  override def method_to_xml_multi_part(e: PEntityPartType) {
    
  }

  override def method_to_xml_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_to_xml_multi_byte {
    
  }

  override def method_to_xml_multi_integer {
    
  }

  override def method_to_xml_multi_decimal {
    
  }

  override def method_to_xml_multi_plain {
    
  }

  override def method_to_xml_single_entity(e: PEntityType) {
    
  }

  override def method_to_xml_single_part(e: PEntityPartType) {
    
  }

  override def method_to_xml_single_powertype(e: PPowertypeType) {
    
  }

  override def method_to_xml_single_byte {
    
  }

  override def method_to_xml_single_integer {
    
  }

  override def method_to_xml_single_decimal {
    
  }

  override def method_to_xml_single_plain {
    
  }

  // to_json
  override def method_to_json_multi_entity(e: PEntityType) {
    
  }

  override def method_to_json_multi_part(e: PEntityPartType) {
    
  }

  override def method_to_json_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_to_json_multi_byte {
    
  }

  override def method_to_json_multi_integer {
    
  }

  override def method_to_json_multi_decimal {
    
  }

  override def method_to_json_multi_plain {
    
  }

  override def method_to_json_single_entity(e: PEntityType) {
    
  }

  override def method_to_json_single_part(e: PEntityPartType) {
    
  }

  override def method_to_json_single_powertype(e: PPowertypeType) {
    
  }

  override def method_to_json_single_byte {
    
  }

  override def method_to_json_single_integer {
    
  }

  override def method_to_json_single_decimal {
    
  }

  override def method_to_json_single_plain {
    
  }

  // to_csv
  override def method_to_csv_multi_entity(e: PEntityType) {
    
  }

  override def method_to_csv_multi_part(e: PEntityPartType) {
    
  }

  override def method_to_csv_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_to_csv_multi_byte {
    
  }

  override def method_to_csv_multi_integer {
    
  }

  override def method_to_csv_multi_decimal {
    
  }

  override def method_to_csv_multi_plain {
    
  }

  override def method_to_csv_single_entity(e: PEntityType) {
    
  }

  override def method_to_csv_single_part(e: PEntityPartType) {
    
  }

  override def method_to_csv_single_powertype(e: PPowertypeType) {
    
  }

  override def method_to_csv_single_byte {
    
  }

  override def method_to_csv_single_integer {
    
  }

  override def method_to_csv_single_decimal {
    
  }

  override def method_to_csv_single_plain {
    
  }

  // to_urlencode
  override def method_to_urlencode_multi_entity(e: PEntityType) {
    
  }

  override def method_to_urlencode_multi_part(e: PEntityPartType) {
    
  }

  override def method_to_urlencode_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_to_urlencode_multi_byte {
    
  }

  override def method_to_urlencode_multi_integer {
    
  }

  override def method_to_urlencode_multi_decimal {
    
  }

  override def method_to_urlencode_multi_plain {
    
  }

  override def method_to_urlencode_single_entity(e: PEntityType) {
    
  }

  override def method_to_urlencode_single_part(e: PEntityPartType) {
    
  }

  override def method_to_urlencode_single_powertype(e: PPowertypeType) {
    
  }

  override def method_to_urlencode_single_byte {
    
  }

  override def method_to_urlencode_single_integer {
    
  }

  override def method_to_urlencode_single_decimal {
    
  }

  override def method_to_urlencode_single_plain {
    
  }

  /*
   * macro_from
   */
  override def method_from_string_multi_entity(e: PEntityType) {
    
  }

  override def method_from_string_multi_part(e: PEntityPartType) {
    
  }

  override def method_from_string_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_from_string_multi_byte {
    
  }

  override def method_from_string_multi_integer {
    
  }

  override def method_from_string_multi_decimal {
    
  }

  override def method_from_string_multi_plain {
    
  }

  override def method_from_string_single_entity(e: PEntityType) {
    
  }

  override def method_from_string_single_part(e: PEntityPartType) {
    
  }

  override def method_from_string_single_powertype(e: PPowertypeType) {
    
  }

  override def method_from_string_single_byte {
    
  }

  override def method_from_string_single_integer {
    
  }

  override def method_from_string_single_decimal {
    
  }

  override def method_from_string_single_plain {
    
  }

  // from_xml
  override def method_from_xml_multi_entity(e: PEntityType) {
    
  }

  override def method_from_xml_multi_part(e: PEntityPartType) {
    
  }

  override def method_from_xml_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_from_xml_multi_byte {
    
  }

  override def method_from_xml_multi_integer {
    
  }

  override def method_from_xml_multi_decimal {
    
  }

  override def method_from_xml_multi_plain {
    
  }

  override def method_from_xml_single_entity(e: PEntityType) {
    
  }

  override def method_from_xml_single_part(e: PEntityPartType) {
    
  }

  override def method_from_xml_single_powertype(e: PPowertypeType) {
    
  }

  override def method_from_xml_single_byte {
    
  }

  override def method_from_xml_single_integer {
    
  }

  override def method_from_xml_single_decimal {
    
  }

  override def method_from_xml_single_plain {
    
  }

  // from_json
  override def method_from_json_multi_entity(e: PEntityType) {
    
  }

  override def method_from_json_multi_part(e: PEntityPartType) {
    
  }

  override def method_from_json_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_from_json_multi_byte {
    
  }

  override def method_from_json_multi_integer {
    
  }

  override def method_from_json_multi_decimal {
    
  }

  override def method_from_json_multi_plain {
    
  }

  override def method_from_json_single_entity(e: PEntityType) {
    
  }

  override def method_from_json_single_part(e: PEntityPartType) {
    
  }

  override def method_from_json_single_powertype(e: PPowertypeType) {
    
  }

  override def method_from_json_single_byte {
    
  }

  override def method_from_json_single_integer {
    
  }

  override def method_from_json_single_decimal {
    
  }

  override def method_from_json_single_plain {
    
  }

  // from_csv
  override def method_from_csv_multi_entity(e: PEntityType) {
    
  }

  override def method_from_csv_multi_part(e: PEntityPartType) {
    
  }

  override def method_from_csv_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_from_csv_multi_byte {
    
  }

  override def method_from_csv_multi_integer {
    
  }

  override def method_from_csv_multi_decimal {
    
  }

  override def method_from_csv_multi_plain {
    
  }

  override def method_from_csv_single_entity(e: PEntityType) {
    
  }

  override def method_from_csv_single_part(e: PEntityPartType) {
    
  }

  override def method_from_csv_single_powertype(e: PPowertypeType) {
    
  }

  override def method_from_csv_single_byte {
    
  }

  override def method_from_csv_single_integer {
    
  }

  override def method_from_csv_single_decimal {
    
  }

  override def method_from_csv_single_plain {
    
  }

  // from_urlencode
  override def method_from_urlencode_multi_entity(e: PEntityType) {
    
  }

  override def method_from_urlencode_multi_part(e: PEntityPartType) {
    
  }

  override def method_from_urlencode_multi_powertype(e: PPowertypeType) {
    
  }

  override def method_from_urlencode_multi_byte {
    
  }

  override def method_from_urlencode_multi_integer {
    
  }

  override def method_from_urlencode_multi_decimal {
    
  }

  override def method_from_urlencode_multi_plain {
    
  }

  override def method_from_urlencode_single_entity(e: PEntityType) {
    
  }

  override def method_from_urlencode_single_part(e: PEntityPartType) {
    
  }

  override def method_from_urlencode_single_powertype(e: PPowertypeType) {
    
  }

  override def method_from_urlencode_single_byte {
    
  }

  override def method_from_urlencode_single_integer {
    
  }

  override def method_from_urlencode_single_decimal {
    
  }

  override def method_from_urlencode_single_plain {
    
  }

  /*
   * Document
   */
  override def method_document_plain() {
    method_document_common(true)
  }

  override def method_document_shallow() {
    method_document_common(false)
  }

  def method_document_common(isDeepCopy: Boolean) {
    val attrName = attr.name;
    val varName = var_name
    val persistentVarName = entity_ref_persistent_var_name
    val refVarName = entity_ref_assoc_var_name
    val partVarName = entity_ref_part_var_name
    val powerVarName = entity_ref_powertype_var_name
    val fromName = "this." + attrName
    val toName = "doc." + attrName

    def one {
      def update(converter: String) {
        jm_if_else_null(fromName) {
          jm_assign_null(toName)
        }
        jm_else {
          jm_assign(toName, converter.format(fromName)) 
        }
      }

      attr.attributeType match {
        case t: PDateTimeType => {
          jm_assign("doc." + varName, "this." + varName)
//          jm_assign("doc.%s_date".format(varName), "Util.makeDate(this.%s)", varName)
//          jm_assign("doc.%s_time".format(varName), "Util.makeTime(this.%s)", varName)
//          jm_assign("doc.%s_now".format(varName), "false")
        }
        case e: PEntityType => {
          if (isDeepCopy) {
            // XXX
          } else {
            // XXX
          }
          entityPersistentKind {
            new EntityAttributeKindFunction[Unit] {
              def apply_Simple() = method_document_make_single_entity_simple(e)
              def apply_Composition_Reference() = method_document_make_single_entity_composition_reference_property(e)
              def apply_Aggregation_Reference() = method_document_make_single_entity_aggregation_reference_property(e)
              def apply_Association_Reference() = method_document_make_single_entity_association_reference_property(e)
              def apply_Composition_Id() = method_document_make_single_entity_composition_id_property(e)
              def apply_Aggregation_Id() = method_document_make_single_entity_aggregation_id_property(e)
              def apply_Association_Id() = method_document_make_single_entity_association_id_property(e)
              def apply_Composition_Id_Reference() = method_document_make_single_entity_composition_id_reference_property(e)
              def apply_Aggregation_Id_Reference() = method_document_make_single_entity_aggregation_id_reference_property(e)
              def apply_Association_Id_Reference() = method_document_make_single_entity_association_id_reference_property(e)
              def apply_Query() = method_document_make_single_entity_query_property(e)
            }
          }
        }
        case p: PEntityPartType => {
          jm_pln("doc.%s = this.%s.make_document();", varName, refVarName)
        }
        case p: PPowertypeType => {
          jm_if_not_null("this." + varName) {
            jm_pln("doc.%s = this.%s.getKey()", varName, powerVarName)
          }
        }
        case v: PByteType    => update("%s.byteValue()")
        case v: PIntegerType => update("new BigInteger(%s)")
        case v: PDecimalType => update("new BigDecimal(%s)")
        case _ => jm_pln("doc.%s = this.%s;", varName, varName)
      }
    } // one

    def many {
      def update(converter: String) {
        jm_if_not_null(fromName) {
          jm_for(persistent_element_type(), "elem", fromName) {
            jm_pln("%s.add(%s);", toName, converter.format("elem")) 
          }
        }
      }

      attr.attributeType match {
        case t: PDateTimeType => { // XXX
          jm_pln("doc.%s = %s;", varName, varName)
//          jm_pln("doc.%s_date = Util.makeDate(%s);", varName, varName)
//          jm_pln("doc.%s_time = Util.makeTime(%s);", varName, varName)
//          jm_pln("doc.%s_now = false;")
        }
        case e: PEntityType => {
          if (isDeepCopy) {
            // XXX
          } else {
            // XXX
          }
          entityPersistentKind {
            new EntityAttributeKindFunction[Unit] {
              def apply_Simple() = method_document_make_multi_entity_simple(e)
              def apply_Composition_Reference() = method_document_make_multi_entity_composition_reference_property(e)
              def apply_Aggregation_Reference() = method_document_make_multi_entity_aggregation_reference_property(e)
              def apply_Association_Reference() = method_document_make_multi_entity_association_reference_property(e)
              def apply_Composition_Id() = method_document_make_multi_entity_composition_id_property(e)
              def apply_Aggregation_Id() = method_document_make_multi_entity_aggregation_id_property(e)
              def apply_Association_Id() = method_document_make_multi_entity_association_id_property(e)
              def apply_Composition_Id_Reference() = method_document_make_multi_entity_composition_id_reference_property(e)
              def apply_Aggregation_Id_Reference() = method_document_make_multi_entity_aggregation_id_reference_property(e)
              def apply_Association_Id_Reference() = method_document_make_multi_entity_association_id_reference_property(e)
              def apply_Query() = method_document_make_multi_entity_query_property(e)
            }
          }
        }
        case p: PEntityPartType => {
          jm_pln("fill_%s();", attrName)
          jm_if_not_null("this." + partVarName) {
            jm_for(java_element_type() + " element: " + partVarName) {
              jm_pln("doc.%s.add(element.make_document());", attrName)
            }
          }
        }
        case v: PByteType    => update("%s.byteValue()")
        case v: PIntegerType => update("new BigInteger(%s)")
        case v: PDecimalType => update("new BigDecimal(%s)")
        case _ => {
          jm_if_not_null("this." + varName) {
            jm_pln("doc.%s..addAll(this.%s);", varName, varName)  
          }
        }
      }
    }

    if (is_settable) {
      if (attr.isHasMany) {
        many
      } else {
        one
      }
    }
  }
/*
          
          def id {
            jm_assign("doc." + pContext.variableName4RefId(attr), "this." + persistentVarName) 
          }

          def get_id {
            jm_assign("doc." + pContext.variableName4RefId(attr), "this.%s.getId()", refVarName)
          }

          def deep {
            jm_pln("fill_%s();".format(attrName))
            jm_if_not_null(refVarName) {
              jm_assign("doc.%s".format(varName), "%s.make_document()", refVarName)
            }
          }

          def shallow {
            jm_pln("fill_%s();".format(attrName))
            jm_if_not_null(refVarName) {
              jm_assign("doc.%s".format(varName), "%s.make_document_shallow()", refVarName)
            }
          }

          if (is_owned_property()) {
            jm_pln("fill_%s();".format(attrName))
            jm_if_not_null(refVarName) {
              get_id
              jm_pln("doc.%s = %s.make_document();".format(varName, refVarName))
            }
          } else if (is_query_property()) {
            if (attr.isComposition) {
              deep
            } else if (attr.isAggregation) {
              if (isDeepCopy) {
                shallow
              }
            }
          } else {
            if (attr.isComposition) {
              jm_pln("fill_%s();".format(attrName))
              jm_if_not_null(refVarName) {
                get_id
                jm_pln("doc.%s = %s.make_document();".format(varName, refVarName))
              }
            } else if (attr.isAggregation) {
              id
              if (isDeepCopy) {
                shallow
              }
            } else {
              id
            }
          }
        }
 */
  def method_document_make_single_entity_simple(e: PEntityType) {
    jm_pln("doc.%s = this.%s.make_document();", varName, varName)
  }

  def method_document_make_single_entity_composition_reference_property(e: PEntityType) {
    error("not supported yet")
  }

  def method_document_make_single_entity_aggregation_reference_property(e: PEntityType) {
    error("not supported yet")
  }

  def method_document_make_single_entity_association_reference_property(e: PEntityType) {
    error("not supported yet")
  }

  def method_document_make_single_entity_composition_id_property(e: PEntityType) {
    error("not supported yet")
  }

  def method_document_make_single_entity_aggregation_id_property(e: PEntityType) {
    error("not supported yet")
  }

  def method_document_make_single_entity_association_id_property(e: PEntityType) {
    error("not supported yet")
  }

  def method_document_make_single_entity_composition_id_reference_property(entityType: PEntityType) {
    jm_for(persistent_element_type + " element: this." + erAssocVarName) {
      jm_pln("this.%s.add(element.get());", erPersistentVarName, entityType.entity.idName.capitalize) 
    } 
  }

  def method_document_make_single_entity_aggregation_id_reference_property(e: PEntityType) {
    error("not supported yet")
  }

  def method_document_make_single_entity_association_id_reference_property(e: PEntityType) {
    error("not supported yet")
  }

  def method_document_make_single_entity_query_property(e: PEntityType) {
    error("not supported yet")
  }
/*
          def id {
            jm_if_not_null("this." + persistentVarName) {
              jm_pln("doc.%s.addAll(this.%s);", pContext.variableName4RefId(attr), persistentVarName)
            }
          }

          def get_id {
            jm_if_not_null("this." + persistentVarName) {
              jm_pln("doc.%s.addAll(this.%s);", pContext.variableName4RefId(attr), persistentVarName) 
            }
          }

          def deep {
            jm_pln("fill_%s();".format(attrName))
            jm_for("%s entity: %s".format(e.entity.name, refVarName)) {
              jm_pln("doc.%s.add(entity.make_document());".format(varName))
            }
          }

          def shallow {
            jm_pln("fill_%s();".format(attrName))
            jm_for("%s entity: %s".format(e.entity.name, refVarName)) {
              jm_pln("doc.%s.add(entity.make_document_shallow());".format(varName))
            }
          }

          if (is_owned_property()) {
            jm_for("%s entity: get%s()".format(e.entity.name, attrName.capitalize)) {
              jm_pln("doc.%s.add(entity.make_document());".format(varName))
              jm_pln("doc.%s.add(entity.getId());".format(pContext.variableName4RefId(attr)))
            }
          } else if (is_query_property()) {
            if (attr.isComposition) {
              deep
            } else if (attr.isAggregation) {
              if (isDeepCopy) {
                shallow
              }
            }
          } else {
            if (attr.isComposition) {
              id
              deep
            } else if (attr.isAggregation) {
              id
              if (isDeepCopy) {
                shallow
              }
            } else {
              id
            }
          }
        }
 */
  def method_document_make_multi_entity_simple(e: PEntityType) {
  }

  def method_document_make_multi_entity_composition_reference_property(e: PEntityType) {
  }

  def method_document_make_multi_entity_aggregation_reference_property(e: PEntityType) {
  }

  def method_document_make_multi_entity_association_reference_property(e: PEntityType) {
  }

  def method_document_make_multi_entity_composition_id_property(e: PEntityType) {
  }

  def method_document_make_multi_entity_aggregation_id_property(e: PEntityType) {
  }

  def method_document_make_multi_entity_association_id_property(e: PEntityType) {
  }

  def method_document_make_multi_entity_composition_id_reference_property(entityType: PEntityType) {
    jm_for(persistent_element_type + " element: this." + erAssocVarName) {
      jm_pln("this.%s.add(element.get());", erPersistentVarName, entityType.entity.idName.capitalize) 
    } 
  }

  def method_document_make_multi_entity_aggregation_id_reference_property(e: PEntityType) {
  }

  def method_document_make_multi_entity_association_id_reference_property(e: PEntityType) {
  }

  def method_document_make_multi_entity_query_property(e: PEntityType) {
  }

  // update
  override def document_methods_update_attribute() {
    val fromName = "doc." + varName
    val toName = "this." + varName

    def update_attribute_one {
      def update(converter: String) {
        jm_if_else_null(fromName) {
          jm_assign_null(toName)
        }
        jm_else {
          jm_assign(toName, converter.format(fromName)) 
        }
      }

      attr.attributeType match { // sync PDomainServiceEntity
        case t: PDateTimeType => {
          jm_assign_this(varName, "doc." + varName)
//          jm_if("doc.%s != null || doc.%s_date != null || doc.%s_time != null || doc.%s_now", varName, varName, varName, varName) {
//            jm_assign_this(varName, "Util.makeDateTime(doc.%s, doc.%s_date, doc.%s_time, doc.%s_now)", varName, varName, varName, varName)
//          }
        }
        case e: PEntityType => {
          entityPersistentKind {
            new EntityAttributeKindFunction[Unit] {
              def apply_Simple() = method_document_update_multi_entity_simple(e)
              def apply_Composition_Reference() = method_document_update_multi_entity_composition_reference_property(e)
              def apply_Aggregation_Reference() = method_document_update_multi_entity_aggregation_reference_property(e)
              def apply_Association_Reference() = method_document_update_multi_entity_association_reference_property(e)
              def apply_Composition_Id() = method_document_update_multi_entity_composition_id_property(e)
              def apply_Aggregation_Id() = method_document_update_multi_entity_aggregation_id_property(e)
              def apply_Association_Id() = method_document_update_multi_entity_association_id_property(e)
              def apply_Composition_Id_Reference() = method_document_update_multi_entity_composition_id_reference_property(e)
              def apply_Aggregation_Id_Reference() = method_document_update_multi_entity_aggregation_id_reference_property(e)
              def apply_Association_Id_Reference() = method_document_update_multi_entity_association_id_reference_property(e)
              def apply_Query() = method_document_update_multi_entity_query_property(e)
            }
          }
/*          
          jm_if_not_null("doc." + pContext.variableName4RefId(attr)) {
            jm_assign_this(persistentVarName, "doc." + pContext.variableName4RefId(attr))
          }
*/
        }
        case p: PEntityPartType => {
        }
        case p: PPowertypeType => {
        }
        case v: PByteType    => update("%s.shortValue()")
        case v: PIntegerType => update("%s.toString()")
        case v: PDecimalType => update("%s.toString()")
        case _ => jm_assign_this(varName, "doc." + varName)
      }
    }

    def update_attribute_many {
      def update(converter: String) {
        jm_assign_new_ArrayList(toName, persistent_element_type())
        jm_for(attr.elementTypeName, "elem", fromName) {
          jm_pln("%s.add(%s);", toName, converter.format("elem")) 
        }
      }

      attr.attributeType match { // sync PDomainServiceEntity
        case t: PDateTimeType => { // XXX
          jm_if("doc.%s != null || doc.%s_date != null || doc.%s_time != null || doc.%s_now", varName, varName, varName, varName) {
            jm_assign_this(varName, "Util.makeDateTime(doc.%s, doc.%s_date, doc.%s_time, doc.%s_now)", varName, varName, varName, varName)
          }
        }
        case e: PEntityType => {
          entityPersistentKind {
            new EntityAttributeKindFunction[Unit] {
              def apply_Simple() = method_document_update_single_entity_simple(e)
              def apply_Composition_Reference() = method_document_update_single_entity_composition_reference_property(e)
              def apply_Aggregation_Reference() = method_document_update_single_entity_aggregation_reference_property(e)
              def apply_Association_Reference() = method_document_update_single_entity_association_reference_property(e)
              def apply_Composition_Id() = method_document_update_single_entity_composition_id_property(e)
              def apply_Aggregation_Id() = method_document_update_single_entity_aggregation_id_property(e)
              def apply_Association_Id() = method_document_update_single_entity_association_id_property(e)
              def apply_Composition_Id_Reference() = method_document_update_single_entity_composition_id_reference_property(e)
              def apply_Aggregation_Id_Reference() = method_document_update_single_entity_aggregation_id_reference_property(e)
              def apply_Association_Id_Reference() = method_document_update_single_entity_association_id_reference_property(e)
              def apply_Query() = method_document_update_single_entity_query_property(e)
            }
          }
/*
          // make_reference_key_unencoded_string
          if (is_owned_property()) {
            jm_pln("// TODO owned")
          } else if (is_query_property()) {
            jm_pln("// TODO query")
          } else {
            jm_pln("// plain")
            val idVarName = "this." + persistentVarName
            jm_assign_new_ArrayList(idVarName, e.entity.idAttr.elementTypeName, "doc." + pContext.variableName4RefId(attr))
          }
*/
        }
        case p: PEntityPartType => {
          jm_if_not_null("doc." + varName) {
            jm_assign_this_new_ArrayList(erPartVarName, java_element_type)
            jm_for(java_doc_element_type + " element: doc." + attrName) {
              jm_pln("this.%s.add(new %s(element, pm));", erPartVarName, java_element_type) 
            }
          }
        }
        case p: PPowertypeType => error("not supported yet")
        case v: PByteType      => update("%s.shortValue()")
        case v: PIntegerType   => update("%s.toString()")
        case v: PDecimalType   => update("%s.toString()")
        case _ => jm_assign_this_new_ArrayList(varName, persistent_element_type(), "doc." + varName)
      }
    }

    if (is_settable) {
      if (attr.isHasMany) {
        update_attribute_many
      } else {
        update_attribute_one
      }
    }
/*
    def update_owned_property_many {
      jm_pln("// TODO owned many")
    }

    def update_owned_property_one {
      jm_pln("// TODO owned one")
    }

    def update_query_property_many {
      jm_pln("// TODO owned many")
    }

    def update_query_property_one {
      jm_pln("// TODO owned one")
    }

    if (is_settable) {
      if (is_owned_property) {
        if (attr.isHasMany) {
          update_owned_property_many
        } else {
          update_owned_property_one
        }
      } else if (is_query_property) {
        if (attr.isHasMany) {
          update_query_property_many
        } else {
          update_query_property_one
        }
      } else {
        if (attr.isHasMany) {
          update_attribute_many
        } else {
          update_attribute_one
        }
      }
    }
*/
  } // update_attribute

  def method_document_update_single_entity_simple(e: PEntityType) {
  }

  def method_document_update_single_entity_composition_reference_property(e: PEntityType) {
  }

  def method_document_update_single_entity_aggregation_reference_property(e: PEntityType) {
  }

  def method_document_update_single_entity_association_reference_property(e: PEntityType) {
  }

  def method_document_update_single_entity_composition_id_property(e: PEntityType) {
  }

  def method_document_update_single_entity_aggregation_id_property(e: PEntityType) {
  }

  def method_document_update_single_entity_association_id_property(e: PEntityType) {
  }

  def method_document_update_single_entity_composition_id_reference_property(entityType: PEntityType) {
    jm_for(persistent_element_type + " element: this." + erAssocVarName) {
      jm_pln("this.%s.add(element.get());", erPersistentVarName, entityType.entity.idName.capitalize) 
    } 
  }

  def method_document_update_single_entity_aggregation_id_reference_property(e: PEntityType) {
  }

  def method_document_update_single_entity_association_id_reference_property(e: PEntityType) {
  }

  def method_document_update_single_entity_query_property(e: PEntityType) {
  }
  
  def method_document_update_multi_entity_simple(e: PEntityType) {
  }

  def method_document_update_multi_entity_composition_reference_property(e: PEntityType) {
  }

  def method_document_update_multi_entity_aggregation_reference_property(e: PEntityType) {
  }

  def method_document_update_multi_entity_association_reference_property(e: PEntityType) {
  }

  def method_document_update_multi_entity_composition_id_property(e: PEntityType) {
  }

  def method_document_update_multi_entity_aggregation_id_property(e: PEntityType) {
  }

  def method_document_update_multi_entity_association_id_property(e: PEntityType) {
  }

  def method_document_update_multi_entity_composition_id_reference_property(entityType: PEntityType) {
    jm_for(persistent_element_type + " element: this." + erAssocVarName) {
      jm_pln("this.%s.add(element.get());", erPersistentVarName, entityType.entity.idName.capitalize) 
    } 
  }

  def method_document_update_multi_entity_aggregation_id_reference_property(e: PEntityType) {
  }

  def method_document_update_multi_entity_association_id_reference_property(e: PEntityType) {
  }

  def method_document_update_multi_entity_query_property(e: PEntityType) {
  }
}
