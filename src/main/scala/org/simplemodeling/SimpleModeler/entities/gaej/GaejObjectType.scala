package org.simplemodeling.SimpleModeler.entities.gaej

import scala.collection.mutable
import scala.collection.mutable.{Buffer, ArrayBuffer}
import java.io._
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entity.{SMConstraint, SMAttributeType, SMAssociation}

/*
 * @since   Apr. 10, 2009
 * @version Nov.  9, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class GaejObjectType(private val model_attribute_type: SMAttributeType) {
  def objectTypeName: String
  def getDatatypeName: Option[String] = None
  def xmlDatatypeName: String = "string"
  def jdoObjectTypeName: String = objectTypeName
  def getJdoDatatypeName: Option[String] = getDatatypeName
  val constraints = mutable.Map[String, GaejConstraint]()
  def isEntity = false
  def isDataType = getDatatypeName.isDefined

  if (model_attribute_type != null) {
    for (constraint <- model_attribute_type.constraints) {
      addConstraint(new GaejConstraint(constraint.name, constraint.value))
    }
  }

  def this() = this(null)

  def addConstraints(constraints: Seq[GaejConstraint]) {
    constraints.foreach(addConstraint)
  }

  def addConstraint(constraint: GaejConstraint) {
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
}

class GaejStringType(aModelAttrType: SMAttributeType) extends GaejObjectType {
  override def objectTypeName = "String"

  def this() = this(null)
}
object GaejStringType extends GaejStringType

class GaejByteStringType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "ShortBlob" // com.google.appengine.api.datastore.ShortBlob
  override def xmlDatatypeName = "base64Binary"

  def this() = this(null)
}
object GaejByteStringType extends GaejByteStringType

class GaejBooleanType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Boolean"
  override def getDatatypeName = Some("boolean")
  override def xmlDatatypeName = "boolean"

  def this() = this(null)
}
object GaejBooleanType extends GaejBooleanType

class GaejByteType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Byte"
  override def getDatatypeName = Some("byte")
  override def xmlDatatypeName = "byte"
  override def jdoObjectTypeName = "Short"

  override def base_long_max_option = Some(java.lang.Byte.MAX_VALUE)
  override def base_long_min_option = Some(java.lang.Byte.MIN_VALUE)

  def this() = this(null)
}
object GaejByteType extends GaejByteType

class GaejUnsignedByteType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Short"
  override def getDatatypeName = Some("short")
  override def xmlDatatypeName = "unsignedByte"

  override def base_long_max_option = Some(java.lang.Byte.MAX_VALUE * 2)
  override def base_long_min_option = Some(0)

  def this() = this(null)
}
object GaejUnsignedByteType extends GaejUnsignedByteType

class GaejShortType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Short"
  override def getDatatypeName = Some("short")
  override def xmlDatatypeName = "short"

  override def base_long_max_option = Some(java.lang.Short.MAX_VALUE)
  override def base_long_min_option = Some(java.lang.Short.MIN_VALUE)

  def this() = this(null)
}
object GaejShortType extends GaejShortType

class GaejUnsignedShortType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Integer"
  override def getDatatypeName = Some("int")
  override def xmlDatatypeName = "unsignedShort"

  override def base_long_max_option = Some(java.lang.Short.MAX_VALUE * 2)
  override def base_long_min_option = Some(0)

  def this() = this(null)
}
object GaejUnsignedShortType extends GaejUnsignedShortType

class GaejIntType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Integer"
  override def getDatatypeName = Some("int")
  override def xmlDatatypeName = "int"

  override def base_long_max_option = Some(java.lang.Integer.MAX_VALUE)
  override def base_long_min_option = Some(java.lang.Integer.MIN_VALUE)

  def this() = this(null)
}
object GaejIntType extends GaejIntType

class GaejUnsignedIntType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Long"
  override def getDatatypeName = Some("long")
  override def xmlDatatypeName = "unsignedInt"

  override def base_long_max_option = Some(java.lang.Integer.MAX_VALUE * 2)
  override def base_long_min_option = Some(0)

  def this() = this(null)
}
object GaejUnsignedIntType extends GaejUnsignedIntType

class GaejLongType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Long"
  override def getDatatypeName = Some("long")
  override def xmlDatatypeName = "long"

  override def base_long_max_option = Some(java.lang.Long.MAX_VALUE)
  override def base_long_min_option = Some(java.lang.Long.MIN_VALUE)

  def this() = this(null)
}
object GaejLongType extends GaejLongType

class GaejUnsignedLongType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "unsignedLong"

  def this() = this(null)
}
object GaejUnsignedLongType extends GaejUnsignedLongType

class GaejFloatType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Float"
  override def getDatatypeName = Some("float")
  override def xmlDatatypeName = "float"

  def this() = this(null)
}
object GaejFloatType extends GaejFloatType

class GaejDoubleType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Double"
  override def getDatatypeName = Some("double")
  override def xmlDatatypeName = "double"

  def this() = this(null)
}
object GaejDoubleType extends GaejDoubleType

class GaejIntegerType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "integer"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object GaejIntegerType extends GaejIntegerType

class GaejPositiveIntegerType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "positiveInteger"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object GaejPositiveIntegerType extends GaejPositiveIntegerType

class GaejNonPositiveIntegerType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "nonPositiveInteger"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object GaejNonPositiveIntegerType extends GaejNonPositiveIntegerType

class GaejNegativeIntegerType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "negativeInteger"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object GaejNegativeIntegerType extends GaejNegativeIntegerType

class GaejNonNegativeIntegerType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "BigInteger"
  override def xmlDatatypeName = "nonNegativeInteger"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object GaejNonNegativeIntegerType extends GaejNonNegativeIntegerType

class GaejDecimalType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "BigDecimal"
  override def xmlDatatypeName = "decimal"
  override def jdoObjectTypeName = "String"

  def this() = this(null)
}
object GaejDecimalType extends GaejDecimalType

//
class GaejDurationType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Duration" // javax.xml.datatype.Duration
  override def xmlDatatypeName = "duration"

  def this() = this(null)
}
object GaejDurationType extends GaejDurationType

class GaejDateTimeType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Date" // java.util.Date
  override def xmlDatatypeName = "dateTime"

  def this() = this(null)
}
object GaejDateTimeType extends GaejDateTimeType

class GaejDateType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Date"
  override def xmlDatatypeName = "date"

  def this() = this(null)
}
object GaejDateType extends GaejDateType

class GaejTimeType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Date"
  override def xmlDatatypeName = "time"

  def this() = this(null)
}
object GaejTimeType extends GaejTimeType

class GaejGYearMonthType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gYearMonth"

  def this() = this(null)
}
object GaejGYearMonthType extends GaejGYearMonthType

class GaejGMonthDayType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gMonthDay"

  def this() = this(null)
}
object GaejGMonthDayType extends GaejGMonthDayType

class GaejGYearType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gYear"

  def this() = this(null)
}
object GaejGYearType extends GaejGYearType

class GaejGMonthType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gMonth"

  def this() = this(null)
}
object GaejGMonthType extends GaejGMonthType

class GaejGDayType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "XMLGregorianCalendar" // javax.xml.datatype.XMLGregorianCalendar
  override def xmlDatatypeName = "gDay"

  def this() = this(null)
}
object GaejGDayType extends GaejGDayType

class GaejAnyURIType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "URI"

  def this() = this(null)
}
object GaejAnyURIType extends GaejAnyURIType

class GaejLanguageType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Locale"

  def this() = this(null)
}
object GaejLanguageType extends GaejLanguageType

//
class GaejUserType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "User" // com.google.appengine.api.users.User

  def this() = this(null)
}
object GaejUserType extends GaejUserType

class GaejBlobType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Blob" // com.google.appengine.api.datastore.Blob
  override def xmlDatatypeName = "base64Binary"

  def this() = this(null)
}
object GaejBlobType extends GaejBlobType

class GaejTextType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Text" // com.google.appengine.api.datastore.Text

  def this() = this(null)
}
object GaejTextType extends GaejTextType

class GaejCategoryType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Category" // com.google.appengine.api.datastore.Category

  def this() = this(null)
}
object GaejCategoryType extends GaejCategoryType

class GaejLinkType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Link" // com.google.appengine.api.datastore.Link

  def this() = this(null)
}
object GaejLinkType extends GaejLinkType

class GaejEmailType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Email" // com.google.appengine.api.datastore.Email

  def this() = this(null)
}
object GaejEmailType extends GaejEmailType

class GaejGeoPtType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "GeoPt" // com.google.appengine.api.datastore.GeoPt

  def this() = this(null)
}
object GaejGeoPtType extends GaejGeoPtType

class GaejIMType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "IM" // com.google.appengine.api.datastore.IM

  def this() = this(null)
}
object GaejIMType extends GaejIMType

class GaejPhoneNumberType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "PhoneNumber" // com.google.appengine.api.datastore.PhoneNumber

  def this() = this(null)
}
object GaejPhoneNumberType extends GaejPhoneNumberType

class GaejPostalAddressType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "PostalAddress" // com.google.appengine.api.datastore.PostalAddress

  def this() = this(null)
}
object GaejPostalAddressType extends GaejPostalAddressType

class GaejRatingType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Rating" // com.google.appengine.api.datastore.Rating

  def this() = this(null)
}
object GaejRatingType extends GaejRatingType

// XXX Link
class GaejUrlType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Link" // com.google.appengine.api.datastore.Link

  def this() = this(null)
}
object GaejUrlType extends GaejUrlType

//
class GaejMoneyType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "Long"
  override def getDatatypeName = Some("long")

  def this() = this(null)
}
object GaejMoneyType extends GaejMoneyType

// ReferenceProperty
class GaejObjectReferenceType(val name: String, val packageName: String) extends GaejObjectType(null) {
  override def objectTypeName = name

  private var _reference: GaejObjectEntity = _
  def reference: GaejObjectEntity = {
    require(_reference != null)
    _reference
  }
  def reference_=(ref: GaejObjectEntity) = _reference = ref

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

// Document
class GaejDocumentType(val name: String, val packageName: String) extends GaejObjectType(null) {
  def this(aDocument: GaejDocumentEntity) = this(aDocument.name, aDocument.packageName)
  override def objectTypeName = name

  private var _document: GaejDocumentEntity = _
  def document: GaejDocumentEntity = {
    require(_document != null)
    _document
  }
  def document_=(aDocument: GaejDocumentEntity) = _document = aDocument

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

// Powertype
class GaejPowertypeType(val name: String, val packageName: String) extends GaejObjectType(null) {
  def this(aPowertype: GaejPowertypeEntity) = this(aPowertype.name, aPowertype.packageName)
  override def objectTypeName = name

  private var _powertype: GaejPowertypeEntity = _
  def powertype: GaejPowertypeEntity = {
    require(_powertype != null)
    _powertype
  }
  def powertype_=(aPowertype: GaejPowertypeEntity) = _powertype = aPowertype

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

// Entity
class GaejEntityType(val name: String, val packageName: String) extends GaejObjectType(null) {
  def this(anEntity: GaejEntityEntity) = this(anEntity.name, anEntity.packageName)
  override def objectTypeName = name

  private var _entity: GaejEntityEntity = _
  def entity: GaejEntityEntity = {
    require(_entity != null)
    _entity
  }
  def entity_=(anEntity: GaejEntityEntity) = _entity = anEntity

/*
  var modelAssociation: SMAssociation = _

  def model_association_is(anAssoc: SMAssociation): GaejEntityType = {
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

class GaejEntityPartType(val name: String, val packageName: String) extends GaejObjectType(null) {
  def this(aPart: GaejEntityPartEntity) = this(aPart.name, aPart.packageName)
  override def objectTypeName = name
  override def jdoObjectTypeName = "String"

  private var _part: GaejEntityPartEntity = _
  def part: GaejEntityPartEntity = {
    require(_part != null)
    _part
  }
  def part_=(aPart: GaejEntityPartEntity) = _part = aPart

  // XXX unify
  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

//
class GaejGenericType(aModelAttrType: SMAttributeType) extends GaejObjectType(aModelAttrType) {
  override def objectTypeName = "String"
}
