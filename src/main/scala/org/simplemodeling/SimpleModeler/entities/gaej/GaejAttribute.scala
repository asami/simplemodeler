package org.simplemodeling.SimpleModeler.entities.gaej

import scala.collection.mutable.{Buffer, ArrayBuffer}
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity._

/**
 * GaejAttribute represents SMAttribute, SMAssociation,
 * and SMPowertypeRelationship
 *
 * @since   Apr. 10, 2009
 * @version Nov. 12, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejAttribute(val name: String, val attributeType: GaejObjectType) {
  var isId = false
  var multiplicity: GaejMultiplicity = GaejOne
  var modelAttribute: SMAttribute = null
  var modelAssociation: SMAssociation = null // XXX
  var modelPowertype: SMPowertypeRelationship = null // XXX

  final def typeName: String = type_name
  final def objectTypeName: String = type_name_object
  final def elementTypeName: String = element_type_name
  final def jdoTypeName: String = jdo_type_name
  final def jdoElementTypeName: String = jdo_element_type_name
  final def xmlDatatypeName: String = attributeType.xmlDatatypeName
  final def kind: SAttributeKind = {
    if (modelAttribute != null) modelAttribute.kind
    else NullAttributeKind
  }

  val use_object_over_datatype = true

  def idPolicy = {
    require (isId)
    modelAttribute.idPolicy
  }

  def isComposition = modelAssociation != null && modelAssociation.isComposition
  def isAggregation = modelAssociation != null && modelAssociation.isAggregation

  final def isDataType: Boolean = {
    multiplicity == GaejOne && attributeType.isDataType
  }

  final def isName: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isName
  }

  final def isUser: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isUser
  }

  final def isTitle: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isTitle
  }

  final def isSubTitle: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isSubTitle
  }

  final def isSummary: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isSummary
  }

  final def isCategory: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isCategory
  }

  final def isAuthor: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isAuthor
  }

  final def isIcon: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isIcon
  }

  final def isLogo: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isLogo
  }

  final def isLink: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isLink
  }

  final def isContent: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isContent
  }

  final def isCreated: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isCreated
  }

  final def isUpdated: Boolean = {
    if (modelAttribute == null) return false
    else modelAttribute.isUpdated
  }

  final def isPersistentOption: Option[Boolean] = {
    if (modelAttribute != null) {
      if (modelAttribute.isPersistent) {
        Some(true)
      } else {
        Some(false)
      }
    } else if (modelAssociation != null) {
      if (modelAssociation.isPersistent) {
        Some(true)
      } else {
        Some(false)
      }
    } else if (modelPowertype != null) {
      error("power type attribute")
    } else {
      None
    }
  }

  final def isCache = {
    modelAssociation != null && modelAssociation.isCache
  }

  final def constraints = attributeType.constraints

  //
  final def multiplicity_is(aMultiplicity: GaejMultiplicity): GaejAttribute = {
    multiplicity = aMultiplicity
    this
  }

  //
  private def value_or_object_type_name = {
    if (use_object_over_datatype) {
      objectTypeName
    } else {
      attributeType.getDatatypeName match {
        case Some(value) => value
        case None => objectTypeName
      }
    }
  }

  private def object_type_name = attributeType.objectTypeName
  private def element_type_name = attributeType.objectTypeName
  private def list_type_name = "List<" + element_type_name + ">"

  private def type_name: String = {
    multiplicity match {
      case m: GaejOne => value_or_object_type_name
      case m: GaejZeroOne => object_type_name
      case m: GaejZeroMore => list_type_name
      case m: GaejOneMore => list_type_name
    }
  }

  private def type_name_object: String = {
    multiplicity match {
      case m: GaejOne => object_type_name
      case m: GaejZeroOne => object_type_name
      case m: GaejZeroMore => list_type_name
      case m: GaejOneMore => list_type_name
    }
  }

  //
  private def jdo_value_or_object_type_name = {
    if (use_object_over_datatype) {
      jdo_object_type_name
    } else {
      attributeType.getJdoDatatypeName match {
        case Some(value) => value
        case None => jdo_object_type_name
      }
    }
  }

  private def jdo_object_type_name = attributeType.jdoObjectTypeName
  private def jdo_element_type_name = attributeType.jdoObjectTypeName
  private def jdo_list_type_name = "List<" + jdo_element_type_name + ">"

  private def jdo_type_name: String = {
    multiplicity match {
      case m: GaejOne => jdo_value_or_object_type_name
      case m: GaejZeroOne => jdo_object_type_name
      case m: GaejZeroMore => jdo_list_type_name
      case m: GaejOneMore => jdo_list_type_name
    }
  }

  private def jdo_type_name_object: String = {
    multiplicity match {
      case m: GaejOne => jdo_object_type_name
      case m: GaejZeroOne => jdo_object_type_name
      case m: GaejZeroMore => jdo_list_type_name
      case m: GaejOneMore => jdo_list_type_name
    }
  }

  //
  final def isHasMany: Boolean = {
    multiplicity match {
      case m: GaejOne => false
      case m: GaejZeroOne => false
      case m: GaejZeroMore => true
      case m: GaejOneMore => true
      case _ => error("???")
    }
  }

  final def isOptional = {
    multiplicity == GaejZeroOne
  }

  final def isSingle = {
    multiplicity match {
      case m: GaejOne => true
      case m: GaejZeroOne => true
      case m: GaejZeroMore => false
      case m: GaejOneMore => false
      case _ => error("???")
    }
  }

  final def isEntity = {
    attributeType.isEntity
  }
}

abstract class GaejMultiplicity
class GaejOne extends GaejMultiplicity
object GaejOne extends GaejOne
class GaejZeroOne extends GaejMultiplicity
object GaejZeroOne extends GaejZeroOne
class GaejOneMore extends GaejMultiplicity
object GaejOneMore extends GaejOneMore
class GaejZeroMore extends GaejMultiplicity
object GaejZeroMore extends GaejZeroMore
class GaejRange extends GaejMultiplicity
