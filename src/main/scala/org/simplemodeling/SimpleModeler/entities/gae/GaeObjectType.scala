package org.simplemodeling.SimpleModeler.entities.gae

import scala.collection.mutable.{Buffer, ArrayBuffer}
import java.io._
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entity.SMAttributeType

/*
 * @since   Mar.  8, 2009
 * @version Oct. 26, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class GaeObjectType(val modelAttributeType: SMAttributeType) {
  def propertyClass: String
  def valueType: String
  def isEntity = false

  def this() = this(null)

  def addAnnotations(annotations: Buffer[String]) = {
    add_Annotations(annotations)
  }

  def addListAnnotations(annotations: Buffer[String]) = {
    annotations += ("item_type=" + valueType)
    add_ListAnnotations(annotations)
  }

  protected def add_Annotations(annotations: Buffer[String]) = {
  }

  protected def add_ListAnnotations(annotations: Buffer[String]) = {
  }
}

class GaeStringType extends GaeObjectType {
  def propertyClass = "StringProperty"
  def valueType = "unicode"

  override protected def add_Annotations(annotations: Buffer[String]) {
    annotations += "multiline=False"
  }
}
object GaeStringType extends GaeStringType

class GaeByteStringType extends GaeObjectType {
  def propertyClass = "ByteStringProperty"
  def valueType = "ByteString"
}
object GaeByteStringType extends GaeByteStringType

class GaeBooleanType extends GaeObjectType {
  def propertyClass = "BooleanProperty"
  def valueType = "bool"
}
object GaeBooleanType extends GaeBooleanType

class GaeIntegerType(aModelAttrType: SMAttributeType) extends GaeObjectType(aModelAttrType) {
  def propertyClass = "IntegerProperty"
  def valueType = "long"
}
object GaeIntegerType extends GaeIntegerType(null)

class GaeFloatType(aModelAttrType: SMAttributeType) extends GaeObjectType(aModelAttrType) {
  def propertyClass = "FloatProperty"
  def valueType = "float"
}
object GaeFloatType extends GaeFloatType(null)

class GaeDateTimeType extends GaeObjectType {
  def propertyClass = "DateTimeProperty"
  def valueType = "datetime.datetime"

  override protected def add_Annotations(annotations: Buffer[String]) {
    annotations += "auto_now_add=True"
  }
}
object GaeDateTimeType extends GaeDateTimeType

class GaeDateType extends GaeObjectType {
  def propertyClass = "DateProperty"
  def valueType = "datetime.datetime"
}
object GaeDateType extends GaeDateType

class GaeTimeType extends GaeObjectType {
  def propertyClass = "TimeProperty"
  def valueType = "datetime.datetime"
}
object GaeTimeType extends GaeTimeType

//
class GaeUserType extends GaeObjectType {
  def propertyClass = "UserProperty"
  def valueType = "users.User"
}
object GaeUserType extends GaeUserType

class GaeBlobType extends GaeObjectType {
  def propertyClass = "BlobProperty"
  def valueType = "db.Blob"
}
object GaeBlobType extends GaeBlobType

class GaeTextType extends GaeObjectType {
  def propertyClass = "TextProperty"
  def valueType = "db.Text"
}
object GaeTextType extends GaeTextType

class GaeCategoryType extends GaeObjectType {
  def propertyClass = "CategoryProperty"
  def valueType = "db.Category"
}
object GaeCategoryType extends GaeCategoryType

class GaeLinkType extends GaeObjectType {
  def propertyClass = "LinkProperty"
  def valueType = "db.Link"
}
object GaeLinkType extends GaeLinkType

class GaeEmailType extends GaeObjectType {
  def propertyClass = "EmailProperty"
  def valueType = "db.EMail"
}
object GaeEmailType extends GaeEmailType

class GaeGeoPtType extends GaeObjectType {
  def propertyClass = "GeoPtProperty"
  def valueType = "db.GeoPt"
}
object GaeGeoPtType extends GaeGeoPtType

class GaeIMType extends GaeObjectType {
  def propertyClass = "IMProperty"
  def valueType = "db.IM"
}
object GaeIMType extends GaeIMType

class GaePhoneNumberType extends GaeObjectType {
  def propertyClass = "PhoneNumberProperty"
  def valueType = "db.PhoneNumber"
}
object GaePhoneNumberType extends GaePhoneNumberType

class GaePostalAddressType extends GaeObjectType {
  def propertyClass = "PostalAddressProperty"
  def valueType = "db.PostalAddress"
}
object GaePostalAddressType extends GaePostalAddressType

class GaeRatingType extends GaeObjectType {
  def propertyClass = "RatingProperty"
  def valueType = "db.Rating"
}
object GaeRatingType extends GaeRatingType

// XXX Link
class GaeUrlType extends GaeObjectType {
  def propertyClass = "URLProperty"
  def valueType = "db.Link"
}

// Powertype
class GaePowertypeType(val name: String, val packageName: String) extends GaeObjectType {
  def this(anPowertype: GaePowertypeEntity) = this(anPowertype.name, anPowertype.packageName)
  def propertyClass = "ReferenceProperty" // XXX
  def valueType = "db.Key" // XXX

  private var _powertype: GaeObjectEntity = _
  def powertype: GaeObjectEntity = {
    require(_powertype != null)
    _powertype
  }
  def powertype_=(anPowertype: GaeObjectEntity) = _powertype = anPowertype

  override protected def add_Annotations(annotations: Buffer[String]) {
    annotations += "reference_class=" + name // XXX
  }

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
}

// Entity
class GaeEntityType(val name: String, val packageName: String) extends GaeObjectType {
  def this(anEntity: GaeObjectEntity) = this(anEntity.name, anEntity.packageName)
  def propertyClass = "ReferenceProperty"
  def valueType = "db.Key"

  private var _entity: GaeObjectEntity = _
  def entity: GaeObjectEntity = {
    require(_entity != null)
    _entity
  }
  def entity_=(anEntity: GaeObjectEntity) = _entity = anEntity

  override protected def add_Annotations(annotations: Buffer[String]) {
    annotations += "reference_class=" + name
  }

  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
  override def isEntity = true
}

class GaeGenericType(aModelAttrType: SMAttributeType) extends GaeObjectType(aModelAttrType) {
  def propertyClass = "StringProperty"
  def valueType = "unicode"
}
