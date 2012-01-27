package org.simplemodeling.SimpleModeler.entities.gae

import scala.collection.mutable.{Buffer, ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Mar.  8, 2009
 * @version Oct. 26, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeAttribute(val name: String, val attributeType: GaeObjectType) {
  var isId = false
  var multiplicity: GaeMultiplicity = GaeOne
  var modelAttribute: SMAttribute = null
  var modelAssociation: SMAssociation = null
  var modelPowertype: SMPowertypeRelationship = null

  final def typeLiteral: String = {
//    if (isId) {
//      return "Key(encoded=None)"
//    }
    val buf = new StringBuilder
    buf.append(type_name)
    type_name match {
      case "StringListProperty" => {
        buf.append("()")
      }
      case _ => {
        buf.append("(")
        buf.append(make_annotations)
        buf.append(")")
      }
    }
    buf.toString
  }

  private def type_name = {
    def content_type_name = {
      attributeType.propertyClass
    }

    def list_type_name = {
      if (attributeType.isInstanceOf[GaeStringType]) {
	"StringListProperty"
      } else {
	"ListProperty"
      }
    }

    multiplicity match {
      case m: GaeOne => content_type_name
      case m: GaeZeroOne => content_type_name
      case m: GaeZeroMore => list_type_name
      case m: GaeOneMore => list_type_name
    }
  }

  private def make_annotations = {
    val annotations = new ArrayBuffer[String]
    multiplicity match {
      case m: GaeOne => {
        annotations += "required=True"
        attributeType.addAnnotations(annotations)
      }
      case m: GaeZeroOne => {
        annotations += "required=False"
        attributeType.addAnnotations(annotations)
      }
      case m: GaeZeroMore => {
        attributeType.addListAnnotations(annotations)
      }
      case m: GaeOneMore => {
        attributeType.addListAnnotations(annotations)
      }
    }
    annotations.mkString(",")
  }

  //
  final def isHasMany: Boolean = {
    multiplicity match {
      case m: GaeOne => false
      case m: GaeZeroOne => false
      case m: GaeZeroMore => true
      case m: GaeOneMore => true
      case _ => error("???")
    }
  }

  final def isOptional = {
    multiplicity == GaeZeroOne
  }

  final def isSingle = {
    multiplicity match {
      case m: GaeOne => true
      case m: GaeZeroOne => true
      case m: GaeZeroMore => false
      case m: GaeOneMore => false
      case _ => error("???")
    }
  }

  final def isEntity = {
    attributeType.isEntity
  }

/*
  //
  final def isString = {
    attributeType.isInstanceOf[GaeDateTimeType]
  }

  final def isByteString = {
    attributeType.isInstanceOf[GaeDateTimeType]
  }

  final def isBoolean = {
    attributeType.isInstanceOf[GaeDateTimeType]
  }

  final def isInteger = {
    attributeType.isInstanceOf[GaeIntegerType]
  }

  final def isFloat = {
    attributeType.isInstanceOf[GaeFloatType]
  }

  final def isDateTime = {
    attributeType.isInstanceOf[GaeDateTimeType]
  }

  final def isDate = {
    attributeType.isInstanceOf[GaeDateType]
  }

  final def isTime = {
    attributeType.isInstanceOf[GaeTimeType]
  }

  //
  final def isReferenceProperty = {
    attributeType.isInstanceOf[GaeEntityType]
  }

  //
  final def isUser = {
    attributeType.isInstanceOf[GaeUserType]
  }

  final def isBlob = {
    attributeType.isInstanceOf[GaeBlobType]
  }

  final def isText = {
    attributeType.isInstanceOf[GaeTextType]
  }

  final def isCategory = {
    attributeType.isInstanceOf[GaeCategoryType]
  }

  final def isLink = {
    attributeType.isInstanceOf[GaeLinkType]
  }

  final def isEmail = {
    attributeType.isInstanceOf[GaeEmailType]
  }

  final def isGeoPt = {
    attributeType.isInstanceOf[GaeGeoPtType]
  }

  final def isIM = {
    attributeType.isInstanceOf[GaeIMType]
  }

  final def isPhoneNumber = {
    attributeType.isInstanceOf[GaePhoneNumberType]
  }

  final def isPostalAddress = {
    attributeType.isInstanceOf[GaePostalAddressType]
  }

  final def isRating = {
    attributeType.isInstanceOf[GaePostalAddressType]
  }
*/
}

abstract class GaeMultiplicity
class GaeOne extends GaeMultiplicity
object GaeOne extends GaeOne
class GaeZeroOne extends GaeMultiplicity
object GaeZeroOne extends GaeZeroOne
class GaeOneMore extends GaeMultiplicity
object GaeOneMore extends GaeOneMore
class GaeZeroMore extends GaeMultiplicity
object GaeZeroMore extends GaeZeroMore
class GaeRange extends GaeMultiplicity
