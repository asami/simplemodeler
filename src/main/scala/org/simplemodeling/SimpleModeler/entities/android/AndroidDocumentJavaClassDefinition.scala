package org.simplemodeling.SimpleModeler.entities.android

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.dsl.datatype.XInt
import org.simplemodeling.dsl.SDatatype
import org.simplemodeling.dsl.SDatatypeFunction
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * @since   Jul. 23, 2011
 *  version Oct. 25, 2011
 * @version Apr.  8, 2012
 * @author  ASAMI, Tomoharu
 */
class AndroidDocumentJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends DocumentJavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  customImplementNames = List("IGADocument") // Parcelable

  override protected def head_imports_Extension {
    jm_import("android.content.ContentValues")
    jm_import("android.os.Bundle")
    jm_import("android.os.Parcel")
    jm_import("android.os.Parcelable")
    jm_import("org.goldenport.android.feed.*")
  }

  override protected def attribute_variables_extension {
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_auxiliary_constructors {
    def in(attr: PAttribute) {
      new PObjectTypeFunction[Unit] {
        override def apply_StringType(dt: PStringType) {
          jm_assign_this(attr.name, "in.readString()")
        }
        override def apply_ByteType(dt: PByteType) {
          jm_assign_this(attr.name, "in.readByte()")
        }
        override def apply_ShortType(dt: PShortType) {
          jm_assign_this(attr.name, "in.readShort()")
        }
        override def apply_IntType(dt: PIntType) {
          jm_assign_this(attr.name, "in.readInt()")
        }
        override def apply_LongType(dt: PLongType) {
          jm_assign_this(attr.name, "in.readLong()")
        }
        override def apply_FloatType(dt: PFloatType) {
          jm_assign_this(attr.name, "in.readFloat()")
        }
        override def apply_DoubleType(dt: PDoubleType) {
          jm_assign_this(attr.name, "in.readDouble()")
        }
        override def apply_DateTimeType(dt: PDateTimeType) {
          jm_assign_this_null_or_object(attr.name, "-1")("long", "in.readLong()")("new Date(%s)")
        }
        override def apply_ValueType(ot: PValueType) {
          ot.value.contentType {
            new PObjectTypeFunction[Unit] {
              override def apply_StringType(dt: PStringType) {
//                jm_assign_this(attr.name, "new %s(in.readString())", ot.value.name)
                jm_assign_this_null_or_object(attr.name)("String", "in.readString()")("new %s(%%s)".format(ot.value.name))
              }
            }
          }
        }
        override def apply_PowertypeType(ot: PPowertypeType) {
          jm_assign_this(attr.name, "in.readString()")
        }
        override def apply_EntityType(ot: PEntityType) {
          jm_assign_this(attr.name, "(%s)in.readParcelable(cl)", pContext.entityDocumentName(ot.entity))
        }
      }.apply(attr.attributeType)
    }

    def out(attr: PAttribute) {
      new PObjectTypeFunction[Unit] {
        override def apply_StringType(dt: PStringType) {
          jm_pln("dest.writeString(%s);", attr.name)
        }
        override def apply_ByteType(dt: PByteType) {
          jm_pln("dest.writeByte(%s);", attr.name)
        }
        override def apply_ShortType(dt: PShortType) {
          jm_pln("dest.writeShort(%s);", attr.name)
        }
        override def apply_IntType(dt: PIntType) {
          jm_pln("dest.writeInt(%s);", attr.name)
        }
        override def apply_LongType(dt: PLongType) {
          jm_pln("dest.writeLong(%s);", attr.name)
        }
        override def apply_FloatType(dt: PFloatType) {
          jm_pln("dest.writeFloat(%s);", attr.name)
        }
        override def apply_DoubleType(dt: PDoubleType) {
          jm_pln("dest.writeDouble(%s);", attr.name)
        }
        override def apply_DateTimeType(dt: PDateTimeType) {
          jm_if_else_null(attr.name) {
            jm_pln("dest.writeLong(-1);")
          }
          jm_else {
            jm_pln("dest.writeLong(%s.getTime());", attr.name)
          }
        }
        override def apply_ValueType(ot: PValueType) {
          ot.value.contentType {
            new PObjectTypeFunction[Unit] {
              override def apply_StringType(dt: PStringType) {
                jm_if_else_null(attr.name) {
                  jm_pln("dest.writeString(null);", attr.name)
                }
                jm_else {
                  jm_pln("dest.writeString(%s.value);", attr.name)
                }
              }
            }
          }
        }
        override def apply_PowertypeType(ot: PPowertypeType) {
          jm_pln("dest.writeString(%s);", attr.name)
        }
        override def apply_EntityType(ot: PEntityType) {
          jm_pln("dest.writeParcelable(%s, flags);", attr.name)
        }
      }.apply(attr.attributeType)
    }

    jm_public_constructor("%s(Parcel in)", name) {
      jm_var("ClassLoader", "cl", "getClass().getClassLoader()")
      for (a <- attributeDefinitions) {
        in(a.attr)
      }
    }
    jm_override_public_method("int describeContents()") {
      jm_return("0")
    }
    jm_override_public_method("void writeToParcel(Parcel dest, int flags)") {
      for (a <- attributeDefinitions) {
        out(a.attr)
      }
    }
    jm_code("""
public static final Parcelable.Creator<%classname%> CREATOR = new Parcelable.Creator<%classname%>() {
    public %classname% createFromParcel(Parcel in) {
        return new %classname%(in);
    }

    public %classname%[] newArray(int size) {
        return new %classname%[size];
    }
};
""", Map("%classname%" -> name))
  }

  override protected def to_methods_feed {
    val builder = "GADocumentFeedBuilder<%s>".format(name)
    jm_public_method("GADocumentFeed<%s> toFeed()", name) {
      jm_var_new(builder, "builder")
      jm_pln("builder.withDocuments(this);")
      jm_return("builder.build()");
    }
  }

  override protected def to_methods_entry {
    val builder = "GADocumentEntryBuilder<%s>".format(name)
    jm_public_method("GADocumentEntry<%s> toEntry()", name) {
      jm_var_new(builder, "builder")
      jm_pln("builder.withDocument(this);")
      jm_return("builder.build()");
    }
  }

  private def _bundle_converter(a: GenericClassAttributeDefinition): PObjectType => Unit = {
    new PObjectTypeFunction[Unit] {
      override protected def apply_AnyURIType(datatype: PAnyURIType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ByteStringType(datatype: PByteStringType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_BooleanType(datatype: PBooleanType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ByteType(datatype: PByteType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_DateType(datatype: PDateType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_DateTimeType(datatype: PDateTimeType) {
        jm_pln("bundle.putSerializable(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_DecimalType(datatype: PDecimalType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_DoubleType(datatype: PDoubleType) {
        jm_pln("bundle.putDouble(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_DurationType(datatype: PDurationType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_FloatType(datatype: PFloatType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GDayType(datatype: PGDayType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GMonthType(datatype: PGMonthType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GMonthDayType(datatype: PGMonthDayType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GYearType(datatype: PGYearType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GYearMonthType(datatype: PGYearMonthType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_IntType(datatype: PIntType) {
        jm_pln("bundle.putInt(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_IntegerType(datatype: PIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_LanguageType(datatype: PLanguageType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_LongType(datatype: PLongType) {
        jm_pln("bundle.putLong(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_NegativeIntegerType(datatype: PNegativeIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_NonNegativeIntegerType(datatype: PNonNegativeIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_NonPositiveIntegerType(datatype: PNonPositiveIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PositiveIntegerType(datatype: PPositiveIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ShortType(datatype: PShortType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_StringType(datatype: PStringType) {
        jm_pln("bundle.putString(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_TimeType(datatype: PTimeType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedByteType(datatype: PUnsignedByteType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedIntType(datatype: PUnsignedIntType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedLongType(datatype: PUnsignedLongType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedShortType(datatype: PUnsignedShortType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_CategoryType(datatype: PCategoryType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_EmailType(datatype: PEmailType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GeoPtType(datatype: PGeoPtType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_IMType(datatype: PIMType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_LinkType(datatype: PLinkType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PhoneNumberType(datatype: PPhoneNumberType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PostalAddressType(datatype: PPostalAddressType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_RatingType(datatype: PRatingType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_TextType(datatype: PTextType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UserType(datatype: PUserType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_BlobType(datatype: PBlobType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UrlType(datatype: PUrlType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_MoneyType(datatype: PMoneyType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ObjectReferenceType(datatype: PObjectReferenceType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ValueType(datatype: PValueType) {
        datatype.value.contentType(_bundle_value_converter(a))
      }

      override protected def apply_DocumentType(datatype: PDocumentType) {
        jm_pln("bundle.putParcelable(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_PowertypeType(datatype: PPowertypeType) {
        jm_pln("bundle.putParcelable(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_EntityType(datatype: PEntityType) {
        jm_pln("bundle.putParcelable(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_EntityPartType(datatype: PEntityPartType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GenericType(datatype: PGenericType) {
        throw new UnsupportedOperationException("not implemented yet")
      }
    }
  }

  private def _bundle_value_converter(a: GenericClassAttributeDefinition): PObjectType => Unit = {
    new PObjectTypeFunction[Unit] {
      override protected def apply_AnyURIType(datatype: PAnyURIType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ByteStringType(datatype: PByteStringType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_BooleanType(datatype: PBooleanType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ByteType(datatype: PByteType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_DateType(datatype: PDateType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_DateTimeType(datatype: PDateTimeType) {
        jm_pln("bundle.putSerializable(%s, %s.value);", a.propertyConstantName, a.varName)
      }

      override protected def apply_DecimalType(datatype: PDecimalType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_DoubleType(datatype: PDoubleType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_DurationType(datatype: PDurationType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_FloatType(datatype: PFloatType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GDayType(datatype: PGDayType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GMonthType(datatype: PGMonthType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GMonthDayType(datatype: PGMonthDayType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GYearType(datatype: PGYearType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GYearMonthType(datatype: PGYearMonthType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_IntType(datatype: PIntType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_IntegerType(datatype: PIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_LanguageType(datatype: PLanguageType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_LongType(datatype: PLongType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_NegativeIntegerType(datatype: PNegativeIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_NonNegativeIntegerType(datatype: PNonNegativeIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_NonPositiveIntegerType(datatype: PNonPositiveIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PositiveIntegerType(datatype: PPositiveIntegerType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ShortType(datatype: PShortType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_StringType(datatype: PStringType) {
        jm_pln("bundle.putString(%s, %s.value);", a.propertyConstantName, a.varName)
      }

      override protected def apply_TimeType(datatype: PTimeType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedByteType(datatype: PUnsignedByteType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedIntType(datatype: PUnsignedIntType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedLongType(datatype: PUnsignedLongType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UnsignedShortType(datatype: PUnsignedShortType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_CategoryType(datatype: PCategoryType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_EmailType(datatype: PEmailType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GeoPtType(datatype: PGeoPtType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_IMType(datatype: PIMType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_LinkType(datatype: PLinkType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PhoneNumberType(datatype: PPhoneNumberType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_PostalAddressType(datatype: PPostalAddressType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_RatingType(datatype: PRatingType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_TextType(datatype: PTextType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UserType(datatype: PUserType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_BlobType(datatype: PBlobType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_UrlType(datatype: PUrlType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_MoneyType(datatype: PMoneyType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ObjectReferenceType(datatype: PObjectReferenceType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_ValueType(datatype: PValueType) {
        datatype.value.contentType(_bundle_converter(a))
      }

      override protected def apply_DocumentType(datatype: PDocumentType) {
        jm_pln("bundle.putParcelable(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_PowertypeType(datatype: PPowertypeType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_EntityType(datatype: PEntityType) {
        jm_pln("bundle.putParcelable(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def apply_EntityPartType(datatype: PEntityPartType) {
        throw new UnsupportedOperationException("not implemented yet")
      }

      override protected def apply_GenericType(datatype: PGenericType) {
        throw new UnsupportedOperationException("not implemented yet")
      }
    }
  }

  private def _contentvalues_converter(a: GenericClassAttributeDefinition): PObjectType => Unit = {
    new PObjectTypeFunction[Unit] {
      override protected def apply_DateTimeType(datatype: PDateTimeType) {
        jm_pln("values.put(%s, to_string(%s));", a.propertyConstantName, a.varName)
      }

      override protected def apply_ValueType(datatype: PValueType) {
        jm_pln("values.put(%s, %s.value);", a.propertyConstantName, a.varName)
      }

      override protected def apply_PowertypeType(datatype: PPowertypeType) {
        jm_pln("values.put(%s, %s.value);", a.propertyConstantName, a.varName)
      }

      override protected def apply_EntityType(datatype: PEntityType) {
        val access = datatype.entity.idAttr.attributeType match {
          case t: PValueType => "get%s().value".format(datatype.entity.idName.capitalize)
          case _ => "get%s()".format(datatype.entity.idName.capitalize)
        }
         jm_pln("values.put(%s, %s.%s);", a.propertyConstantName, a.varName, access)
      }

      override protected def handle_primitive(datatype: PObjectType) {
        jm_pln("values.put(%s, %s);", a.propertyConstantName, a.varName)
      }

      override protected def handle_fundamental(datatype: PObjectType) {
        jm_pln("values.put(%s, %s);", a.propertyConstantName, a.varName)
      }
    }
  }

  override protected def to_methods_Extension {
    jm_public_method("Bundle toBundle()") {
      jm_var_new("Bundle", "bundle")
      for (a <- attributeDefinitions) {
        a.attr.attributeType(_bundle_converter(a))
      }
      jm_return("bundle")
    }
    jm_public_method("ContentValues toContentValues()") {
      jm_var_new("ContentValues", "values")
      for (a <- attributeDefinitions) {
        a.attr.attributeType(_contentvalues_converter(a))
      }
      jm_return("values")
    }
    jm_public_method("ContentValues toContentValuesWithoutId()") {
      jm_var_new("ContentValues", "values")
      for (a <- attributeDefinitions if !a.attr.isId) {
        a.attr.attributeType(_contentvalues_converter(a))
      }
      jm_return("values")
    }
    jm_public_method("String get_entry_id(String id)") {
      jm_if_return_expr("id != null")("id")
      jm_return_null;
    }
    jm_public_method("String get_entry_title(String title)") {
      jm_if_return_expr("title != null")("title")
      entry_attr("title", "name") match {
        case Some(attr) => _prop_return(attr)
        case None => jm_return_null
      }
    }
    jm_public_method("String get_entry_summary(String summary)") {
      jm_if_return_expr("summary != null")("summary")
      entry_attr("summary", "subtitle") match {
        case Some(attr) => _prop_return(attr)
        case None => jm_return_null
      }
    }
  }

  protected def entry_attr(names: String*): Option[GenericClassAttributeDefinition] = {
//    attributeDefinitions.find(attr => { println(names + "," + attr.attrName);names.contains(attr.attrName) } )
    attributeDefinitions.find(attr => { names.contains(attr.attrName) } )
  }

  private def _prop_return(attr: GenericClassAttributeDefinition) {
    jm_return(_property_to_string_expr(attr.attrName, attr.attr.attributeType))
  }

  private def _property_to_string_expr(propname: String, ptype: PObjectType) = {
    "this.%s == null ? null : this.%s".format(propname, _to_string_expr(propname, ptype))
  }

  private def _to_string_expr(propname: String, ptype: PObjectType): String = {
    val tostring = propname + ".toString()" 
    val primitivetostring = "_to_string(%s)".format(propname)
    ptype {
      new PObjectTypeFunction[String] {
        override protected def apply_AnyURIType(datatype: PAnyURIType) = tostring
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
        override protected def apply_StringType(datatype: PStringType) = propname
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

        override protected def apply_ValueType(datatype: PValueType): String = {
          def valuetostring = propname + ".value.toString()" 
          def valueprimitivetostring = "_to_string(%s.value)".format(propname)
          def plainvalue = "%s.value".format(propname)
          datatype.value.contentType {
            new PObjectTypeFunction[String] {
              override protected def apply_LongType(datatype2: PLongType) = valueprimitivetostring
              override protected def apply_IntType(datatype2: PIntType) = valueprimitivetostring
              override protected def apply_StringType(datatype2: PStringType) = plainvalue
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

/*
    @Override
    public String get_entry_id(String id) {
        if (id != null) {
            return id;
        }
        return buyId.value;
    }

    @Override
    public String get_entry_title(String title) {
        if (title != null) {
            return title;
        }
        return null;
    }

    @Override
    public String get_entry_summary(String summary) {
        if (summary != null) {
            return summary;
        }
        return null;
    }
 */

  /*
   * builder
   */
  override protected def builder_class {
    val builder = new AndroidBuilderJavaClassDefinition(pContext, Nil, pobject, jm_maker)
    builder.build
  }
}
