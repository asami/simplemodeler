package org.simplemodeling.SimpleModeler.entities

/*
 * @since   Jul.  7, 2011
 *  version Aug. 27, 2011
 * @version Dec. 14, 2011
 * @author  ASAMI, Tomoharu
 */
class BuilderJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity,
  maker: JavaMaker
) extends JavaClassDefinition(pContext, aspects, pobject, maker) {
  useDocument = false
  isStatic = true
  customName = Some("Builder")
  isCustomVariableImplementation = true

  override protected def constructors_copy_constructor {
    jm_public_constructor("%s(%s src)", name, pobject.name) {
      for (a <- attributeDefinitions) {
        jm_assign_this(a.varName, "src." + a.varName)
      }
    }
  }

  override protected def constructors_plain_constructor {
    constructors_plain_constructor_for_document
  }

  override protected def attribute(attr: PAttribute) = {
    new BuilderJavaClassAttributeDefinition(pContext, aspects, attr, this, jm_maker)
  }

  override protected def service_methods {
    jm_public_method("%s with_json(JSONObject json) throws JSONException", name) {
      for (attr <- attributeDefinitions) {
        _with_json_attribute(attr)
      }
      jm_return_this
    }
    jm_public_method("%s build()", pobject.name) {
      jm_p("return new %s(", pobject.name)
      jm_indent_up
      if (!attributeDefinitions.isEmpty) {
        jm_pln
        jm_p(attributeDefinitions.head.varName)
      }
      for (a <- attributeDefinitions.tail) {
        jm_pln(",")
        jm_p(a.varName)
      }
      jm_pln(");")
      jm_indent_down
    }
  }

  private def _with_json_attribute(attrdef: GenericClassAttributeDefinition) {
    val attr = attrdef.attr
    val attrname = attrdef.attrName
    val propname = attrdef.propertyConstantName
    if (attr.isSingle) {
      _with_json_attribute(attrdef.attr.attributeType, attrname, propname)
    } else {
      // XXX
    }
  }

  private def _with_json_attribute(ptype: PObjectType,
      attrname: String, propname: String) {
    ptype {
      new PObjectTypeFunction[Unit] {
        override protected def apply_AnyURIType(datatype: PAnyURIType) {}
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

        override protected def apply_StringType(datatype: PStringType) = {
          jm_if("!json.isNull(%s)", propname) { 
            jm_assign_this(attrname, "json.getString(%s)", propname)
          }
        }

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

        override protected def apply_ValueType(datatype: PValueType) = {
          jm_if("!json.isNull(%s)", propname) { 
            datatype.value.contentType {
              new PObjectTypeFunction[Unit] {
                override protected def apply_LongType(datatype2: PLongType) = {
                  jm_assign_this(attrname, "new %s(json.getLong(%s))", datatype.name, propname)
                }
                override protected def apply_IntType(datatype2: PIntType) = {
                  jm_assign_this(attrname, "new %s(json.getInt(%s))", datatype.name, propname)
                }
                override protected def apply_StringType(datatype2: PStringType) = {
                  jm_assign_this(attrname, "new %s(json.getString(%s))", datatype.name, propname)
                }
              }
            }
          }
        }

        override protected def apply_DocumentType(datatype: PDocumentType) = "XDocument"
        override protected def apply_PowertypeType(datatype: PPowertypeType) = "XPower"
        override protected def apply_EntityType(datatype: PEntityType) = "XEntity"
        override protected def apply_EntityPartType(datatype: PEntityPartType) = "XEntityPart"
        override protected def apply_GenericType(datatype: PGenericType) = "XGeneric"
      }
    }
  }
}
