package org.simplemodeling.dsl

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import java.util.Locale
import org.goldenport.sdoc._

/*
 * derived from ModelElement since Mar. 18, 2007
 *
 * @since   Sep. 10, 2008
 * @version Sep. 18, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class SObject(aName: String, aPkgName: String) extends SElement(aName) {
  type Descriptable_TYPE = SObject
  type Historiable_TYPE = SObject
  if (aName == null) {
    set_name_by_className
  }

  private var _package_name = aPkgName

/*
  protected def set_name(pkgname: String, aName: String) {
    _package_name = pkgname
    set_name(aName)
  }
*/

  lazy val packageName: String = {
    if (_package_name != null) {
      _package_name
    } else {
      val pkg = getClass.getPackage
      if (pkg == null) ""
      else pkg.getName
    }
  }

  val qualifiedName: String = {
    packageName match {
      case "" => name
      case pkgName => pkgName + "." + name
    }
  }

  def this() = this(null, null)
  def this(aName: String) = this(aName, null)

  def isNull: Boolean = false

  // atom feed author
  // title
  // subtitle
  // updated

  /**
   * Used by Atom Feed.
   * 
   */
  var title: String = ""

  /**
   * Used by Atom Feed.
   */
  var subtitle : String = ""

  /**
   * Used by Atom Feed.
   */
  var author: String = ""

  /**
   * Used by Atom Feed.
   */
  var authors: ArrayBuffer[String] = new ArrayBuffer[String]

  /**
   * Used by Atom Feed.
   */
  var category: String = ""

  /**
   * Used by Atom Feed.
   */
  val categories = new ArrayBuffer[String]

  /**
   * Used by Atom Feed.
   */
  var contributor: String = ""

  /**
   * Used by Atom Feed.
   */
  val contributors = new ArrayBuffer[String]

  /**
   * Used by Atom Feed.
   */
  var generator: String = ""

  /**
   * Used by Atom Feed.
   */
  val generators = new ArrayBuffer[String]

  /**
   * Used by Atom Feed.
   */
  var icon: String = ""

  /**
   * Used by Atom Feed.
   */
  var logo: String = ""

  /**
   * Used by Atom Feed.
   */
  val rights: String = ""

  // used in BusinessUsecase.task and SimpleModelEntity.is_exists_or_register  
  def isObjectScope: Boolean = false
  val isMaster: Boolean = SObjectRepository.register(this)

  var xmlNamespace: String = null
  var baseObject: SObject = NullObject

  var version: String = ""

  val feature = new SFeatureSet
  val powertype = new SPowertypeSet
  val attribute = new SAttributeSet
  val association = new SAssociationSet(isMaster)
  val operation = new SOperationSet
  val statemachine = new SStateMachineSet(this)
  val role = new SRoleSet
  val service = new SServiceSet
  val rule = new SRuleSet
  val document = new SDocumentSet(isMaster)
  val port = new SPortSet(isMaster)
  protected val property_factory = new SPropertyFactory(attribute, association)

  def association_binary(aName: String, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    association.create(aName, anEntity, aMultiplicity) binary_is true
  }

  def association_binary(aSymbol: Symbol, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    association.create(aSymbol.name, anEntity, aMultiplicity) binary_is true
  }

  def aggregation(anEntity: => SEntity): SAssociation = {
    association.create(anEntity) aggregation_is true
  }

  def aggregation(anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    association.create(anEntity, aMultiplicity) aggregation_is true
  }

  def aggregation(aSymbol: Symbol, anEntity: => SEntity): SAssociation = {
    aggregation(aSymbol.name, anEntity)
  }

  def aggregation(aName: String, anEntity: => SEntity): SAssociation = {
    aggregation(aName, anEntity, One)
  }

  def aggregation(aSymbol: Symbol, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    aggregation(aSymbol.name, anEntity, aMultiplicity)
  }

  def aggregation(aName: String, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    association.create(aName, anEntity, aMultiplicity) aggregation_is true
  }

  def composition(aSymbol: Symbol, anEntity: => SEntity): SAssociation = {
    composition(aSymbol.name, anEntity)
  }

  def composition(aName: String, anEntity: => SEntity): SAssociation = {
    composition(aName, anEntity, One)
  }

  def composition(aSymbol: Symbol, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    composition(aSymbol.name, anEntity, aMultiplicity)
  }

  def composition(aName: String, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    association.create(aName, anEntity, aMultiplicity) composition_is true
  }

  def getXmlNamespace: Option[String] = xmlNamespace match {
    case null => None
    case _ => Some(xmlNamespace)
  }
  def getPowertypes = powertype.powertypes
  def getAttributes = attribute.attributes
  def getAssociations = association.associations
  def getOperations = operation.operations
  def getStateMachines = statemachine.stateMachines
  def getRoles = role.roles
  def getServices = service.services
  def getRules = rule.rules
  def getDocuments = document.documents
  def getPorts = port.ports

  final def getAttribute(aName: String): Option[SAttribute]= {
    getAttributes.find(aName == _.name)
  }

  final def getEffectiveAttribute(aName: String): Option[SAttribute] = {
    getAttributes.find(aName == _.name) match {
      case Some(attr) => Some(attr)
      case None => {
	if (baseObject == NullObject) None
	else baseObject.getEffectiveAttribute(aName)
      }
    }
  }

  final def getEffectiveIdAttribute: Option[SAttribute] = {
    getAttributes.find(IdAttributeKind == _.kind) match {
      case Some(attr) => Some(attr)
      case None => {
	if (baseObject == NullObject) None
	else baseObject.getEffectiveIdAttribute
      }
    }
  }

  implicit def toProxy(aName: String): SPropertyProxy = {
    property_factory.proxy(aName)
  }

  implicit def toProxy(aValue: SAttributeType): SAttributeProxy = {
    property_factory.value(aValue)
  }

  implicit def toProxy(aEntity: SEntity): SAssociationProxy = {
    property_factory.entity(aEntity)
  }

  final def is_a(anObject: SObject): SObject = { // XXX generic type
    is_kind_of(anObject)
  }

  final def is_kind_of(anObject: SObject): SObject = { // XXX generic type
    base(anObject)
  }

  final def base(anObject: SObject): SObject = {
    require (anObject != null)
    baseObject = anObject
    this
  }
}

class NullObject extends SObject {
  override def isNull = true
}

object NullObject extends NullObject {
}
