package org.simplemodeling.SimpleModeler.entities.sql

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * @since   May.  3, 2012
 * @version May.  6, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SqlClassAttributeDefinition(
  val sqlContext: SqlEntityContext,
  aspects: Seq[SqlAspect],
  attr: PAttribute,
  owner: SqlClassDefinition,
  maker: JavaMaker) extends GenericClassAttributeDefinition(sqlContext, aspects, attr, owner) with JavaMakerHolder {
  type ATTR_DEF = SqlClassAttributeDefinition

  jm_open(maker, Nil)

  protected def pln() {
    jm_pln()
  }

  //
  protected val sql_typename = new PObjectTypeFunction[String] {
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

  override protected def variable_id_Id(typeName: String, varName: String): Boolean = {
    _column
    true
  }

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    _column
  }

  protected def variable_plain_Transient_Instance_Variable(typename: String, varname: String) {
    _column
  }

  private def _column {
    val typename = attr.attributeType(sql_typename)
    jm_p(sqlContext.sqlName(attr))
    jm_p(" ")
    if (attr.multiplicity == POne) {
      jm_p("NOT NULL ")
    }
    jm_p(typename)
  }
}

/*
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isAdmin boolean NOT NULL,
    PRIMARY KEY (id)
);
*/

class NullSqlClassAttributeDefinition(
  pContext: SqlEntityContext,
  aspects: Seq[SqlAspect],
  attr: PAttribute,
  owner: SqlClassDefinition,
  maker: JavaMaker) extends SqlClassAttributeDefinition(pContext, aspects, attr, owner, maker)
