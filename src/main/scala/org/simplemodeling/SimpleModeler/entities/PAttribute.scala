package org.simplemodeling.SimpleModeler.entities

import org.apache.commons.lang3.StringUtils
import scalaz._
import Scalaz._
import scala.collection.mutable.{Buffer, ArrayBuffer}
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity._

/*
 * derived from GaejAttribute Apr. 10, 2009
 * 
 * @since   Apr. 22, 2011
 *  version Jul. 22, 2011
 *  version Feb. 19, 2012
 *  version Apr. 19, 2012
 *  version Oct. 30, 2012
 * @version Nov. 23, 2012
 * @author  ASAMI, Tomoharu
 */
/**
 * PAttribute represents SMAttribute, SMAssociation,
 * and SMPowertypeRelationship so that these relationships are
 * implemented as a instance variable.
 * 
 */
class PAttribute(val name: String, val attributeType: PObjectType, val readonly: Boolean = false, val inject: Boolean = false) {
  var isId = false
  var multiplicity: PMultiplicity = POne
  var modelAttribute: SMAttribute = null
  var modelAssociation: SMAssociation = null // XXX
  var modelPowertype: SMPowertypeRelationship = null // XXX
  var modelStateMachine: SMStateMachineRelationship = null // XXX
//  var modelStateMachine: SMStateMachineRelationship = null // XXX
  def modelElement: SMElement = {
    if (modelAttribute != null) modelAttribute
    else if (modelAssociation != null) modelAssociation
    else if (modelPowertype != null) modelPowertype
    else if (modelStateMachine != null) modelStateMachine
    else throw new IllegalArgumentException("modelElement should not be null.: " + name)
  } 
  def getModelElement: Option[SMElement] = {
    if (modelAttribute != null) modelAttribute.some
    else if (modelAssociation != null) modelAssociation.some
    else if (modelPowertype != null) modelPowertype.some
    else if (modelStateMachine != null) modelStateMachine.some
    else None
  }

  final def typeName: String = type_name
  final def objectTypeName: String = type_name_object
  final def elementTypeName: String = element_type_name
  final def jpaTypeName: String = jpa_type_name
  final def jpaElementTypeName: String = jpa_element_type_name
  final def jdoTypeName: String = jdo_type_name // AppEngine
  final def jdoElementTypeName: String = jdo_element_type_name // AppEngine
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

  def isMulti = !(multiplicity == POne || multiplicity == PZeroOne)

  def isComposition = modelAssociation != null && modelAssociation.isComposition
  def isAggregation = modelAssociation != null && modelAssociation.isAggregation

  final def isDataType: Boolean = {
    multiplicity == POne && attributeType.isDataType
  }

  /*
   * attributes for GUI
   * 
  final def name_en = dslElement.name_en
  final def name_ja = dslElement.name_ja
  final def term = dslElement.term
  final def term_en = dslElement.term_en
  final def term_ja = dslElement.term_ja
  // XXX another name stuff
  final def caption = dslElement.caption
  final def brief = dslElement.brief
  final def summary = dslElement.summary
  final def resume = dslElement.resume
  final def description = dslElement.description
  final def note = dslElement.note
  final def history = dslElement.history
   */
/* use PEntityContext#labelName
  def labelString: String = {
    // TODO locale
    getModelElement.flatMap(x => {
      _get_label(
        x.label.toText, x.title_sdoc.toText, x.term_ja, x.term_en, x.term,
        x.name_ja, x.name_en)
    }) | name
  }

  private def _get_label(candidates: String*): Option[String] = {
    candidates.find(StringUtils.isNotBlank)
  }
*/

  /*
   *
   */
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
      Some(false) // XXX
    } else if (modelStateMachine != null) {
      Some(false) // XXX
    } else {
      None
    }
  }

  final def isCache = {
    modelAssociation != null && modelAssociation.isCache
  }

  final def constraints: Map[String, PConstraint] = attributeType.constraints.toMap

  def deriveExpression: Option[PExpression] = {
    for {
      a <- Option(modelAttribute)
      b <- a.deriveExpression
    } yield PExpression(b)
  }

  def isDerive: Boolean = {
    Option(modelAttribute).flatMap(_.deriveExpression) ? true | false
  }

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

  /*
   * JPA
   */
  private def jpa_value_or_object_type_name = {
    if (use_object_over_datatype) {
      jpa_object_type_name
    } else {
      attributeType.getJpaDatatypeName match {
        case Some(value) => value
        case None => jpa_object_type_name
      }
    }
  }

  private def jpa_object_type_name = attributeType.jpaObjectTypeName
  private def jpa_element_type_name = attributeType.jpaObjectTypeName
  private def jpa_list_type_name = "List<" + jpa_element_type_name + ">"

  private def jpa_type_name: String = {
    multiplicity match {
      case m: POne => jpa_value_or_object_type_name
      case m: PZeroOne => jpa_object_type_name
      case m: PZeroMore => jpa_list_type_name
      case m: POneMore => jpa_list_type_name
    }
  }

  private def jpa_type_name_object: String = {
    multiplicity match {
      case m: POne => jpa_object_type_name
      case m: PZeroOne => jpa_object_type_name
      case m: PZeroMore => jpa_list_type_name
      case m: POneMore => jpa_list_type_name
    }
  }

  /*
   * JDO
   */
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
      case _ => sys.error("???")
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
      case _ => sys.error("???")
    }
  }

  final def isEntity = {
    attributeType.isEntity
  }

  //
  def idDatatypeName: String = {
    getIdDatatypeName.get
  }

  def getIdDatatypeName(): Option[String] = {
    Option(modelAttribute) flatMap { m =>
      SMObject.getIdDatatypeName(m.attributeType.typeObject)
    }
  }

  def getConstraints(): Seq[SConstraint] = {
    Option(modelAttribute) map { m =>
      m.dslAttribute.constraints
    } orZero
  }
}
