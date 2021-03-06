package org.simplemodeling.SimpleModeler.entities.g3

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.dsl.datatype.XInt
import org.simplemodeling.dsl.SDatatype
import org.simplemodeling.dsl.SDatatypeFunction
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/**
 * @since   Aug. 15, 2011
 *  version Sep. 25, 2011
 *  version May.  1, 2012
 * @version Jun. 10, 2012
 * @author  ASAMI, Tomoharu
 */
class G3ApplicationScalaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  pobject: PObjectEntity
) extends ScalaClassDefinition(pContext, aspects, pobject) {
  customBaseName = Some("G3Application")
  customImplementNames = List("UseRecord")

  override protected def head_imports_Extension {
    super.head_imports_Extension
    sm_import("org.goldenport.g3._")
    sm_import("org.goldenport.g3.messages._")
  }

  override protected def constructors_null_constructor = null
  override protected def constructors_copy_constructor = null

  override protected def package_methods_Extension {
    _package_port
    traverse_entities_in_module(_entity_schema)
    _datastore
  }

  private def _package_port {
    sm_pln("""port("/%s") invoke('ds)""", asciiName)
  }

  private def _schema_name(entity: PEntityEntity) = {
    "schema_%s".format(entity.asciiName)
  }

  private def _entity_schema(entity: PEntityEntity) {
    sm_pln("val %s = Schema(", _schema_name(entity))
    sm_indent_up
    val columns = entity.attributes.map(_column_def)
    val last = columns.last
    for (a <- columns) {
      sm_p(a)
      if (a eq last) {
        sm_pln(")")
      } else {
        sm_pln(",")
      }
    }
    sm_indent_down
  }

  private def _column_def(attr: PAttribute): String = {
    if (attr.isId) {
      "AutoIdField"
    } else if (attr.multiplicity == POne) {
      if (attr.constraints.isEmpty) {
        "('%s, %s)".format(attr.name, _datatype(attr.attributeType))
      } else {
        "('%s, %s, %s)".format(attr.name, _datatype(attr.attributeType), 
            _constraints(attr.constraints))
      }
    } else {
      if (attr.constraints.isEmpty) {
        "('%s, %s, %s)".format(attr.name, _datatype(attr.attributeType),
            _multiplicity(attr.multiplicity))
      } else {
        "('%s, %s, %s, %s)".format(attr.name, _datatype(attr.attributeType),
            _constraints(attr.constraints), _multiplicity(attr.multiplicity))
      }
    }
  }

  private def _datatype(attrtype: PObjectType): String = {
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

      override protected def apply_ValueType(datatype: PValueType) = {
        _datatype(datatype.value.contentType)
      }

      override protected def apply_DocumentType(datatype: PDocumentType) = "XDocument"
      override protected def apply_PowertypeType(datatype: PPowertypeType) = "XPower"

      override protected def apply_EntityType(datatype: PEntityType) = {
        "XEntityReference('%s)".format(datatype.entity.asciiName)
        //_datatype(entity.entity.idAttr.attributeType)
      }

      override protected def apply_EntityPartType(datatype: PEntityPartType) = "XEntityPart"
      override protected def apply_GenericType(datatype: PGenericType) = "XGeneric"
    })
  }

  private def _constraints(c: Map[String, PConstraint]) = {
    c.toString
  }

  private def _multiplicity(m: PMultiplicity) = {
    m.toString
  }

  private def _datastore {
    sm_pln("""datastore('ds, RecordClassSpace(""")
    sm_indent_up
    val classes = map_entities_in_module(e =>
      "RecordClass('%s, %s)".format(e.asciiName, _schema_name(e)))
    val last = classes.last
    sm_indent_up
    for (c <- classes) {
      sm_p(c)
      if (c eq last) {
        sm_pln("))")
      } else {
        sm_pln(",")
      }
    }
    sm_indent_down
    sm_indent_down
  }

/*
  override protected def object_auxiliary {
    sm_code("""

""", Map("%context%" -> contextName, "%application%" -> name, "%module%" -> moduleName))
  }
*/
}
