package org.simplemodeling.SimpleModeler.entities

import org.apache.commons.lang3.StringUtils
import scalaz._
import Scalaz._
import scala.collection.mutable.{Buffer, ArrayBuffer}
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.builder.NaturalLabel
import org.simplemodeling.SimpleModeler.entity._

/*
 * derived from GaejAttribute Apr. 10, 2009
 * 
 * @since   Apr. 22, 2011
 *  version Jul. 22, 2011
 *  version Feb. 19, 2012
 *  version Apr. 19, 2012
 *  version Oct. 30, 2012
 *  version Nov. 26, 2012
 * @version Dec. 22, 2012
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
  var modelParticipation: SMParticipation = null // XXX
  def modelElement: SMElement = {
    if (modelAttribute != null) modelAttribute
    else if (modelAssociation != null) modelAssociation
    else if (modelPowertype != null) modelPowertype
    else if (modelStateMachine != null) modelStateMachine
    else if (modelParticipation != null) modelParticipation
    else throw new IllegalArgumentException("modelElement should not be null.: " + name)
  } 
  def getModelElement: Option[SMElement] = {
    if (modelAttribute != null) modelAttribute.some
    else if (modelAssociation != null) modelAssociation.some
    else if (modelPowertype != null) modelPowertype.some
    else if (modelStateMachine != null) modelStateMachine.some
    else if (modelParticipation != null) modelParticipation.some
    else None
  }
  def getModelAssociation: Option[SMAssociation] = {
    Option(modelAssociation)
  }

  /**
   * PObjectEntity sets when this attribute created from participation.
   * JavaExpressionBuilder uses it to get entity via association class.
   */
  var platformParticipation: Option[PParticipation] = None

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

  def name_en = {
    getModelElement.map(_.name_en) | name
  }

  def name_ja = {
    getModelElement.map(_.name_ja) | name
  }

  def term = {
    getModelElement.map(_.term) | name
  }

  def term_en = {
    getModelElement.map(_.term_en) | name
  }

  def term_ja = {
    getModelElement.map(_.term_ja) | name
  }

  def xmlName = {
    getModelElement.map(_.xmlName) | name
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

  /**
   * SqlMaker Uses to decide requiring join.
   */
  def isEntityReference = {
    modelAssociation != null ||
    (modelPowertype != null && modelPowertype.isEntityReference) ||
    (modelStateMachine != null && modelStateMachine.isEntityReference) ||
    platformParticipation.isDefined
  }

  def isAssociationClass = getModelAssociation.map(_.isAssociationClass) | false

  def getProperty(key: String) = {
    getModelElement.flatMap(_.getProperty(key))
  }

  def getProperty(key: NaturalLabel) = {
    getModelElement.flatMap(_.getProperty(key))
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
   * GUI
   */
  def displaySequence: Int = {
    getModelElement.map(_.displaySequence) | SConstants.DEFAULT_DISPLAY_SEQUENCE
  }

  /*
   * SQL
   */
  def sqlColumnName: String = {
    getModelElement.map(_.sqlColumnName) | name
  }

  def sqlAutoId: Boolean = {
    getModelElement.map(_.sqlAutoId) | false
  }

  def sqlReadOnly: Boolean = {
    getModelElement.map(_.sqlReadOnly) | false
  }

  def sqlAutoCreate: Boolean = {
    getModelElement.map(_.sqlAutoCreate) | false
  }

  def sqlAutoUpdate: Boolean = {
    getModelElement.map(_.sqlAutoUpdate) | false
  }
  // def sqlLifeCycle = modelElement.sqlLifeCycle

  /*
   * Atom Publishing
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
      case POne => value_or_object_type_name
      case PZeroOne => object_type_name
      case PZeroMore => list_type_name
      case POneMore => list_type_name
    }
  }

  private def type_name_object: String = {
    multiplicity match {
      case POne => object_type_name
      case PZeroOne => object_type_name
      case PZeroMore => list_type_name
      case POneMore => list_type_name
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
      case POne => jpa_value_or_object_type_name
      case PZeroOne => jpa_object_type_name
      case PZeroMore => jpa_list_type_name
      case POneMore => jpa_list_type_name
    }
  }

  private def jpa_type_name_object: String = {
    multiplicity match {
      case POne => jpa_object_type_name
      case PZeroOne => jpa_object_type_name
      case PZeroMore => jpa_list_type_name
      case POneMore => jpa_list_type_name
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
      case POne => jdo_value_or_object_type_name
      case PZeroOne => jdo_object_type_name
      case PZeroMore => jdo_list_type_name
      case POneMore => jdo_list_type_name
    }
  }

  private def jdo_type_name_object: String = {
    multiplicity match {
      case POne => jdo_object_type_name
      case PZeroOne => jdo_object_type_name
      case PZeroMore => jdo_list_type_name
      case POneMore => jdo_list_type_name
    }
  }

  //
  final def isHasMany: Boolean = {
    multiplicity match {
      case POne => false
      case PZeroOne => false
      case PZeroMore => true
      case POneMore => true
      case _ => sys.error("???")
    }
  }

  final def isOptional = {
    multiplicity == PZeroOne
  }

  final def isSingle = {
    multiplicity match {
      case POne => true
      case PZeroOne => true
      case PZeroMore => false
      case POneMore => false
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

  //
  override def toString() = {
    val a = getModelElement.map(_.toString) orElse platformParticipation
    name + multiplicity.mark + "/" + (a | "No model")
  }
}
