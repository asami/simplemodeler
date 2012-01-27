package org.simplemodeling.SimpleModeler.entities

import scala.collection.mutable.{Buffer, ArrayBuffer}
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity._

// derived from GaejAttribute Apr. 10, 2009
/**
 * PAttribute represents SMAttribute, SMAssociation,
 * and SMPowertypeRelationship so that these relationships are
 * implemented as a instance variable.
 * 
 * @since   Apr. 22, 2011
 * @version Jul. 22, 2011
 * @author  ASAMI, Tomoharu
 */
// XXX Should be immutable
class PAttribute(val name: String, val attributeType: PObjectType, val readonly:Boolean = false, val inject: Boolean = false) {
  var isId = false
  var multiplicity: PMultiplicity = POne
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
    multiplicity == POne && attributeType.isDataType
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

  final def constraints: Map[String, PConstraint] = attributeType.constraints.toMap

  //
  final def multiplicity_is(aMultiplicity: PMultiplicity): PAttribute = {
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
      case m: POne => value_or_object_type_name
      case m: PZeroOne => object_type_name
      case m: PZeroMore => list_type_name
      case m: POneMore => list_type_name
    }
  }

  private def type_name_object: String = {
    multiplicity match {
      case m: POne => object_type_name
      case m: PZeroOne => object_type_name
      case m: PZeroMore => list_type_name
      case m: POneMore => list_type_name
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
      case m: POne => jdo_value_or_object_type_name
      case m: PZeroOne => jdo_object_type_name
      case m: PZeroMore => jdo_list_type_name
      case m: POneMore => jdo_list_type_name
    }
  }

  private def jdo_type_name_object: String = {
    multiplicity match {
      case m: POne => jdo_object_type_name
      case m: PZeroOne => jdo_object_type_name
      case m: PZeroMore => jdo_list_type_name
      case m: POneMore => jdo_list_type_name
    }
  }

  //
  final def isHasMany: Boolean = {
    multiplicity match {
      case m: POne => false
      case m: PZeroOne => false
      case m: PZeroMore => true
      case m: POneMore => true
      case _ => error("???")
    }
  }

  final def isOptional = {
    multiplicity == PZeroOne
  }

  final def isSingle = {
    multiplicity match {
      case m: POne => true
      case m: PZeroOne => true
      case m: PZeroMore => false
      case m: POneMore => false
      case _ => error("???")
    }
  }

  final def isEntity = {
    attributeType.isEntity
  }
}
