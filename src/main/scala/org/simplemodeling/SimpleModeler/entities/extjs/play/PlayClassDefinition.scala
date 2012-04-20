package org.simplemodeling.SimpleModeler.entities.extjs.play

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 21, 2012
 * @version Apr. 21, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class PlayClassDefinition(
  context: PEntityContext,
  pobject: PObjectEntity
) extends GenericClassDefinition(context, pobject) with ScalaMakerHolder {
  type ATTR_DEF = PlayClassAttributeDefinition

  sm_open(Nil)

  override def toText = {
    sm_to_text
  }

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new NullPlayClassAttributeDefinition(pContext, attr, this, sm_maker)
  }

  override protected def pln() {
    sm_pln()
  }
}

abstract class PlayClassAttributeDefinition(
  pContext: PEntityContext,
  attr: PAttribute,
  owner: PlayClassDefinition,
  maker: ScalaMaker) extends GenericClassAttributeDefinition(pContext, Nil, attr, owner) with ScalaMakerHolder {
  type ATTR_DEF = PlayClassAttributeDefinition

  sm_open(maker)

  override protected def head_imports_Extension {
  }

  override def constant_property {
  }

  override protected def variable_plain_Inject_Annotation {
  }

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
  }

  override protected def variable_plain_Transient_Instance_Variable(typename: String, varname: String) {
  }

  override protected def method_bean_single_plain() {
  }

  override protected def method_bean_single_byte() {
  }

  override protected def method_bean_single_integer() {
  }    

  override protected def method_bean_single_decimal() {
  }

  def method_bean_single_entity_Simple(e: PEntityType) {
  }

  def method_bean_single_entity_Composition_Reference_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Aggregation_Reference_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Association_Reference_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Composition_Id_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Aggregation_Id_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Association_Id_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Composition_Id_Reference_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Aggregation_Id_Reference_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Association_Id_Reference_Property(e: PEntityType) {
  }

  def method_bean_single_entity_Query_Property(e: PEntityType) {
  }

  // Part
  override protected def method_bean_single_part(p: PEntityPartType) {
  }

  override protected def method_bean_single_powertype(e: PPowertypeType) {
  }

  override protected def method_bean_multi_plain() {
  }

  override protected def method_bean_multi_byte() {
  }

  override protected def method_bean_multi_integer() {
  }    

  override protected def method_bean_multi_decimal() {
  }

  def method_bean_multi_entity_Simple(e: PEntityType) {
  }

  def method_bean_multi_entity_Composition_Reference_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Aggregation_Reference_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Association_Reference_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Composition_Id_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Aggregation_Id_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Association_Id_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Composition_Id_Reference_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Aggregation_Id_Reference_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Association_Id_Reference_Property(e: PEntityType) {
  }

  def method_bean_multi_entity_Query_Property(e: PEntityType) {
  }

  // part
  override protected def method_bean_multi_part(p: PEntityPartType) {
  }

  override protected def method_bean_multi_powertype(e: PPowertypeType) {
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
  }

  override def method_with_plain_single_entity(e: PEntityType) {
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
  }

  override def method_with_string {
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
  }

  override def method_document_shallow() {
  }

  // update
  override def document_methods_update_attribute() {
  } // update_attribute
}

class NullPlayClassAttributeDefinition(
  pContext: PEntityContext,
  attr: PAttribute,
  owner: PlayClassDefinition,
  maker: ScalaMaker) extends PlayClassAttributeDefinition(pContext, attr, owner, maker)
