package org.simplemodeling.SimpleModeler.entities.extjs

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType

/**
 * @since   Apr.  4, 2012
 * @version May.  7, 2013
 * @author  ASAMI, Tomoharu
 */
abstract class ExtjsClassAttributeDefinition(
  val extjsContext: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  attr: PAttribute,
  owner: ExtjsClassDefinition,
  maker: ExtjsTextMaker) extends GenericClassAttributeDefinition(extjsContext, aspects, attr, owner) with ExtjsMakerHolder {
  type ATTR_DEF = ExtjsClassAttributeDefinition

  ej_open(maker, aspects)

  //
  protected val model_typename = new PObjectTypeFunction[String] {
    override protected def apply_AnyURIType(datatype: PAnyURIType): String = {
      handle_xml(datatype)
    }

    override protected def apply_ByteStringType(datatype: PByteStringType): String = {
      handle_xml(datatype)
    }

    override protected def apply_BooleanType(datatype: PBooleanType): String = {
      "boolean"
    }

    override protected def apply_ByteType(datatype: PByteType): String = {
      "int"
    }

    override protected def apply_DateType(datatype: PDateType): String = {
      "date"
    }

    override protected def apply_DateTimeType(datatype: PDateTimeType): String = {
      handle_fundamental(datatype)
    }

    override protected def apply_DecimalType(datatype: PDecimalType): String = {
      "number"
    }

    override protected def apply_DoubleType(datatype: PDoubleType): String = {
      "number"
    }

    override protected def apply_DurationType(datatype: PDurationType): String = {
      handle_xml(datatype)
    }

    override protected def apply_FloatType(datatype: PFloatType): String = {
      "float"
    }

    override protected def apply_GDayType(datatype: PGDayType): String = {
      handle_xml(datatype)
    }

    override protected def apply_GMonthType(datatype: PGMonthType): String = {
      handle_xml(datatype)
    }

    override protected def apply_GMonthDayType(datatype: PGMonthDayType): String = {
      handle_xml(datatype)
    }

    override protected def apply_GYearType(datatype: PGYearType): String = {
      handle_xml(datatype)
    }

    override protected def apply_GYearMonthType(datatype: PGYearMonthType): String = {
      handle_xml(datatype)
    }

    override protected def apply_IntType(datatype: PIntType): String = {
      "int"
    }

    override protected def apply_IntegerType(datatype: PIntegerType): String = {
      "integer"
    }

    override protected def apply_LanguageType(datatype: PLanguageType): String = {
      handle_xml(datatype)
    }

    override protected def apply_LongType(datatype: PLongType): String = {
      "number"
    }

    override protected def apply_NegativeIntegerType(datatype: PNegativeIntegerType): String = {
      handle_xml(datatype)
    }

    override protected def apply_NonNegativeIntegerType(datatype: PNonNegativeIntegerType): String = {
      handle_xml(datatype)
    }

    override protected def apply_NonPositiveIntegerType(datatype: PNonPositiveIntegerType): String = {
      handle_xml(datatype)
    }

    override protected def apply_PositiveIntegerType(datatype: PPositiveIntegerType): String = {
      handle_xml(datatype)
    }

    override protected def apply_ShortType(datatype: PShortType): String = {
      "int"
    }

    override protected def apply_StringType(datatype: PStringType): String = {
      "string"
    }

    override protected def apply_TimeType(datatype: PTimeType): String = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedByteType(datatype: PUnsignedByteType): String = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedIntType(datatype: PUnsignedIntType): String = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedLongType(datatype: PUnsignedLongType): String = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedShortType(datatype: PUnsignedShortType): String = {
      handle_xml(datatype)
    }

    override protected def apply_CategoryType(datatype: PCategoryType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_EmailType(datatype: PEmailType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_GeoPtType(datatype: PGeoPtType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_IMType(datatype: PIMType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_LinkType(datatype: PLinkType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_PhoneNumberType(datatype: PPhoneNumberType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_PostalAddressType(datatype: PPostalAddressType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_RatingType(datatype: PRatingType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_TextType(datatype: PTextType): String = {
      handle_xml(datatype)
    }

    override protected def apply_UserType(datatype: PUserType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_BlobType(datatype: PBlobType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_UrlType(datatype: PUrlType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_MoneyType(datatype: PMoneyType): String = {
      handle_auxiliary(datatype)
    }

    override protected def apply_ObjectReferenceType(datatype: PObjectReferenceType): String = {
      handle_model(datatype)
    }

    override protected def apply_ValueType(datatype: PValueType): String = {
      handle_model(datatype)
    }

    override protected def apply_DocumentType(datatype: PDocumentType): String = {
      handle_model(datatype)
    }

    override protected def apply_PowertypeType(datatype: PPowertypeType): String = {
      handle_model(datatype)
    }

    override protected def apply_EntityType(datatype: PEntityType): String = {
      handle_model(datatype)
    }

    override protected def apply_EntityPartType(datatype: PEntityPartType): String = {
      handle_model(datatype)
    }

    override protected def apply_GenericType(datatype: PGenericType): String = {
      handle_model(datatype)
    }

    override protected def handle_primitive(datatype: PObjectType): String = {
      handle_fundamental(datatype)
    }

    override protected def handle_fundamental(datatype: PObjectType): String = {
      handle_xml(datatype)
    }

    override protected def handle_xml(datatype: PObjectType): String = {
      handle_all(datatype)
    }

    override protected def handle_auxiliary(datatype: PObjectType): String = {
      handle_all(datatype)
    }

    override protected def handle_model(datatype: PObjectType): String = {
      handle_all(datatype)
    }

    override protected def handle_all(datatype: PObjectType): String = {
      "auto"
    }
  }

  protected val grid_column = new PObjectTypeFunction[List[(Symbol, Any)]] {
    override protected def apply_AnyURIType(datatype: PAnyURIType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_ByteStringType(datatype: PByteStringType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_BooleanType(datatype: PBooleanType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_ByteType(datatype: PByteType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_DateType(datatype: PDateType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_DateTimeType(datatype: PDateTimeType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_DecimalType(datatype: PDecimalType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_DoubleType(datatype: PDoubleType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_DurationType(datatype: PDurationType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_FloatType(datatype: PFloatType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_GDayType(datatype: PGDayType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_GMonthType(datatype: PGMonthType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_GMonthDayType(datatype: PGMonthDayType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_GYearType(datatype: PGYearType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_GYearMonthType(datatype: PGYearMonthType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_IntType(datatype: PIntType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_IntegerType(datatype: PIntegerType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_LanguageType(datatype: PLanguageType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_LongType(datatype: PLongType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_NegativeIntegerType(datatype: PNegativeIntegerType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_NonNegativeIntegerType(datatype: PNonNegativeIntegerType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_NonPositiveIntegerType(datatype: PNonPositiveIntegerType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_PositiveIntegerType(datatype: PPositiveIntegerType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_ShortType(datatype: PShortType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_StringType(datatype: PStringType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_TimeType(datatype: PTimeType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedByteType(datatype: PUnsignedByteType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedIntType(datatype: PUnsignedIntType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedLongType(datatype: PUnsignedLongType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedShortType(datatype: PUnsignedShortType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_CategoryType(datatype: PCategoryType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_EmailType(datatype: PEmailType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_GeoPtType(datatype: PGeoPtType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_IMType(datatype: PIMType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_LinkType(datatype: PLinkType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_PhoneNumberType(datatype: PPhoneNumberType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_PostalAddressType(datatype: PPostalAddressType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_RatingType(datatype: PRatingType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_TextType(datatype: PTextType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UserType(datatype: PUserType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_BlobType(datatype: PBlobType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_UrlType(datatype: PUrlType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_MoneyType(datatype: PMoneyType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_ObjectReferenceType(datatype: PObjectReferenceType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_ValueType(datatype: PValueType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_DocumentType(datatype: PDocumentType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_PowertypeType(datatype: PPowertypeType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_EntityType(datatype: PEntityType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_EntityPartType(datatype: PEntityPartType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_GenericType(datatype: PGenericType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def handle_primitive(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def handle_fundamental(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def handle_xml(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_all(datatype)
    }

    override protected def handle_auxiliary(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_all(datatype)
    }

    override protected def handle_model(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_all(datatype)
    }

    override protected def handle_all(datatype: PObjectType): List[(Symbol, Any)] = {
      List('xtype -> "gridcolumn",
           'text -> label_name,
           'dataIndex -> data_key)
    }
  }

  protected val form_item = new PObjectTypeFunction[List[(Symbol, Any)]] {
    override protected def apply_AnyURIType(datatype: PAnyURIType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_ByteStringType(datatype: PByteStringType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_BooleanType(datatype: PBooleanType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_ByteType(datatype: PByteType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_DateType(datatype: PDateType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_DateTimeType(datatype: PDateTimeType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_DecimalType(datatype: PDecimalType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_DoubleType(datatype: PDoubleType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_DurationType(datatype: PDurationType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_FloatType(datatype: PFloatType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_GDayType(datatype: PGDayType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_GMonthType(datatype: PGMonthType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_GMonthDayType(datatype: PGMonthDayType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_GYearType(datatype: PGYearType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_GYearMonthType(datatype: PGYearMonthType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_IntType(datatype: PIntType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_IntegerType(datatype: PIntegerType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_LanguageType(datatype: PLanguageType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_LongType(datatype: PLongType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_NegativeIntegerType(datatype: PNegativeIntegerType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_NonNegativeIntegerType(datatype: PNonNegativeIntegerType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_NonPositiveIntegerType(datatype: PNonPositiveIntegerType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_PositiveIntegerType(datatype: PPositiveIntegerType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_ShortType(datatype: PShortType): List[(Symbol, Any)] = {
      handle_primitive(datatype)
    }

    override protected def apply_StringType(datatype: PStringType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def apply_TimeType(datatype: PTimeType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedByteType(datatype: PUnsignedByteType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedIntType(datatype: PUnsignedIntType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedLongType(datatype: PUnsignedLongType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UnsignedShortType(datatype: PUnsignedShortType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_CategoryType(datatype: PCategoryType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_EmailType(datatype: PEmailType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_GeoPtType(datatype: PGeoPtType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_IMType(datatype: PIMType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_LinkType(datatype: PLinkType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_PhoneNumberType(datatype: PPhoneNumberType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_PostalAddressType(datatype: PPostalAddressType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_RatingType(datatype: PRatingType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_TextType(datatype: PTextType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def apply_UserType(datatype: PUserType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_BlobType(datatype: PBlobType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_UrlType(datatype: PUrlType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_MoneyType(datatype: PMoneyType): List[(Symbol, Any)] = {
      handle_auxiliary(datatype)
    }

    override protected def apply_ObjectReferenceType(datatype: PObjectReferenceType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_ValueType(datatype: PValueType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_DocumentType(datatype: PDocumentType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_PowertypeType(datatype: PPowertypeType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_EntityType(datatype: PEntityType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_EntityPartType(datatype: PEntityPartType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def apply_GenericType(datatype: PGenericType): List[(Symbol, Any)] = {
      handle_model(datatype)
    }

    override protected def handle_primitive(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_fundamental(datatype)
    }

    override protected def handle_fundamental(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_xml(datatype)
    }

    override protected def handle_xml(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_all(datatype)
    }

    override protected def handle_auxiliary(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_all(datatype)
    }

    override protected def handle_model(datatype: PObjectType): List[(Symbol, Any)] = {
      handle_all(datatype)
    }

    override protected def handle_all(datatype: PObjectType): List[(Symbol, Any)] = {
      List('xtype -> "textfield",
           'fieldLabel -> label_name)
    }
  }

  /*
   * 
   */
//  protected final def labelName = {
//    extjsContext.labelName(attr)
//  }

  /*
   *
   */
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

  override def method_bean_single_entity_Simple(e: PEntityType) {
  }

  override def method_bean_single_entity_Composition_Reference_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Aggregation_Reference_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Association_Reference_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Composition_Id_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Aggregation_Id_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Association_Id_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Composition_Id_Reference_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Aggregation_Id_Reference_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Association_Id_Reference_Property(e: PEntityType) {
  }

  override def method_bean_single_entity_Query_Property(e: PEntityType) {
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

  override def method_bean_multi_entity_Simple(e: PEntityType) {
  }

  override def method_bean_multi_entity_Composition_Reference_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Aggregation_Reference_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Association_Reference_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Composition_Id_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Aggregation_Id_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Association_Id_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Composition_Id_Reference_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Aggregation_Id_Reference_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Association_Id_Reference_Property(e: PEntityType) {
  }

  override def method_bean_multi_entity_Query_Property(e: PEntityType) {
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

  /*
   * Ext-JS
   */
  def extjsValidation: List[String] = {
    val a: Option[String] = attr.multiplicity match {
      case POne => Some("{field: '%s', type: 'presence'}".format(attr.name))
      case _ => None
    }
    val b: Seq[Option[String]] = if (attr.modelAttribute != null) {
      for (c <- attr.modelAttribute.constraints) yield { // XXX datatype constraints
        c.dslConstraint match {
          case x: CAssertFalse => None
          case x: CAssertTrue => None
          case x: CDecimalMax => None
          case x: CDecimalMin => None
          case x: CDigits => None
          case x: CFuture => None
          case x: CMax => 
            Some("{field: '%s', type: 'length'}".format(attr.name))
          case x: CMin =>
            Some("{field: '%s', type: 'length'}".format(attr.name))
          case x: CNotNull => None
          case x: CNull => None
          case x: CPast => None
          case x: CPattern => 
            Some("{field: '%s', type: 'format'}".format(attr.name))
          case x: CSize => None
          case x: CInclusion =>
            Some("{field: '%s', type: 'inclusion'}".format(attr.name))
          case x: CExclusion => 
            Some("{field: '%s', type: 'exclusion'}".format(attr.name))
        }
      }
    } else Nil
    val c = a :: b.toList
    c.flatten
  }
}

class NullExtjsClassAttributeDefinition(
  pContext: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  attr: PAttribute,
  owner: ExtjsClassDefinition,
  maker: ExtjsTextMaker) extends ExtjsClassAttributeDefinition(pContext, aspects, attr, owner, maker)
