package org.simplemodeling.dsl

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import java.util.Locale
import org.goldenport.sdoc._
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * derived from ModelElement since Mar. 18, 2007
 *
 * @since   Sep. 10, 2008
 *  version Sep. 18, 2011
 *  version Oct. 21, 2012
 * @version Nov. 22, 2012
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
  // var title: String = ""

  /**
   * Used by Atom Feed.
   */
  // var subtitle : String = ""

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
  def isObjectScope: Boolean = false // OLD logic
  val isRegisteredMaster: Boolean = SObjectRepository.register(this) // OLD logic
  // New logic
  def isMaster = isRegisteredMaster | isMasterSingleton
  var isMasterSingleton = true

  var xmlNamespace: String = null
  var baseObject: SObject = NullObject
  val mixinTrait = new STraitSet

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
  def getTraits = mixinTrait.traits
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

  /*
   *
   */

  // id
  def attribute_id: SAttribute = attribute_id('id)
  def attribute_id(symbol: Symbol): SAttribute = attribute_id(symbol.name)
  def attribute_id(name: String): SAttribute = attribute.create(name, XLong) kind_is IdAttributeKind idPolicy_is AutoIdPolicy
  // name
  def attribute_name: SAttribute = attribute_name('name)
  def attribute_name(symbol: Symbol): SAttribute = attribute_name(symbol.name)
  def attribute_name(name: String): SAttribute = attribute.create(name, XString) kind_is NameAttributeKind
  // user
  def attribute_user: SAttribute = attribute_user('user)
  def attribute_user(symbol: Symbol): SAttribute = attribute_user(symbol.name)
  def attribute_user(name: String): SAttribute = attribute.create(name, XUser) kind_is UserAttributeKind
  // title
  def attribute_title: SAttribute = attribute_title('title)
  def attribute_title(symbol: Symbol): SAttribute = attribute_title(symbol.name)
  def attribute_title(name: String): SAttribute = attribute.create(name, XString) kind_is TitleAttributeKind
  // subtitle
  def attribute_subtitle: SAttribute = attribute_subtitle('subtitle)
  def attribute_subtitle(symbol: Symbol): SAttribute = attribute_subtitle(symbol.name)
  def attribute_subtitle(name: String): SAttribute = attribute.create(name, XString) kind_is SubTitleAttributeKind
  // summary
  def attribute_summary: SAttribute = attribute_summary('summary)
  def attribute_summary(symbol: Symbol): SAttribute = attribute_summary(symbol.name)
  def attribute_summary(name: String): SAttribute = attribute.create(name, XString) kind_is SummaryAttributeKind
  // category
  def attribute_category: SAttribute = attribute_category('category)
  def attribute_category(symbol: Symbol): SAttribute = attribute_category(symbol.name)
  def attribute_category(name: String): SAttribute = attribute.create(name, XCategory, ZeroMore) kind_is CategoryAttributeKind
  // categories
  def attribute_categories: SAttribute = attribute_categories('categories)
  def attribute_categories(symbol: Symbol): SAttribute = attribute_categories(symbol.name)
  def attribute_categories(name: String): SAttribute = attribute.create(name, XCategory) kind_is CategoryAttributeKind
  // author
  def attribute_author: SAttribute = attribute_author('author)
  def attribute_author(symbol: Symbol): SAttribute = attribute_author(symbol.name)
  def attribute_author(name: String): SAttribute = attribute.create(name, XString) kind_is AuthorAttributeKind
  // icon
  def attribute_icon: SAttribute = attribute_icon('icon)
  def attribute_icon(symbol: Symbol): SAttribute = attribute_icon(symbol.name)
  def attribute_icon(name: String): SAttribute = attribute.create(name, XString) kind_is IconAttributeKind
  // logo
  def attribute_logo: SAttribute = attribute_logo('logo)
  def attribute_logo(symbol: Symbol): SAttribute = attribute_logo(symbol.name)
  def attribute_logo(name: String): SAttribute = attribute.create(name, XString) kind_is LogoAttributeKind
  // link
  def attribute_link: SAttribute = attribute_link('link)
  def attribute_link(symbol: Symbol): SAttribute = attribute_link(symbol.name)
  def attribute_link(name: String): SAttribute = attribute.create(name, XLink) kind_is LinkAttributeKind
  // content
  def attribute_content: SAttribute = attribute_content('content)
  def attribute_content(symbol: Symbol): SAttribute = attribute_content(symbol.name)
  def attribute_content(name: String): SAttribute = attribute.create(name, XText) kind_is ContentAttributeKind
  // published
  def attribute_created: SAttribute = attribute_created('created)
  def attribute_created(symbol: Symbol): SAttribute = attribute_created(symbol.name)
  def attribute_created(name: String): SAttribute = attribute.create(name, XDateTime) kind_is CreatedAttributeKind
  // updated
  def attribute_updated: SAttribute = attribute_updated('updated)
  def attribute_updated(symbol: Symbol): SAttribute = attribute_updated(symbol.name)
  def attribute_updated(name: String): SAttribute = attribute.create(name, XDateTime) kind_is UpdatedAttributeKind
  // contributor
  // rights
  // source

  def composition_comment(aSymbol: Symbol, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    composition_comment(aSymbol.name, anEntity, aMultiplicity)
  }

  def composition_comment(aName: String, anEntity: => SEntity, aMultiplicity: SMultiplicity): SAssociation = {
    association.create(aName, anEntity, aMultiplicity) composition_is true atomLinkRel_is "comment"
  }

  override def toString() = {
    class_Name + "(" + (name +: class_Params).mkString(",") + ")"
  }

  def class_Name: String = getClass.getSimpleName
  def class_Params: Seq[String] = Nil
}

class NullObject extends SObject {
  override def isNull = true
  override def class_Name = "NullObject"
}

object NullObject extends NullObject
