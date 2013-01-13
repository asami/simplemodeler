package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import scala.collection.mutable.ArrayBuffer
import org.apache.commons.lang3.StringUtils
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entities.simplemodel.SMMDisplay
import org.simplemodeling.SimpleModeler.SimpleModelerConstants
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.builder.NameLabel
import org.goldenport.value._
import org.goldenport.entity.{GEntity, GEntityContext} 
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.content.{GContent, EntityContent}
import org.simplemodeling.dsl._

/*
 * @since   Apr. 21, 2011
 *  version Aug. 20, 2011
 *  version Dec. 15, 2011
 *  version May.  5, 2012
 *  version Jun. 17, 2012
 *  version Oct. 26, 2012
 *  version Nov. 29, 2012
 *  version Dec. 26, 2012
 * @version Jan. 14, 2013
 * @author  ASAMI, Tomoharu
 */
abstract class PObjectEntity(val pContext: PEntityContext) 
  extends GEntity(pContext) with SimpleModelerConstants {
  type DataSource_TYPE = GDataSource
  type AttributeTYPE <: PAttribute
  type ClassDefinition_TYPE = GenericClassDefinition

  override def is_Text_Output = true

  protected var _baseObject: PObjectReferenceType = null
//  protected var _belongsTo: PEntityType = null
  protected val _mixinTraits = new ArrayBuffer[PObjectReferenceType]

  val fileSuffix: String

  var packageName = ""
  var kindName = ""
  var xmlNamespace = ""
  def qualifiedName = if (packageName != "") packageName + "." + name else name
  def getKindName: Option[String] = {
    if (StringUtils.isNotBlank(kindName)) kindName.some
    else None
  }

  /**
   * Used when this object is entity.
   */
  var documentName = ""
  /**
   * Sets in SimpleModel2ProgramRealmTransformerBase#build_entity.
   * Used by RepositoryServiceJavaClassDefinition#wadl_description_literal
   * to generate document description in wadl.
   */
  var documentEntity: Option[PDocumentEntity] = None
  /**
   * Used when this object is package.
   */
  var serviceName = ""
  var serviceEntity: Option[PServiceEntity] = None
  private val _attributes = new ArrayBuffer[PAttribute]
  private val _operations = new ArrayBuffer[POperation]
  val participations = new ArrayBuffer[PParticipation]

  def attributes: Seq[PAttribute] = _attributes
  def operations: Seq[POperation] = _operations

  def addAttribute(attr: PAttribute) {
    if (_attributes.exists(_.name == attr.name)) {
        record_warning("「%s」で属性・関連「%s」の重複があります。", name, attr.name)
    } else _attributes += attr
  }

  def addOperation(oper: POperation) {
    if (_operations.exists(_.name == oper.name)) {
        record_warning("「%s」で操作「%s」の重複があります。", name, oper.name)
    } else _operations += oper
  }

  /**
   * Logic duplicates with build_participation_association_class via
   * build_participation in SimpleModel2ProgramRealmTransformerBase.
   *
   * GenericClassDefinition uses this method.
   *
   * AttributeParticipation is setted in resolve_attribute of
   * SimpleModel2ProgramRealmTransformerBase. (not SimpleModelEntity).
   */
  lazy val associationEntityAttributes: List[PAttribute] = {
    participations.toList.collect({
      case a: AttributeParticipation if a.attribute.isAssociationClass => a
    }).flatMap(_to_attr)
  }

  private def _to_attr(a: AttributeParticipation): List[PAttribute] = {
//    println("PObjectEntity#AttributeParticipation(%s) = %s".format(name, a))
    val b = _another_association(a) match {
      case Some(x) => x.attributeType match {
        case e: PEntityType => {
          _entity_type_attribute(e.entity)
        }
        case d: PDocumentType => {
          _document_type_attribute(d.document)
        }
        case p: PPowertypeType => {
          _powertype_type_attribute(p.powertype)
        }
      }
      case None => a.source match {
        case e: PEntityEntity => {
          _entity_type_attribute(e)
        }
        case d: PDocumentEntity => {
          _document_type_attribute(d)
        }
        case p: PPowertypeEntity => {
          _powertype_type_attribute(p)
        }
      }
    }
/*
    val b = _direct_entity(a) match {
      case Some(x) => x
      case _ => a.source
    }
    val n = b.name
    val t = new PEntityType(b.name, b.packageName)
    t.entity = b.asInstanceOf[PEntityEntity]
    val c = new PAttribute(n, t) multiplicity_is PZeroMore // XXX
    List(c)
*/
    b.platformParticipation = Some(a)
    List(b)
  }

  private def _another_association(a: AttributeParticipation): Option[PAttribute] = {
    pContext.sqlAssociationClassCounterAssociation(a)
  }

  private def _entity_type_attribute(e: PEntityEntity): PAttribute = {
    val t = new PEntityType(e.name, e.packageName)
    t.entity = e
    val name = pContext.participationAssociationClassReferenceName(e.name)
    new PAttribute(name, t) multiplicity_is PZeroMore // XXX
  }

  private def _document_type_attribute(d: PDocumentEntity): PAttribute = {
    val t = new PDocumentType(d.name, d.packageName)
    t.document = d
    val name = pContext.participationAssociationClassReferenceName(d.modelObject.name)
    new PAttribute(name, t) multiplicity_is PZeroMore // XXX
  }

  private def _powertype_type_attribute(d: PPowertypeEntity): PAttribute = {
    val t = new PPowertypeType(d.name, d.packageName)
    t.powertype = d
    val name = pContext.participationAssociationClassReferenceName(d.modelObject.name)
    new PAttribute(name, t) multiplicity_is PZeroMore // XXX
  }
/*
  private def _direct_entity(a: AttributeParticipation): Option[PObjectEntity] = {
    _another_association(a.source, a.attribute).map(_.attributeType).collect {
      case e: PEntityType => e.entity
      case d: PDocumentType => d.document
    }
  }
*/

  /**
   * Name in program. Defined in the specification DSL.
   * 
   * "name" is defined in GEntity.
   *
   * Setuped in SimpleModel2JavaRealmTransformerBase.
   */
  def name_en = {
    (platform_name_en orElse modelPackage.map(_.name_en)) |
    modelObject.name_en
  }
  var platform_name_en: Option[String] = None

  def name_ja = {
    (platform_name_ja orElse modelPackage.map(_.name_ja)) |
    modelObject.name_ja
  }
  var platform_name_ja: Option[String] = None

  /**
   * Name in glossary. Defined in the specification DSL.
   */
  def term = {
    (platform_term orElse modelPackage.map(_.term)) |
    modelObject.term
  }
  var platform_term: Option[String] = None

  def term_en = {
    (platform_term_en orElse modelPackage.map(_.term_en)) |
    modelObject.term_en
  }
  var platform_term_en: Option[String] = None

  def term_ja = {
    (platform_term_ja orElse modelPackage.map(_.term_ja)) |
    modelObject.term_ja
  }
  var platform_term_ja: Option[String] = None

  /**
   * 
   */
  def label = {
    require (term != null && term_en != null && term_ja != null)
    pContext.labelName(modelObject)
  }
  // URL (term_name)
  /**
   * Program oriented name. Case Neutral. (preserved)
   */
  var asciiName = ""
  /**
   * Program oriented name. Lower case term.
   *
   * Use case: URI
   */
  var uriName = ""

  // Class Name base (TermName)
  var classNameBase = ""

  /**
   * Used by PEntityContext#applicationName
   */
  var modelPackage: Option[SMPackage] = None
  /**
   * Used by PEntityContext#applicationName
   */
  var modelObject: SMObject = SMNullObject
  /**
   * Used by PEntityContext#applicationName
   */
  var platformPackage: Option[PPackageEntity] = None
  /**
   * Used by PEntityContext#applicationName
   */
  var sourcePlatformObject: Option[PObjectEntity] = None
  /**
   * Holds platform package node which contains this node as child. 
   */
//  var containerNode: Option[GTreeNode[GContent]] = None
  lazy val xmlElementName = asciiName
  lazy val factoryName = pContext.factoryName(packageName)
  lazy val contextName = pContext.contextName(packageName)

  final def getModelObject = {
    if (modelObject == SMNullObject) None else modelObject.some
  }

  final def modelEntity: SMEntity = {
    require (modelObject.isInstanceOf[SMEntity], "modelObject should be SMEntity. (%s): %s".format(name, modelObject))
    modelObject.asInstanceOf[SMEntity]    
  }

  var isImmutable: Boolean = false

  override protected def write_Content(out: java.io.BufferedWriter) {
    val klass = class_Definition
    klass.build()
    out.append(klass.toText)
    out.flush
  }

  protected def class_Definition: ClassDefinition_TYPE = {
    throw new UnsupportedOperationException("Please create ClassDefinition or let this Entity be hidden(%s/%s).".format(name, getClass.getSimpleName))
  }

  /*
   * GUI
   */
  def guiView: Option[String] = modelEntity.guiView
  def guiTemplate: Option[String] = modelEntity.guiTemplate
  def guiWidget: Option[String] = modelEntity.guiWidget

  /*
   * SQL
   */
  def sqlTableName = {
    (platform_sqlTableName orElse modelPackage.map(_.sqlTableName)) |
    modelObject.sqlTableName
  }
  var platform_sqlTableName: Option[String] = None

  /*
   * Body
   */
  // base
  def getBaseObject: Option[PObjectEntity] = {
    if (_baseObject != null) Some(_baseObject.reference) else None
  }

  def setBaseObjectType(className: String, packageName: String) {
    _baseObject = new PObjectReferenceType(className, packageName)
  }

  def setKindedBaseObjectType(className: String, packageName: String) {
    _baseObject = new PObjectReferenceType(className, get_kinded_package_name(packageName))
  }

  def getBaseObjectType = {
    if (_baseObject != null) Some(_baseObject) else None
  }

  // traits
  def getTraitObjects: List[PObjectReferenceType] = {
    _mixinTraits.toList
  }

  def addTraitObjectType(className: String, packageName: String) {
    _mixinTraits += new PObjectReferenceType(className, packageName)
  }

  def addKindedTraitObjectType(className: String, packageName: String) {
    _mixinTraits += new PObjectReferenceType(className, get_kinded_package_name(packageName))
  }

  def getTraitObjectTypes = {
    _mixinTraits.toList
  }

  def getTraitNames: Seq[String] = {
    _mixinTraits.map(_.name)
  }

  //
  /**
   * Used by enum or entity decision in powertype.
   * This usages would be replacy by isKnowledge.
   */
//  def hasInheritance: Boolean = _baseObject != null || _mixinTraits.nonEmpty

  /**
   * Used by JavaClassDefinition
   * to decide whether or not to add attributes
   * derviced from entity reference participation.
   */
  def isEntity: Boolean = false

  /**
   * Used to decide creating event service.
   */
  def isEvent: Boolean = modelObject.isEvent

  // 
  def setKindedPackageName(pkgname: String) {
    packageName = get_kinded_package_name(pkgname)
  }

  protected final def get_kinded_package_name(pkgname: String): String = {
    if (UString.isNull(kindName)) pkgname
    else pkgname + "." + kindName
  }

  lazy val wholeAttributes: List[PAttribute] = {
//    println("PObjectEntity#wholeAttributes(%s, %s): %s".format(name, _baseObject, _mixinTraits.map(_.reference.wholeAttributes.map(_.name))))
    val a = whole_attributes(Set.empty)
//    println("PObjectEntity#wholeAttributes(%s): %s".format(name, a._1.map(_.name)))
    val b = a._1.foldLeft((nil[PAttribute], Set.empty[String]))((a, x) => {
      if (a._2.contains(x.name)) {
        record_warning("「%s」で属性・関連「%s」の重複があります。", name, x.name)
        a
      } else {
        (x :: a._1, a._2 + x.name)
      }
    })
    record_trace("PObjectEntity#wholeAttributes(%s, %s): %s".format(name, b._2, b._1.map(_.name)))
    b._1
//    (Option(_baseObject).orEmpty[List] ::: _mixinTraits.toList).flatMap(
//      _.reference.wholeAttributes
//    ) ::: attributes.toList
/*    
    if (_baseObject != null) {
      _baseObject.referenceOption match {
        case Some(ref) => ref.wholeAttributes ::: attributes.toList
        case None => attributes.toList
      }
    } else {
      attributes.toList
    }
*/
  }

  def whole_attributes(used: Set[String]): (List[PAttribute], Set[String]) = {
    val a = Option(_baseObject).orEmpty[List] ::: _mixinTraits.toList
    val b = a.foldLeft((nil[PAttribute], Set.empty[String]))((a, x) => {
      val c = x.reference.whole_attributes(a._2)
      (c._1 ::: a._1, c._2 ++ a._2)
    })
    if (b._2.contains(qualifiedName)) b // XXX what?
    else (b._1 ::: attributes.toList ::: associationEntityAttributes, b._2 + qualifiedName)
  }

  lazy val wholeAttributesWithoutId: List[PAttribute] = {
    wholeAttributes.filter(!_.isId)
  }

  lazy val wholeOperations: List[POperation] = {
//    println("PObjectEntity#wholeOperations(%s, %s): %s".format(name, _baseObject, _mixinTraits.map(_.reference.wholeOperations.map(_.name))))
    val a = whole_operations(Set.empty)
//    println("PObjectEntity#wholeOperations(%s): %s".format(name, a._1.map(_.name)))
    val b = a._1.foldLeft((nil[POperation], Set.empty[String]))((a, x) => {
      if (a._2.contains(x.name)) {
        record_warning("「%s」で属性・関連「%s」の重複があります。", name, x.name)
        a
      } else {
        (x :: a._1, a._2 + x.name)
      }
    })
    record_trace("PObjectEntity#wholeOperations(%s, %s): %s".format(name, b._2, b._1.map(_.name)))
    b._1
  }

  def whole_operations(used: Set[String]): (List[POperation], Set[String]) = {
    val a = Option(_baseObject).orEmpty[List] ::: _mixinTraits.toList
    val b = a.foldLeft((nil[POperation], Set.empty[String]))((a, x) => {
      val c = x.reference.whole_operations(a._2)
      (c._1 ::: a._1, c._2 ++ a._2)
    })
    if (b._2.contains(qualifiedName)) b
    else (b._1 ::: operations.toList, b._2 + qualifiedName)
  }

  lazy val isId: Boolean = idAttrOption ? true | false
  lazy val idAttrOption = wholeAttributes.find(_.isId)
  lazy val idAttr = idAttrOption match {
    case Some(attr) => attr
    case None => {
      throw new UnsupportedOperationException("no id [%s]: %s".format(
        name,
        wholeAttributes.map(_.name)
      ))
    }
  }
  def idName = idAttr.name
  def idPolicy = idAttr.idPolicy

  def nameName: String = {
    getNameName match {
      case Some(name) => name
      case None => ""
    }
  }

  def getNameAttr: Option[PAttribute] = {
    for (attr <- wholeAttributesWithoutId) {
      if (attr.isName) return Some(attr)
    }
    for (attr <- wholeAttributesWithoutId) {
      if (NameLabel.isMatch(attr.name) &&
          (attr.attributeType.isInstanceOf[PTokenType] ||
           attr.attributeType.isInstanceOf[PStringType])
        ) {
        return Some(attr)
      }
    }
    for (attr <- wholeAttributesWithoutId) {
      if (attr.attributeType.isInstanceOf[PTokenType]) {
        return Some(attr)
      }
    }
    for (attr <- wholeAttributesWithoutId) {
      if (attr.attributeType.isInstanceOf[PStringType]) {
        return Some(attr)
      }
    }
    None
  }

  var actions: SMActions = SMActions(Nil)
  var displays: SMDisplays = SMDisplays(Nil)

  def getDisplay(name: String): Option[SMMDisplay] = displays.get(name)

  def nameAttr: PAttribute = getNameAttr match {
    case Some(attr) => attr
    case None => {
      record_warning("Name特性を持つ属性がないのでId属性を使用します。(%s)".format(name))
      idAttr
    }
  }

  def getNameName: Option[String] = getNameAttr.map(_.name)

  def hasInheritancePowertypeExcludingBaseClasses: Boolean = {
    this.attributes.exists(_.isInheritancePowertype) ||
    _mixinTraits.exists(_.reference.wholeAttributes.exists(_.isInheritancePowertype))
  }

  def getInheritancePowertype: Option[PAttribute] = {
    wholeAttributes.find(_.isInheritancePowertype)
  }

  //
  def isUser: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isUser) return true
    }
    false
  }

  def getUserName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isUser) return Some(attr.name)
    }
    None
  }

  def isTitle: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isTitle) return true
    }
    false
  }

  def getTitleName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isTitle) return Some(attr.name)
    }
    None
  }

  def isSubTitle: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isSubTitle) return true
    }
    false
  }

  def getSubTitleName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isSubTitle) return Some(attr.name)
    }
    None
  }

  def isSummary: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isSummary) return true
    }
    false
  }

  def getSummaryName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isSummary) return Some(attr.name)
    }
    None
  }

  def isCategory: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isCategory) return true
    }
    false
  }

  def getCategoryName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isCategory) return Some(attr.name)
    }
    None
  }

  def isAuthor: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isAuthor) return true
    }
    false
  }

  def getAuthorName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isAuthor) return Some(attr.name)
    }
    None
  }

  def isIcon: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isIcon) return true
    }
    false
  }

  def getIconName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isIcon) return Some(attr.name)
    }
    None
  }

  def isLogo: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isLogo) return true
    }
    false
  }

  def getLogoName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isLogo) return Some(attr.name)
    }
    None
  }

  def isLink: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isLink) return true
    }
    false
  }

  def getLinkName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isLink) return Some(attr.name)
    }
    None
  }

  def isContent: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isContent) return true
    }
    false
  }

  def getContentName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isContent) return Some(attr.name)
    }
    None
  }

  def isCreated: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isCreated) return true
    }
    false
  }

  def getCreatedName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isCreated) return Some(attr.name)
    }
    None
  }

  def isUpdated: Boolean = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isUpdated) return true
    }
    false
  }

  def getUpdatedName: Option[String] = {
    for (attr <- attributes if !attr.isId) {
      if (attr.isUpdated) return Some(attr.name)
    }
    None
  }

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

/*
  final protected def java_type(anAttr: PAttribute) = {
    anAttr.kind match {
      case NullAttributeKind => anAttr.typeName
      case IdAttributeKind => {
        anAttr.idPolicy match {
          case SMAutoIdPolicy => "Long"
          case SMApplicationIdPolicy => "String"
        }
      }
      case NameAttributeKind => "String"
      case UserAttributeKind => "User"
      case TitleAttributeKind => "String"
      case CreatedAttributeKind => "Date"
      case UpdatedAttributeKind => "Date"
    }
  }
*/

  //
  final protected def attr_name(attr: PAttribute) = {
    pContext.attributeName(attr)
  }

  final protected def var_name(attr: PAttribute) = {
    pContext.variableName(attr)
  }

  final protected def doc_attr_name(attr: PAttribute) = {
    pContext.documentAttributeName(attr)
  }

  final protected def doc_var_name(attr: PAttribute) = {
    pContext.documentVariableName(attr)
  }

  final protected def entity_ref_assoc_var_name(attr: PAttribute) = {
    var_name(attr) + "_association"
  }

  final protected def entity_ref_part_var_name(attr: PAttribute) = {
    var_name(attr) + "_part"
  }

  final protected def entity_ref_powertype_var_name(attr: PAttribute) = {
    var_name(attr) + "_powertype"
  }

  //
  final protected def entity_ref_updated_var_name(attr: PAttribute) = {
    var_name(attr) + "_updated"
  }

  final protected def entity_ref_label_var_name(attr: PAttribute) = {
    var_name(attr) + "_label"
  }

  final protected def entity_ref_cache_var_name(attr: PAttribute) = {
    var_name(attr) + "_cache"
  }

  final protected def entity_ref_cache_timestamp_var_name(attr: PAttribute) = {
    var_name(attr) + "_cache_timestamp"
  }

/*
  final protected def java_type(anAttr: PAttribute) = {
    anAttr.typeName
  }

  final protected def java_element_type(anAttr: PAttribute) = {
    anAttr.elementTypeName
  }

  final protected def jdo_type(anAttr: PAttribute) = {
    anAttr.jdoTypeName
  }

  final protected def jdo_element_type(anAttr: PAttribute) = {
    anAttr.jdoElementTypeName
  }

  final protected def java_doc_type(anAttr: PAttribute) = {
    if (anAttr.isHasMany) {
      "List<" + java_doc_element_type(anAttr) + ">"
    } else {
      java_doc_element_type(anAttr)
    }
  }

  final protected def java_doc_element_type(anAttr: PAttribute) = {
    anAttr.attributeType match {
      case p: PEntityPartType => {
        p.part.documentName
      }
      case p: PPowertypeType => "String"
      case _ => java_element_type(anAttr)
    }
  }
*/
  protected final def is_settable(attr: PAttribute): Boolean = {
    if (isImmutable) return false
    // if (!attr.isId || attr.isId) {
    attr.kind match {
      case NullAttributeKind => true
      case IdAttributeKind => attr.idPolicy match {
        case SMAutoIdPolicy => false
        case SMApplicationIdPolicy => true
      }
      case NameAttributeKind => true
      case UserAttributeKind => true
      case TitleAttributeKind => true
      case SubTitleAttributeKind => true
      case SummaryAttributeKind => true
      case CategoryAttributeKind => true
      case AuthorAttributeKind => true
      case IconAttributeKind => true
      case LogoAttributeKind => true
      case LinkAttributeKind => true
      case ContentAttributeKind => true
      case CreatedAttributeKind => false
      case UpdatedAttributeKind => false
    }
  }

  protected final def is_logical_operation: Boolean = {
//    modelEntity.appEngine.logical_operation
    error("not supported yet")
  }

  protected final def is_logical_operation(entityType: PEntityType) = {
    entityType.entity.modelEntity.appEngine.logical_operation
  }

  protected final def is_owned_property(attr: PAttribute) = {
    attr.modelAssociation != null && 
    attr.modelAssociation.isComposition &&
    true
//    modelEntity != null &&
//    modelEntity.appEngine.use_owned_property
  }

  protected final def is_query_property(attr: PAttribute) = {
    attr.modelAssociation != null && 
    attr.modelAssociation.isQueryReference
  }

  protected final def entity_ref_jdo_var_name(attr: PAttribute) = {
    if (is_owned_property(attr)) {
      var_name(attr)
    } else {
      attr.attributeType match {
//        case e: GaejEntityType => var_name(attr) + "_id"
        case _ => var_name(attr)
      }
    }
  }

  protected final def entity_ref_is_loaded_var_name(attr: PAttribute) = {
    "is_loaded_" + entity_ref_jdo_var_name(attr) 
  }

  protected final def back_reference_var_name(attr: PAttribute): String = {
//    back_reference_var_name(modelEntity, attr.modelAssociation)
    error("not supported yet")
  }

  protected final def back_reference_var_name(source: SMObject, assoc: SMAssociation): String = {
    assoc.backReferenceNameOption match {
      case Some(name) => name
//      case None => "_backref_%s_%s".format(gaejContext.termName(source), gaejContext.termName(assoc))
    }
  }

/*
  def packageChildren: Seq[PObjectEntity] = {
    val cn = containerNode match {
      case Some(n) => Some(n)
      case None => platformPackage match {
        case Some(p) => p.containerNode
        case None => None
      }
    }
    cn match {
//      case Some(node) => node.children.map(_.content) collect {
//        case o: PObjectEntity => o
//      }
      case Some(node) => {
        val x = node.children.map(_.content)
        val y = x collect {
          case o: EntityContent => o.entity
        }
        val z = y collect {
          case o: PObjectEntity => o
        }
        z
      }
      case None => Nil
    }
  }

  def packageEntities: Seq[PEntityEntity] = {
    packageChildren collect {
      case entity: PEntityEntity => entity
    }
  }
*/
}
