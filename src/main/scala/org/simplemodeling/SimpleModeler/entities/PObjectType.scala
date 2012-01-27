package org.simplemodeling.SimpleModeler.entities

import scala.collection.mutable
import scala.collection.mutable.{Buffer, ArrayBuffer}
import java.io._
import org.goldenport.util.Options
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entity.{SMConstraint, SMAttributeType, SMAssociation}

/*
 * @since   Apr. 22, 2011
 * @version Jul. 25, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class PObjectType(private val model_attribute_type: SMAttributeType) {
  def objectTypeName: String
  def getDatatypeName: Option[String] = None
  def xmlDatatypeName: String = "string"
  def jdoObjectTypeName: String = objectTypeName
  def getJdoDatatypeName: Option[String] = getDatatypeName
  val constraints = mutable.Map[String, PConstraint]()
  def isEntity = false
  def isDataType = getDatatypeName.isDefined

  if (model_attribute_type != null) {
    for (constraint <- model_attribute_type.constraints) {
      addConstraint(new PConstraint(constraint.name, constraint.value))
    }
  }

  def this() = this(null)

  def addConstraints(constraints: Seq[PConstraint]) {
    constraints.foreach(addConstraint)
  }

  def addConstraint(constraint: PConstraint) {
    this.constraints += constraint.name -> constraint
  }

  final def getLongMax(): Option[Long] = {
    constraints.get("max") match {
      case Some(c) => c.getLongValue match {
        case Some(v) => base_long_max_option match {
          case Some(b) => Some(Math.min(v, b))
          case None => Some(v)
        }
        case None => base_long_max_option
      }
      case None => base_long_max_option
    }
  }

  protected def base_long_max_option: Option[Long] = None

  final def getLongMin(): Option[Long] = {
    constraints.get("min") match {
      case Some(c) => c.getLongValue match {
        case Some(v) => base_long_min_option match {
          case Some(b) => Some(Math.max(v, b))
          case None => Some(v)
        }
        case None => base_long_min_option
      }
      case None => base_long_min_option
    }
  }

  protected def base_long_min_option: Option[Long] = None

  def apply[T](f: Function1[PObjectType, T]): T = {
    f(this)
  }
}

class PStringType(aModelAttrType: SMAttributeType) extends PObjectType {
  override def objectTypeName = "String"

  def this() = this(null)
}
object PStringType extends PStringType

class PByteStringType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "ShortBlob" // com.google.appengine.api.datastore.ShortBlob
  override def xmlDatatypeName = "base64Binary"

  def this() = this(null)
}
object PByteStringType extends PByteStringType

class PBooleanType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Boolean"
  override def getDatatypeName = Some("boolean")
  override def xmlDatatypeName = "boolean"

  def this() = this(null)
}
object PBooleanType extends PBooleanType

class PByteType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Byte"
  override def getDatatypeName = Some("byte")
  override def xmlDatatypeName = "byte"
  override def jdoObjectTypeName = "Short"

  override def base_long_max_option = Some(java.lang.Byte.MAX_VALUE)
  override def base_long_min_option = Some(java.lang.Byte.MIN_VALUE)

  def this() = this(null)
}
object PByteType extends PByteType

class PUnsignedByteType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Short"
  override def getDatatypeName = Some("short")
  override def xmlDatatypeName = "unsignedByte"

  override def base_long_max_option = Some(java.lang.Byte.MAX_VALUE * 2)
  override def base_long_min_option = Some(0)

  def this() = this(null)
}
object PUnsignedByteType extends PUnsignedByteType

class PShortType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Short"
  override def getDatatypeName = Some("short")
  override def xmlDatatypeName = "short"

  override def base_long_max_option = Some(java.lang.Short.MAX_VALUE)
  override def base_long_min_option = Some(java.lang.Short.MIN_VALUE)

  def this() = this(null)
}
object PShortType extends PShortType

class PUnsignedShortType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Integer"
  override def getDatatypeName = Some("int")
  override def xmlDatatypeName = "unsignedShort"

  override def base_long_max_option = Some(java.lang.Short.MAX_VALUE * 2)
  override def base_long_min_option = Some(0)

  def this() = this(null)
}
object PUnsignedShortType extends PUnsignedShortType

class PIntType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Integer"
  override def getDatatypeName = Some("int")
  override def xmlDatatypeName = "int"

  override def base_long_max_option = Some(java.lang.Integer.MAX_VALUE)
  override def base_long_min_option = Some(java.lang.Integer.MIN_VALUE)

  def this() = this(null)
}
object PIntType extends PIntType

class PUnsignedIntType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Long"
  override def getDatatypeName = Some("long")
  override def xmlDatatypeName = "unsignedInt"

  override def base_long_max_option = Some(java.lang.Integer.MAX_VALUE * 2)
  override def base_long_min_option = Some(0)

  def this() = this(null)
}
object PUnsignedIntType extends PUnsignedIntType

class PLongType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Long"
  override def getDatatypeName = Some("long")
  override def xmlDatatypeName = "long"

  override def base_long_max_option = Some(java.lang.Long.MAX_VALUE)
  override def base_long_min_option = Some(java.lang.Long.MIN_VALUE)

  def this() = this(null)
}
object PLongType extends PLongType

class PUnsignedLongType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "unsignedLong"

  def this() = this(null)
}
object PUnsignedLongType extends PUnsignedLongType

class PFloatType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Float"
  override def getDatatypeName = Some("float")
  override def xmlDatatypeName = "float"

  def this() = this(null)
}
object PFloatType extends PFloatType

class PDoubleType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Double"
  override def getDatatypeName = Some("double")
  override def xmlDatatypeName = "double"

  def this() = this(null)
}
object PDoubleType extends PDoubleType

class PIntegerType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "integer"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object PIntegerType extends PIntegerType

class PPositiveIntegerType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "positiveInteger"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object PPositiveIntegerType extends PPositiveIntegerType

class PNonPositiveIntegerType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "nonPositiveInteger"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object PNonPositiveIntegerType extends PNonPositiveIntegerType

class PNegativeIntegerType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "negativeInteger"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object PNegativeIntegerType extends PNegativeIntegerType

class PNonNegativeIntegerType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "nonNegativeInteger"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object PNonNegativeIntegerType extends PNonNegativeIntegerType

class PDecimalType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "BigDecimal"
  override def xmlDatatypeName = "decimal"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object PDecimalType extends PDecimalType

//
class PDurationType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Duration" // javax.xml.datatype.Duration
  override def xmlDatatypeName = "duration"

  def this() = this(null)
}
object PDurationType extends PDurationType

class PDateTimeType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Date" // java.util.Date
  override def xmlDatatypeName = "dateTime"

  def this() = this(null)
}
object PDateTimeType extends PDateTimeType

class PDateType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Date"
  override def xmlDatatypeName = "date"

  def this() = this(null)
}
object PDateType extends PDateType

class PTimeType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Date"
  override def xmlDatatypeName = "time"

  def this() = this(null)
}
object PTimeType extends PTimeType

class PGYearMonthType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gYearMonth"

  def this() = this(null)
}
object PGYearMonthType extends PGYearMonthType

class PGMonthDayType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gMonthDay"

  def this() = this(null)
}
object PGMonthDayType extends PGMonthDayType

class PGYearType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gYear"

  def this() = this(null)
}
object PGYearType extends PGYearType

class PGMonthType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gMonth"

  def this() = this(null)
}
object PGMonthType extends PGMonthType

class PGDayType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gDay"

  def this() = this(null)
}
object PGDayType extends PGDayType

class PAnyURIType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "URI"

  def this() = this(null)
}
object PAnyURIType extends PAnyURIType

class PLanguageType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Locale"

  def this() = this(null)
}
object PLanguageType extends PLanguageType

//
class PUserType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "User" // com.google.appengine.api.users.User

  def this() = this(null)
}
object PUserType extends PUserType

class PBlobType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Blob" // com.google.appengine.api.datastore.Blob
  override def xmlDatatypeName = "base64Binary"

  def this() = this(null)
}
object PBlobType extends PBlobType

class PTextType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Text" // com.google.appengine.api.datastore.Text

  def this() = this(null)
}
object PTextType extends PTextType

class PCategoryType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Category" // com.google.appengine.api.datastore.Category

  def this() = this(null)
}
object PCategoryType extends PCategoryType

class PLinkType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Link" // com.google.appengine.api.datastore.Link

  def this() = this(null)
}
object PLinkType extends PLinkType

class PEmailType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Email" // com.google.appengine.api.datastore.Email

  def this() = this(null)
}
object PEmailType extends PEmailType

class PGeoPtType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "GeoPt" // com.google.appengine.api.datastore.GeoPt

  def this() = this(null)
}
object PGeoPtType extends PGeoPtType

class PIMType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "IM" // com.google.appengine.api.datastore.IM

  def this() = this(null)
}
object PIMType extends PIMType

class PPhoneNumberType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "PhoneNumber" // com.google.appengine.api.datastore.PhoneNumber

  def this() = this(null)
}
object PPhoneNumberType extends PPhoneNumberType

class PPostalAddressType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "PostalAddress" // com.google.appengine.api.datastore.PostalAddress

  def this() = this(null)
}
object PPostalAddressType extends PPostalAddressType

class PRatingType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Rating" // com.google.appengine.api.datastore.Rating

  def this() = this(null)
}
object PRatingType extends PRatingType

// XXX Link
class PUrlType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Link" // com.google.appengine.api.datastore.Link

  def this() = this(null)
}
object PUrlType extends PUrlType

//
class PMoneyType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "Long"
  override def getDatatypeName = Some("long")

  def this() = this(null)
}
object PMoneyType extends PMoneyType

// ReferenceProperty
class PObjectReferenceType(val name: String, val packageName: String) extends PObjectType(null) {
  override def objectTypeName = name

  /**
   * Nullable in case of reference to an external object.
   * Use Case: Base class is an external object. (e.g. android.content.ContentProvider)
   */
  private var _reference: PObjectEntity = _
  def reference: PObjectEntity = {
    require(_reference != null)
    _reference
  }
  def reference_=(ref: PObjectEntity) = _reference = ref

  def referenceOption: Option[PObjectEntity] = Options.lift(_reference)

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

// Value
class PValueType(val name: String, val packageName: String) extends PObjectType(null) {
  def this(aValue: PValueEntity) = this(aValue.name, aValue.packageName)
  override def objectTypeName = name

  private var _value: PValueEntity = _
  def value: PValueEntity = {
    require(_value != null)
    _value
  }
  def value_=(aValue: PValueEntity) = _value = aValue

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

// Document
class PDocumentType(val name: String, val packageName: String) extends PObjectType(null) {
  def this(aDocument: PDocumentEntity) = this(aDocument.name, aDocument.packageName)
  override def objectTypeName = name

  private var _document: PDocumentEntity = _
  def document: PDocumentEntity = {
    require(_document != null)
    _document
  }
  def document_=(aDocument: PDocumentEntity) = _document = aDocument

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

// Powertype
class PPowertypeType(val name: String, val packageName: String) extends PObjectType(null) {
  def this(aPowertype: PPowertypeEntity) = this(aPowertype.name, aPowertype.packageName)
  override def objectTypeName = name

  private var _powertype: PPowertypeEntity = _
  def powertype: PPowertypeEntity = {
    require(_powertype != null)
    _powertype
  }
  def powertype_=(aPowertype: PPowertypeEntity) = _powertype = aPowertype

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

// Entity
class PEntityType(val name: String, val packageName: String) extends PObjectType(null) {
  def this(anEntity: PEntityEntity) = this(anEntity.name, anEntity.packageName)
  override def objectTypeName = name

  private var _entity: PEntityEntity = _
  def entity: PEntityEntity = {
    require(_entity != null, "PEntityType entity is unset")
    _entity
  }
  def entity_=(anEntity: PEntityEntity) = _entity = anEntity

/*
  var modelAssociation: SMAssociation = _

  def model_association_is(anAssoc: SMAssociation): PEntityType = {
    modelAssociation = anAssoc
    this
  }

  def isAggregation = {
    println("GaeEntityType assoc = " + modelAssociation)
    if (modelAssociation != null) modelAssociation.isAggregation
    else false
  }

  def isComposition =
    if (modelAssociation != null) modelAssociation.isComposition
    else false

  def isQueryReference =
    if (modelAssociation != null) modelAssociation.isQueryReference
    else false
*/

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
  override def isEntity = true
}

class PEntityPartType(val name: String, val packageName: String) extends PObjectType(null) {
  def this(aPart: PEntityPartEntity) = this(aPart.name, aPart.packageName)
  override def objectTypeName = name
  override def jdoObjectTypeName = "String"

  private var _part: PEntityPartEntity = _
  def part: PEntityPartEntity = {
    require(_part != null)
    _part
  }
  def part_=(aPart: PEntityPartEntity) = _part = aPart

  // XXX unify
  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

//
class PGenericType(aModelAttrType: SMAttributeType) extends PObjectType(aModelAttrType) {
  override def objectTypeName = "String"
}
