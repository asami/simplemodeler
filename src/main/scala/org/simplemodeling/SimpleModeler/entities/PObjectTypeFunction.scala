package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Jul. 23, 2011
 *  version Aug. 21, 2011
 *  version Nov. 16, 2012
 * @version Jan. 29, 2013
 * @author  ASAMI, Tomoharu
 */
trait PObjectTypeFunction[T] extends PartialFunction[PObjectType, T] {
  override def isDefinedAt(x: PObjectType) = true

  override def apply(objecttype: PObjectType): T = {
    objecttype match {
      case ot: PStringType => apply_StringType(ot)
      case ot: PTokenType => apply_TokenType(ot)
      case ot: PByteStringType => apply_ByteStringType(ot)
      case ot: PByteType => apply_ByteType(ot)
      case ot: PBooleanType => apply_BooleanType(ot)
      case ot: PUnsignedByteType => apply_UnsignedByteType(ot)
      case ot: PShortType => apply_ShortType(ot)
      case ot: PUnsignedShortType => apply_UnsignedShortType(ot)
      case ot: PIntType => apply_IntType(ot)
      case ot: PUnsignedIntType => apply_UnsignedIntType(ot)
      case ot: PLongType => apply_LongType(ot)
      case ot: PUnsignedLongType => apply_UnsignedLongType(ot)
      case ot: PFloatType => apply_FloatType(ot)
      case ot: PDoubleType => apply_DoubleType(ot)
      case ot: PIntegerType => apply_IntegerType(ot)
      case ot: PPositiveIntegerType => apply_PositiveIntegerType(ot)
      case ot: PNonPositiveIntegerType => apply_NonPositiveIntegerType(ot)
      case ot: PNegativeIntegerType => apply_NegativeIntegerType(ot)
      case ot: PNonNegativeIntegerType => apply_NonNegativeIntegerType(ot)
      case ot: PDecimalType => apply_DecimalType(ot)
      case ot: PDurationType => apply_DurationType(ot)
      case ot: PDateTimeType => apply_DateTimeType(ot)
      case ot: PDateType => apply_DateType(ot)
      case ot: PTimeType => apply_TimeType(ot)
      case ot: PGYearMonthType => apply_GYearMonthType(ot)
      case ot: PGMonthDayType => apply_GMonthDayType(ot)
      case ot: PGYearType => apply_GYearType(ot)
      case ot: PGMonthType => apply_GMonthType(ot)
      case ot: PGDayType => apply_GDayType(ot)
      case ot: PAnyURIType => apply_AnyURIType(ot)
      case ot: PLanguageType => apply_LanguageType(ot)
      case ot: PUserType => apply_UserType(ot)
      case ot: PBlobType => apply_BlobType(ot)
      case ot: PTextType => apply_TextType(ot)
      case ot: PCategoryType => apply_CategoryType(ot)
      case ot: PLinkType => apply_LinkType(ot)
      case ot: PEmailType => apply_EmailType(ot)
      case ot: PGeoPtType => apply_GeoPtType(ot)
      case ot: PIMType => apply_IMType(ot)
      case ot: PPhoneNumberType => apply_PhoneNumberType(ot)
      case ot: PPostalAddressType => apply_PostalAddressType(ot)
      case ot: PRatingType => apply_RatingType(ot)
      case ot: PUrlType => apply_UrlType(ot)
      case ot: PMoneyType => apply_MoneyType(ot)
      case ot: PPercentType => apply_PercentType(ot)
      case ot: PUnitType => apply_UnitType(ot)
      case ot: PUuidType => apply_UuidType(ot)
      case ot: PEverforthidType => apply_EverforthidType(ot)
      case ot: PXmlType => apply_XmlType(ot)
      case ot: PHtmlType => apply_HtmlType(ot)
      case ot: PObjectReferenceType => apply_ObjectReferenceType(ot)
      case ot: PValueType => apply_ValueType(ot)
      case ot: PDocumentType => apply_DocumentType(ot)
      case ot: PPowertypeType => apply_PowertypeType(ot)
      case ot: PStateMachineType => apply_StateMachineType(ot)
      case ot: PEntityType => apply_EntityType(ot)
      case ot: PEntityPartType => apply_EntityPartType(ot)
      case ot: PGenericType => apply_GenericType(ot)
    }
  }

  protected def apply_AnyURIType(datatype: PAnyURIType): T = {
    handle_xml(datatype)
  }

  protected def apply_ByteStringType(datatype: PByteStringType): T = {
    handle_xml(datatype)
  }

/*
  protected def apply_Base64BinaryType(datatype: PBase64BinaryType): T = {

  }
  
  protected def apply_HexBinaryType(datatype: PHexBinaryType): T = {

  }
*/

  protected def apply_BooleanType(datatype: PBooleanType): T = {
    handle_primitive(datatype)
  }

  protected def apply_ByteType(datatype: PByteType): T = {
    handle_primitive(datatype)
  }

  protected def apply_DateType(datatype: PDateType): T = {
    handle_xml(datatype)
  }

  protected def apply_DateTimeType(datatype: PDateTimeType): T = {
    handle_fundamental(datatype)
  }

  protected def apply_DecimalType(datatype: PDecimalType): T = {
    handle_fundamental(datatype)
  }

  protected def apply_DoubleType(datatype: PDoubleType): T = {
    handle_primitive(datatype)
  }

  protected def apply_DurationType(datatype: PDurationType): T = {
    handle_xml(datatype)
  }

  protected def apply_FloatType(datatype: PFloatType): T = {
    handle_primitive(datatype)
  }

  protected def apply_GDayType(datatype: PGDayType): T = {
    handle_xml(datatype)
  }

  protected def apply_GMonthType(datatype: PGMonthType): T = {
    handle_xml(datatype)
  }

  protected def apply_GMonthDayType(datatype: PGMonthDayType): T = {
    handle_xml(datatype)
  }

  protected def apply_GYearType(datatype: PGYearType): T = {
    handle_xml(datatype)
  }

  protected def apply_GYearMonthType(datatype: PGYearMonthType): T = {
    handle_xml(datatype)
  }

  protected def apply_IntType(datatype: PIntType): T = {
    handle_primitive(datatype)
  }

  protected def apply_IntegerType(datatype: PIntegerType): T = {
    handle_fundamental(datatype)
  }

  protected def apply_LanguageType(datatype: PLanguageType): T = {
    handle_xml(datatype)
  }

  protected def apply_LongType(datatype: PLongType): T = {
    handle_primitive(datatype)
  }

  protected def apply_NegativeIntegerType(datatype: PNegativeIntegerType): T = {
    handle_xml(datatype)
  }

  protected def apply_NonNegativeIntegerType(datatype: PNonNegativeIntegerType): T = {
    handle_xml(datatype)
  }

  protected def apply_NonPositiveIntegerType(datatype: PNonPositiveIntegerType): T = {
    handle_xml(datatype)
  }

  protected def apply_PositiveIntegerType(datatype: PPositiveIntegerType): T = {
    handle_xml(datatype)
  }

  protected def apply_ShortType(datatype: PShortType): T = {
    handle_primitive(datatype)
  }

  protected def apply_StringType(datatype: PStringType): T = {
    handle_fundamental(datatype)
  }

  protected def apply_TimeType(datatype: PTimeType): T = {
    handle_xml(datatype)
  }

  protected def apply_TokenType(datatype: PTokenType): T = {
    handle_xml(datatype)
  }

  protected def apply_UnsignedByteType(datatype: PUnsignedByteType): T = {
    handle_xml(datatype)
  }

  protected def apply_UnsignedIntType(datatype: PUnsignedIntType): T = {
    handle_xml(datatype)
  }

  protected def apply_UnsignedLongType(datatype: PUnsignedLongType): T = {
    handle_xml(datatype)
  }

  protected def apply_UnsignedShortType(datatype: PUnsignedShortType): T = {
    handle_xml(datatype)
  }

  protected def apply_CategoryType(datatype: PCategoryType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_EmailType(datatype: PEmailType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_GeoPtType(datatype: PGeoPtType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_IMType(datatype: PIMType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_LinkType(datatype: PLinkType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_PhoneNumberType(datatype: PPhoneNumberType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_PostalAddressType(datatype: PPostalAddressType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_RatingType(datatype: PRatingType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_TextType(datatype: PTextType): T = {
    handle_xml(datatype)
  }

  protected def apply_UserType(datatype: PUserType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_BlobType(datatype: PBlobType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_UrlType(datatype: PUrlType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_MoneyType(datatype: PMoneyType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_PercentType(datatype: PPercentType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_UnitType(datatype: PUnitType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_UuidType(datatype: PUuidType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_EverforthidType(datatype: PEverforthidType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_XmlType(datatype: PXmlType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_HtmlType(datatype: PHtmlType): T = {
    handle_auxiliary(datatype)
  }

  protected def apply_ObjectReferenceType(datatype: PObjectReferenceType): T = {
    handle_model(datatype)
  }

  protected def apply_ValueType(datatype: PValueType): T = {
    handle_model(datatype)
  }

  protected def apply_DocumentType(datatype: PDocumentType): T = {
    handle_model(datatype)
  }

  protected def apply_PowertypeType(datatype: PPowertypeType): T = {
    handle_model(datatype)
  }

  protected def apply_StateMachineType(datatype: PStateMachineType): T = {
    handle_model(datatype)
  }

  protected def apply_EntityType(datatype: PEntityType): T = {
    handle_model(datatype)
  }

  protected def apply_EntityPartType(datatype: PEntityPartType): T = {
    handle_model(datatype)
  }

  protected def apply_GenericType(datatype: PGenericType): T = {
    handle_model(datatype)
  }

  protected def handle_primitive(datatype: PObjectType): T = {
    handle_fundamental(datatype)
  }

  protected def handle_fundamental(datatype: PObjectType): T = {
    handle_xml(datatype)
  }

  protected def handle_xml(datatype: PObjectType): T = {
    handle_all(datatype)
  }

  protected def handle_auxiliary(datatype: PObjectType): T = {
    handle_all(datatype)
  }

  protected def handle_model(datatype: PObjectType): T = {
    handle_all(datatype)
  }

  protected def handle_all(datatype: PObjectType): T = {
    throw new UnsupportedOperationException("not implemented yet: " + datatype)
  }
}

/*
    attrtype(new PObjectTypeFunction[String] {
      override protected def apply_AnyURIType(datatype: PAnyURIType) = "XAnyURI"
      override protected def apply_ByteStringType(datatype: PByteStringType) = "XBase64Binary"
      override protected def apply_BooleanType(datatype: PBooleanType) = "XBoolean"
      override protected def apply_ByteType(datatype: PByteType) = "XByte"
      override protected def apply_DateType(datatype: PDateType) = "XDate"
      override protected def apply_DateTimeType(datatype: PDateTimeType) = "XDateTime"
      override protected def apply_DecimalType(datatype: PDecimalType) = "XDecimal"
      override protected def apply_DoubleType(datatype: PDoubleType) = "XDouble"
      override protected def apply_DurationType(datatype: PDurationType) = "XDuration"
      override protected def apply_FloatType(datatype: PFloatType) = "XFloat"
      override protected def apply_GDayType(datatype: PGDayType) = "XGDay"
      override protected def apply_GMonthType(datatype: PGMonthType) = "XGMonth"
      override protected def apply_GMonthDayType(datatype: PGMonthDayType) = "XGMonthDay"
      override protected def apply_GYearType(datatype: PGYearType) = "XGYear"
      override protected def apply_GYearMonthType(datatype: PGYearMonthType) = "XGYearMonth"
      override protected def apply_IntType(datatype: PIntType) = "XInt"
      override protected def apply_IntegerType(datatype: PIntegerType) = "XInteger"
      override protected def apply_LanguageType(datatype: PLanguageType) = "XLanguage"
      override protected def apply_LongType(datatype: PLongType) = "XLong"
      override protected def apply_NegativeIntegerType(datatype: PNegativeIntegerType) = "XNegativeInteger"
      override protected def apply_NonNegativeIntegerType(datatype: PNonNegativeIntegerType) = "XNonNegativeInteger"
      override protected def apply_NonPositiveIntegerType(datatype: PNonPositiveIntegerType) = "XNonPositiveInteger"
      override protected def apply_PositiveIntegerType(datatype: PPositiveIntegerType) = "XPositiveInteger"
      override protected def apply_ShortType(datatype: PShortType) = "XShort"
      override protected def apply_StringType(datatype: PStringType) = "XString"
      override protected def apply_TimeType(datatype: PTimeType) = "XTime"
      override protected def apply_UnsignedByteType(datatype: PUnsignedByteType) = "XUnsignedByte"
      override protected def apply_UnsignedIntType(datatype: PUnsignedIntType) = "XUnsignedInt"
      override protected def apply_UnsignedLongType(datatype: PUnsignedLongType) = "XUnsignedLong"
      override protected def apply_UnsignedShortType(datatype: PUnsignedShortType) = "XUnsignedShort"
      override protected def apply_CategoryType(datatype: PCategoryType) = "XCategory"
      override protected def apply_EmailType(datatype: PEmailType) = "XEmail"
      override protected def apply_GeoPtType(datatype: PGeoPtType) = "XGeoPt"
      override protected def apply_IMType(datatype: PIMType) = "XIM"
      override protected def apply_LinkType(datatype: PLinkType) = "XLink"
      override protected def apply_PhoneNumberType(datatype: PPhoneNumberType) = "XPhoneNumber"
      override protected def apply_PostalAddressType(datatype: PPostalAddressType) = "XPostalAddress"
      override protected def apply_RatingType(datatype: PRatingType) = "XRating"
      override protected def apply_TextType(datatype: PTextType) = "XText"
      override protected def apply_UserType(datatype: PUserType) = "XUser"
      override protected def apply_BlobType(datatype: PBlobType) = "XBlob"
      override protected def apply_UrlType(datatype: PUrlType) = "XUrl"
      override protected def apply_MoneyType(datatype: PMoneyType) = "XMoney"
      override protected def apply_ObjectReferenceType(datatype: PObjectReferenceType) = "XObjectReference"
      override protected def apply_ValueType(datatype: PValueType) = "XValue"
      override protected def apply_DocumentType(datatype: PDocumentType) = "XDocument"
      override protected def apply_PowertypeType(datatype: PPowertypeType) = "XPower"
      override protected def apply_EntityType(datatype: PEntityType) = "XEntity"
      override protected def apply_EntityPartType(datatype: PEntityPartType) = "XEntityPart"
      override protected def apply_GenericType(datatype: PGenericType) = "XGeneric"
    })
*/
