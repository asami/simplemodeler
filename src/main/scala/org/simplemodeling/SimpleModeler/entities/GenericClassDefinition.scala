package org.simplemodeling.SimpleModeler.entities

import org.apache.commons.lang3.StringUtils
import scalaz._, Scalaz._
import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessEntity
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainTrait
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainActor
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainDocument
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainEntity
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainEntityPart
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainEvent
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainPowertype
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainResource
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainRole
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainRule
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainService
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainSummary
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainValue
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainValueId
import org.simplemodeling.SimpleModeler.entity.domain.SMDomainValueName
import org.simplemodeling.SimpleModeler.entity.AggregationParticipationRole
import org.simplemodeling.SimpleModeler.entity.AssociationParticipationRole
import org.simplemodeling.SimpleModeler.entity.AttributeParticipationRole
import org.simplemodeling.SimpleModeler.entity.CompositionParticipationRole
import org.simplemodeling.SimpleModeler.entity.SMAttribute
import org.simplemodeling.SimpleModeler.entity.SMAssociation
import org.simplemodeling.SimpleModeler.entity.SMPowertypeRelationship
import org.simplemodeling.SimpleModeler.entity.SMDatatype
import org.simplemodeling.SimpleModeler.entity.SMEntity
import org.simplemodeling.SimpleModeler.entity.SMObject
import org.simplemodeling.SimpleModeler.entity.SMPackage
import org.simplemodeling.SimpleModeler.entity.SMParticipation
import org.simplemodeling.SimpleModeler.entity.SMStoryObject
import org.simplemodeling.SimpleModeler.entity.SimpleModelVisitor
import org.goldenport.recorder.Recordable

/*
 * Generic Class Definition
 * 
 * @since   Jun.  4, 2011
 *  version Sep. 25, 2011
 *  version Feb. 20, 2012
 *  version May. 15, 2012
 *  version Jun. 10, 2012
 *  version Oct. 30, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class GenericClassDefinition(
  val pContext: PEntityContext,
  val aspects: Seq[GenericAspect],
  val pobject: PObjectEntity
) extends Recordable {
  type ATTR_DEF <: GenericClassAttributeDefinition
  type OP_DEF <: GenericClassOperationDefinition

  def name: String = customName getOrElse pobject.name
  val packageName = pobject.packageName
  val xmlNamespace = pobject.xmlNamespace
  lazy val qualifiedName = pobject.qualifiedName
  lazy val attributes = pobject.attributes.toList ::: customAttributes.toList
  val operations = pobject.operations
  val name_en = pobject.name_en
  val name_ja = pobject.name_ja
  val term = pobject.term
  val term_en = pobject.term_en
  val term_ja = pobject.term_ja
  // URL (term_name)
  val asciiName = pobject.asciiName
  // Class Name base (TermName)
  val classNameBase = pobject.classNameBase
  lazy val xmlElementName = asciiName
  lazy val contextName = pContext.contextName(packageName)
  lazy val moduleName = pContext.moduleName(packageName)
  lazy val factoryName = pContext.factoryName(packageName)
  lazy val repositoryName = pContext.repositoryName(packageName)
  lazy val modelName = pContext.modelName(packageName)
  lazy val errorModelName = pContext.errorModelName(packageName)
  lazy val agentName = pContext.agentName(packageName)
  lazy val controllerName = pContext.controllerName(packageName)
  lazy val entityServiceName = pContext.entityServiceName(packageName)
  lazy val eventServiceName = pContext.eventServiceName(packageName)

  def this(c: PEntityContext, o: PObjectEntity) = this(c, Nil, o)

  setup_FowardingRecorder(pContext)

  val documentName = pobject match {
    case e: PEntityObjectEntity => e.documentName
    case _ => ""
  }
  def isDocumentOwner = StringUtils.isNotBlank(documentName)
  val modelObject: SMObject = pobject.modelObject
  val baseObject: Option[PObjectReferenceType] = pobject.getBaseObjectType
  def hasBaseObject = baseObject.isDefined
  val mixinTraits: List[PObjectReferenceType] = pobject.getTraitObjects
  def isRootObject = baseObject.isEmpty
  val modelEntityOption: Option[SMEntity] = modelObject match {
    case entity: SMEntity => Some(entity)
    case _ => None
  }
  def modelEntity: SMEntity = modelEntityOption.get
  def modelPackageOption: Option[SMPackage] = pobject.modelPackage
  def modelPackage: SMPackage = pobject.modelPackage.get

  lazy val isId = pobject.isId
  lazy val idAttr = pobject.idAttr
  def idName = idAttr.name
  def idPolicy = idAttr.idPolicy
  def nameName = pobject.nameName
  def getNameName = pobject.getNameName

  /*
   * Attributes info
   */
  lazy val attributeDefinitions: List[ATTR_DEF] = {
    val a = attributes.map(attribute).toList
    _ordering(a)
  }
  lazy val parentAttributeDefinitions: List[ATTR_DEF] = {
    val a: List[ATTR_DEF] = baseObject.map(
      _.reference.wholeAttributes.map(attribute).toList
    ).orZero
    _ordering(a)
  }
  lazy val traitsAttributeDefinitions: List[ATTR_DEF] = {
    val a = mixinTraits flatMap {
      _.reference.wholeAttributes.map(attribute)
    }
    _ordering(a)
  }
  lazy val implementsAttributeDefinitions: List[ATTR_DEF] = {
    val a = attributeDefinitions ::: traitsAttributeDefinitions
    _ordering(_cleansing(a))
  }
  lazy val wholeAttributeDefinitions: List[ATTR_DEF] = {
    val a = parentAttributeDefinitions ::: implementsAttributeDefinitions
    _ordering(_cleansing(a))
  }

  /**
   * Use foldLeft to preserve ordering.
   */
  private def _cleansing(attrs: List[ATTR_DEF]): List[ATTR_DEF] = {
    attrs.foldLeft((nil[ATTR_DEF], Set.empty[String]))((a, x) => {
      if (a._2.contains(x.attr.name)) {
        record_warning("「%s」で属性・関連「%s」の重複があります。", name, x.attr.name)
        a
      } else {
        (a._1 :+ x, a._2 + x.attr.name)
      }
    })._1
  }

  implicit val orderer = new scala.math.Ordering[ATTR_DEF] {
    def compare(lhs: ATTR_DEF, rhs: ATTR_DEF) = {
      _numbering(lhs) compare _numbering(rhs)
    }
    private def _numbering(a: ATTR_DEF): Int = {
      if (a.attr.isId) 1
      else a.attr.getModelElement match {
        case Some(s) => s match {
          case p: SMPowertypeRelationship => 2
//          case s: SMStateMachineRelationship => 3
          case a: SMAttribute => 4
          case a: SMAssociation => 5
          case _ => 6
        }
        case None => 6
      }
    }
  }

  private def _ordering(attrs: List[ATTR_DEF]): List[ATTR_DEF] = {
    attrs.sorted
  }

  lazy val effectiveAttributeDefinitions: List[ATTR_DEF] = {
    if (useWholeAttributes) {
      wholeAttributeDefinitions
    } else {
      implementsAttributeDefinitions
    }
  }
  lazy val effectiveNoIdAttributeDefinitions: List[ATTR_DEF] = {
    effectiveAttributeDefinitions.filterNot(_.attr.isId)
  }
  lazy val effectiveIdAttributeDefinition: Option[ATTR_DEF] = {
    effectiveAttributeDefinitions.find(_.attr.isId)
  }
  
  /**
   * Used by JavaClassDefinition to collect constructor parameters.
   */
  protected def not_derived_implements_attribute_definitions = {
    record_trace("GenericClassDefinition#not_derived_implements_attribute_definitions(%s): %s".format(name, implementsAttributeDefinitions.map(_.attr.name)))
    if (useDerivedAttribute) 
      implementsAttributeDefinitions.filterNot(_.isDerive)
    else
      implementsAttributeDefinitions
  }

  protected def not_derived_whole_attribute_definitions = {
    if (useDerivedAttribute)
      wholeAttributeDefinitions.filterNot(_.isDerive)
    else
      wholeAttributeDefinitions
  }

  protected def not_derived_parent_attribute_definitions = {
    if (useDerivedAttribute)
      parentAttributeDefinitions.filterNot(_.isDerive)
    else
      parentAttributeDefinitions
  }

  /*
   * operation info
   */
  lazy val operationDefinitions: List[OP_DEF] = {
    val a = operations.map(operation).toList
    _ordering_operation(a)
  }

  lazy val traitsOperationDefinitions: List[OP_DEF] = {
    val a = mixinTraits flatMap {
      _.reference.wholeOperations.map(operation)
    }
    _ordering_operation(a)
  }

  lazy val implementsOperationDefinitions: List[OP_DEF] = {
    val a = operationDefinitions ::: traitsOperationDefinitions
    _ordering_operation(a)
  }

  private def _ordering_operation(ops: List[OP_DEF]): List[OP_DEF] = {
    ops
  }

  //
  var useDocument: Boolean = false
  var usePersistent: Boolean = false
  /**
   * Sets true in EntityJavaClassDefinition.
   * In case of Entity is true.
   * In case of Value is false to convey real value.
   */
  var useDerivedAttribute: Boolean = false
  var isImmutable: Boolean = pobject.isImmutable
  def useBuilder: Boolean = isImmutable
  var isStatic: Boolean = false
  var classifierKind: ClassifierKind = ClassClassifierKind
  var customName: Option[String] = None
  var customBaseName: Option[String] = None
  var customImplementNames: List[String] = Nil
//  var identityEquality: Boolean = true
  var isValueEquality: Boolean = false
  /**
   * The object is used as data so that data manipulation utility
   * methods (e.g. XML, JSON), are supported.
   */
  var isData: Boolean = false
  val customAttributes = new ArrayBuffer[PAttribute]
  var isCustomVariableImplementation = false
  var isSingleton = false
  var useWholeAttributes = false

//  private val _maker = new JavaMaker

//  aspects.foreach(_.open(_maker))

  def build() {
    definition
  }

  def toText: String

  def definition {
    if (!isStatic) {
      head_package
      head_imports
    }
    class_open
    attribute_variables
    package_variables
    lifecycle_variables
    constructors
    lifecycle_methods
    attribute_methods
    service_methods
    to_methods
    update_methods
    object_methods
    if (useDocument) {
      document_methods
    }
    if (usePersistent) {
      persistent_methods
    }
    package_methods
    object_auxiliary
    utilities
    builder
    class_close
  }

  def includeImports {
    head_imports_body
  }

  protected def head_package {
  }

  /*
   * Import
   */
  protected def head_imports {
    head_imports_body
    head_imports_Epilogue
  }

  protected def head_imports_body {
    head_imports_Prologue
    baseObject.foreach(head_imports_Object)
    val attrs = attributes.filter(_.attributeType.isEntity)
    for (attr <- attrs) {
      val entity = attr.attributeType.asInstanceOf[PEntityType]
      head_imports_Entity(entity)
    }
    effectiveAttributeDefinitions.foreach(_.head_imports)
    aspects.foreach(_.weaveImports)
    head_imports_Extension
    if (useBuilder) {
      head_imports_Builder
    }    
  }

  protected def head_imports_Prologue {
  }

  protected def head_imports_Object(anObject: PObjectReferenceType) {
  }

  protected def head_imports_Entity(anEntity: PEntityType) {
  }

  protected def head_imports_Extension {
  }

  protected def head_imports_Builder {
  }

  protected def head_imports_Epilogue {
  }

  /*
   * Class open close
   */
  protected def class_open {
    class_open_document
    aspects.foreach(_.weaveOpenAnnotation)
    class_open_Annotation
    class_open_body
  }

  protected def class_open_document {
  }

  protected def class_open_Annotation {
  }

  protected def class_open_body

  protected def class_close {
    class_close_body
  }

  protected def class_close_body

  /*
   * Attributes Variables
   */
  protected def attribute_variables {
    attribute_variables_Prologue
    attribute_variables_constants
    attribute_variables_id
    attribute_variables_plain
    attribute_variables_aggregate
    attribute_variables_extension
    attribute_variables_Epilogue
  }

  protected def attribute_variables_Prologue {}

  protected def attribute_variables_constants {
    for (attr <- attributeDefinitions) {
      attr.constant_property
    }
  }

  protected def attribute_variables_id {
    for (attr <- effectiveIdAttributeDefinition) {
      attr.variable_id
    }
  }

  protected def attribute(attr: PAttribute): ATTR_DEF

  protected def attribute_variables_plain {
    for (attr <- effectiveAttributeDefinitions if !attr.attr.isId) {
      attr.variable_plain
    }
  }

  protected def attribute_variables_aggregate {
    def back_reference(source: SMObject, assoc: SMAssociation) {
      attribute_variables_aggregate_BackReference(source, assoc)
    }

    def is_back_reference(participation: SMParticipation) = {
      participation.roleType match {
        case CompositionParticipationRole => true
        case AggregationParticipationRole => true
        case AssociationParticipationRole => {
          participation.association.isBinary ||
          participation.association.isQueryReference
        }
        case AttributeParticipationRole => false
      }
    }

    for (participation <- modelObject.participations) {
      if (is_back_reference(participation)) {
        back_reference(participation.element, participation.association)
      }
    }
  }

  protected def attribute_variables_aggregate_BackReference(source: SMObject, assoc: SMAssociation) {}

  protected def attribute_variables_extension {
  }

  protected def attribute_variables_Epilogue {}

  /*
   * Operation
   */
  protected def operation(op: POperation): OP_DEF = NullClassOperationDefinition.asInstanceOf[OP_DEF]

  /*
   * Package (module) scope variables compartment
   */
  protected def package_variables {
    package_variables_simplemodel
    package_variables_platformmodel
    package_variables_Extension
  }

  protected def package_variables_simplemodel {
    for (pkg <- pobject.modelPackage; child <- pkg.children) {
      child match {
        case tr: SMDomainTrait            => package_variables_Trait(tr)
        case actor: SMDomainActor         => package_variables_Actor(actor)
        case resource: SMDomainResource   => package_variables_Resource(resource)
        case event: SMDomainEvent         => package_variables_Event(event)
        case role: SMDomainRole           => package_variables_Role(role)
        case summary: SMDomainSummary     => package_variables_Summary(summary)
        case entity: SMDomainEntity       => package_variables_Entity(entity)
        case part: SMDomainEntityPart     => package_variables_Entity_Part(part)
        case id: SMDomainValueId          => package_variables_Id(id)
        case name: SMDomainValueName      => package_variables_Name(name)
        case value: SMDomainValue         => package_variables_Value(value)
        case powertype: SMDomainPowertype => package_variables_Powertype(powertype)
        case document: SMDomainDocument   => package_variables_Document(document)
        case rule: SMDomainRule           => package_variables_Rule(rule)
        case service: SMDomainService     => package_variables_Service(service)
        //        case port: SMDomainPort => package_variables_Port(port)
        //        case facade: SMDomainFacade => package_variables_Facade(facade)
        case datatype: SMDatatype         => {}
        case uc: SMStoryObject            => {}
        case be: SMBusinessEntity         => {}
        case pkg: SMPackage               => {}
        case unknown                      => error("Unspported simple model object = " + unknown)
      }
    }
  }

  protected def package_variables_Trait(tr: SMDomainTrait) {
  }

  protected def package_variables_Actor(actor: SMDomainActor) {
    package_variables_Entity(actor)
  }

  protected def package_variables_Resource(resource: SMDomainResource) {
    package_variables_Entity(resource)
  }

  protected def package_variables_Event(event: SMDomainEvent) {
    package_variables_Entity(event)
  }

  protected def package_variables_Role(role: SMDomainRole) {
    package_variables_Entity(role)
  }

  protected def package_variables_Summary(summary: SMDomainSummary) {
    package_variables_Entity(summary)
  }

  protected def package_variables_Entity(entity: SMDomainEntity) {
  }

  protected def package_variables_Entity_Part(part: SMDomainEntityPart) {
  }

  protected def package_variables_Powertype(powertype: SMDomainPowertype) {
  }

  protected def package_variables_Id(id: SMDomainValueId) {
    package_variables_Value(id)
  }

  protected def package_variables_Name(name: SMDomainValueName) {
    package_variables_Value(name)
  }

  protected def package_variables_Value(value: SMDomainValue) {
  }

  protected def package_variables_Document(document: SMDomainDocument) {
  }

  protected def package_variables_Rule(rule: SMDomainRule) {
  }

  protected def package_variables_Service(service: SMDomainService) {
  }

  val package_variables_platformmodel_visitor = new PObjectEntityFunction[Unit] {
    override protected def apply_EntityEntity(entity: PEntityEntity) {
      package_variables_EntityEntity(entity)
    }

    override protected def apply_PackageEntity(pkg: PPackageEntity) {
      package_variables_PackageEntity(pkg)
    }

    override protected def apply_ObjectEntity(pkg: PObjectEntity) {
    }
  }

  protected def package_variables_platformmodel {
    pContext.traversePlatform(package_variables_platformmodel_visitor)
/*
    val children = pobject.packageChildren
    if (!children.isEmpty) {
      val f = new PObjectEntityFunction[Unit] {
        override protected def apply_EntityEntity(entity: PEntityEntity) {
          package_variables_EntityEntity(entity)
        }

        override protected def apply_PackageEntity(pkg: PPackageEntity) {
          package_variables_PackageEntity(pkg)
        }

        override protected def apply_ObjectEntity(pkg: PObjectEntity) {
        }
      }
//      println(this)
      children.map(f)
    }
*/
  }

  protected def package_variables_EntityEntity(entity: PEntityEntity) {
  }

  protected def package_variables_PackageEntity(pkg: PPackageEntity) {
  }

  protected def package_variables_Extension {
  }

  /*
   * Lifecycle variables
   */
  protected def lifecycle_variables {
  }

  /*
   * Constructors
   */
  protected def constructors {
    constructors_null_constructor
    constructors_copy_constructor
    constructors_plain_constructor
    if (isDocumentOwner) {
      constructors_doc_constructor
    }
    constructors_auxiliary_constructors
  }

  protected def constructors_null_constructor {}

  protected def constructors_copy_constructor {}

  protected def constructors_plain_constructor {}

  protected def constructors_doc_constructor {}

  protected def constructors_auxiliary_constructors {}

  /*
   * Lifecycle methods
   */
  protected def lifecycle_methods {
    lifecycle_methods_init_method
    lifecycle_methods_open_method
    lifecycle_methods_close_method
  }

  protected def lifecycle_methods_init_method {}
  protected def lifecycle_methods_open_method {}
  protected def lifecycle_methods_close_method {}

  /*
   * Attribute methods
   */
  protected def attribute_methods {
    attribute_bean_methods
    attribute_as_methods // getFoo_asString
    if (!isImmutable) {
      attribute_by_methods
      attribute_with_methods // method chain (e.g. withTitle)
      attribute_updates
    }
  }

  protected def attribute_bean_methods {
    attribute_bean_methods_id
    attribute_bean_methods_plain
  }

  protected def attribute_bean_methods_id {
    if (isId && isRootObject) {
      attribute(idAttr).method_id
    }
  }

  protected def attribute_bean_methods_plain {
    for (attr <- effectiveNoIdAttributeDefinitions) {
      attr.method_bean
    }
  }

  protected def attribute_as_methods {
    for (attr <- effectiveNoIdAttributeDefinitions) {
      attr.method_as
    }
  }

  protected def attribute_by_methods {
    for (attr <- effectiveNoIdAttributeDefinitions) {
      if (attr.isSettable) {
        attr.method_by
      }
    }
  }

  protected def attribute_with_methods {
    for (attr <- effectiveNoIdAttributeDefinitions) {
      if (attr.isSettable) {
        attr.method_with
      }
    }
  }

  protected def attribute_updates {
    
  }

  /*
   * foo.setPerson()
   *      .withName("a").withAddress("b").build()
   *    .setGood()
   *      .withName("x").withPrice(123).build()
   *
   * foo.setPerson(new Person("a", "b"))
   * foo.setPerson(Person.create("a", "b"))
   * foo.setPerson(Person.build("a", "b").withAge(10).build())
   * foo.setPerson_json("{name: 'a', address: 'b'}")
   * foo.setPerson_xml("<person><name>a</name><address>b</address></person>")
   */

  /*
   * Service methods
   */
  protected def service_methods {
    for (op <- implementsOperationDefinitions) {
      op.method
    }
  }

  /*
   * convert to value methods
   */
  protected def to_methods {
    if (isData) {
      to_methods_string
      to_methods_xml
      to_methods_json
      to_methods_csv
      to_methods_yaml
      to_methods_feed
      to_methods_entry
      to_methods_Extension
    }
  }

  protected def to_methods_string {
  }

  protected def to_methods_xml {
  }

  protected def to_methods_json {
  }

  protected def to_methods_yaml {
  }

  protected def to_methods_csv {
  }

  protected def to_methods_map {
  }

  protected def to_methods_feed {
  }

  protected def to_methods_entry {
  }

  protected def to_methods_Extension {
  }

  /*
   * convert from value methods
   */
  protected def update_methods {
    if (isData && !isImmutable) {
      update_methods_string
      update_methods_xml
      update_methods_json
      update_methods_csv
      update_methods_yaml
      update_methods_urlencode
      update_methods_map
      update_methods_feed
      update_methods_entry
      update_methods_Extension
    }
  }

  protected def update_methods_string {
  }

  protected def update_methods_xml {
  }

  protected def update_methods_json {
  }

  protected def update_methods_csv {
  }

  protected def update_methods_yaml {
  }

  protected def update_methods_urlencode {
  }

  protected def update_methods_map {
  }

  protected def update_methods_feed {
  }

  protected def update_methods_entry {
  }

  protected def update_methods_Extension {
  }

  /*
   * 
   */
  protected def object_methods {
    if (isValueEquality) {
      object_methods_hashcode
      object_methods_equals
    }
  }

  protected def object_methods_hashcode {
  }

  protected def object_methods_equals {
  }

  /*
   * Document
   */
  protected def document_methods {
    document_methods_make
    document_methods_update
    document_methods_Extension
  }

  protected def document_methods_make {
  }

  protected def document_methods_update {
  }

  protected def document_methods_Extension {
  }

  /*
   * Persistent
   */
  protected def persistent_methods {
    persistent_methods_make_persistent_method
    persistent_methods_delete_persistent_method
    persistent_methods_get_entity_method
    persistent_methods_get_entities_method
    persistent_methods_delete_entity_method
    persistent_methods_entity_fill_method
    persistent_methods_entity_restore_method
  }

  protected def persistent_methods_make_persistent_method {
  }

  protected def persistent_methods_delete_persistent_method {
  }

  protected def persistent_methods_get_entity_method {
  }

  protected def persistent_methods_get_entities_method {
  }

  protected def persistent_methods_delete_entity_method {
  }

  protected def persistent_methods_entity_fill_method {
  }

  protected def persistent_methods_entity_restore_method {
  }

  /*
   * Package (module) scope methods compartment
   */
  protected def package_methods {
    package_methods_simplemodel
    package_methods_platformmodel
    package_methods_Extension
  }

  protected def package_methods_simplemodel {
    for (pkg <- pobject.modelPackage; child <- pkg.children) {
      child match {
        case tr: SMDomainTrait            => package_methods_Trait(tr)
        case actor: SMDomainActor         => package_methods_Actor(actor)
        case resource: SMDomainResource   => package_methods_Resource(resource)
        case event: SMDomainEvent         => package_methods_Event(event)
        case role: SMDomainRole           => package_methods_Role(role)
        case summary: SMDomainSummary     => package_methods_Summary(summary)
        case entity: SMDomainEntity       => package_methods_Entity(entity)
        case part: SMDomainEntityPart     => package_methods_Entity_Part(part)
        case id: SMDomainValueId          => package_methods_Id(id)
        case name: SMDomainValueName      => package_methods_Name(name)
        case value: SMDomainValue         => package_methods_Value(value)
        case powertype: SMDomainPowertype => package_methods_Powertype(powertype)
        case document: SMDomainDocument   => package_methods_Document(document)
        case rule: SMDomainRule           => package_methods_Rule(rule)
        case service: SMDomainService     => package_methods_Service(service)
        //        case port: SMDomainPort => package_methods_Port(port)
        //        case facade: SMDomainFacade => package_methods_Facade(facade)
        case datatype: SMDatatype         => {}
        case uc: SMStoryObject            => {}
        case be: SMBusinessEntity         => {}
        case pkg: SMPackage               => {}
        case unknown                      => error("Unspported simple model object = " + unknown)
      }
    }
  }

  protected def package_methods_Trait(tr: SMDomainTrait) {
  }

  protected def package_methods_Actor(actor: SMDomainActor) {
    package_methods_Entity(actor)
  }

  protected def package_methods_Resource(resource: SMDomainResource) {
    package_methods_Entity(resource)
  }

  protected def package_methods_Event(event: SMDomainEvent) {
    package_methods_Entity(event)
  }

  protected def package_methods_Role(role: SMDomainRole) {
    package_methods_Entity(role)
  }

  protected def package_methods_Summary(summary: SMDomainSummary) {
    package_methods_Entity(summary)
  }

  protected def package_methods_Entity(entity: SMDomainEntity) {
  }

  protected def package_methods_Entity_Part(part: SMDomainEntityPart) {
  }

  protected def package_methods_Powertype(powertype: SMDomainPowertype) {
  }

  protected def package_methods_Id(id: SMDomainValueId) {
    package_methods_Value(id)
  }

  protected def package_methods_Name(name: SMDomainValueName) {
    package_methods_Value(name)
  }

  protected def package_methods_Value(value: SMDomainValue) {
  }

  protected def package_methods_Document(document: SMDomainDocument) {
  }

  protected def package_methods_Rule(rule: SMDomainRule) {
  }

  protected def package_methods_Service(service: SMDomainService) {
  }

  val package_methods_platformmodel_visitor = new PObjectEntityFunction[Unit] {
    override protected def apply_EntityEntity(entity: PEntityEntity) {
      package_methods_platform_Entity(entity)
    }

    override protected def apply_PackageEntity(pkg: PPackageEntity) {
      package_methods_platform_Package(pkg)
    }

    override protected def apply_ObjectEntity(obj: PObjectEntity) {
    }
  }

  protected def package_methods_platformmodel {
    pContext.traversePlatform(package_methods_platformmodel_visitor)
/*    
    val children = pobject.packageChildren
    if (!children.isEmpty) {
      val f = new PObjectEntityFunction[Unit] {
        override protected def apply_EntityEntity(entity: PEntityEntity) {
          package_methods_platform_Entity(entity)
        }

        override protected def apply_PackageEntity(pkg: PPackageEntity) {
          package_methods_platform_Package(pkg)
        }

        override protected def apply_ObjectEntity(obj: PObjectEntity) {
        }
      }
      children.map(f)
    }
*/
  }

  protected def package_methods_platform_Entity(entity: PEntityEntity) {
  }

  protected def package_methods_platform_Package(pkg: PPackageEntity) {
  }

  protected def package_methods_Extension {
  }

  protected final def entities_in_module(): Seq[PEntityEntity] = {
    map_entities_in_module {
      case x: PEntityEntity => x
    }
  }

  protected final def map_entities_in_module[T](f: PEntityEntity => T): Seq[T] = {
    pContext.collectPlatform {
      case x: PEntityEntity => f(x)
    }
  }

  protected final def collect_entities_in_module[T](f: PartialFunction[PEntityEntity,T]): Seq[T] = {
    pContext.collectPlatform {
      case x: PEntityEntity => x
    } collect f
  }

  protected final def traverse_entities_in_module[T](f: PEntityEntity => T) {
    pContext.traversePlatform {
      case x: PEntityEntity => f(x)
    }
  }

/*
  protected final def package_children_map[T](f: PObjectEntity => T): Seq[T] = {
//    pobject.packageChildren.map(f)
  }

  protected final def package_children_collect[T](f: PartialFunction[PObjectEntity, T]): Seq[T] = {
//    pobject.packageChildren.collect(f)
  }

  protected final def package_children_entity_map[T](f: PEntityEntity => T): Seq[T] = {
//    pobject.packageChildren.collect {
//      case e: PEntityEntity => f(e)
//    }
  }
*/
  /*
   * auxiliaryiliary object compartment
   */
  protected def object_auxiliary {
  }

  /*
   * utility methods compartment 
   */

  protected def utilities {
  }

  /*
   * builder
   */
  protected def builder {
    if (useBuilder) {
      builder_copy_factory
      builder_new_factory
      builder_class
      builder_auxiliary
    }
  }

  protected def builder_copy_factory {
  }

  protected def builder_new_factory {
  }

  protected def builder_class {
  }

  protected def builder_auxiliary {
  }

  /*
   * Utility methods
   */
  protected def pln()

  protected final def label_name() = {
    pContext.labelName(pobject)
  }

  protected final def ascii_name() = {
    pContext.asciiName(pobject)
  }

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

  final protected def java_type(anAttr: PAttribute) = {
    anAttr.typeName
  }

  final protected def java_element_type(anAttr: PAttribute) = {
    anAttr.elementTypeName
  }

  final protected def persistent_type(anAttr: PAttribute) = {
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

/*
  protected final def is_settable(attr: PAttribute) = {
    // if (!attr.isId || attr.isId) {
    attr.kind match {
      case NullAttributeKind => true
      case IdAttributeKind => attr.idPolicy match {
        case SMAutoIdPolicy => false
        case SMUserIdPolicy => true
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
*/

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

  protected final def entity_ref_persistent_var_name(attr: PAttribute) = {
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
    "is_loaded_" + entity_ref_persistent_var_name(attr) 
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

  protected final def traverse(visitor: SimpleModelVisitor) { 
    for (pkg <- pobject.modelPackage; child <- pkg.children) {
      visitor.visit(child.content)
    }
  }

  /*
   * Used in NavigationStoreExtjsClassDefinition
   */
  protected final def actor_entities = {
    collect_entities_in_module {
      case x: PEntityEntity if (x.modelEntity.isInstanceOf[SMDomainActor]) => x
    }
  }

  protected final def role_entities = {
    collect_entities_in_module {
      case x: PEntityEntity if (x.modelEntity.isInstanceOf[SMDomainRole]) => x
    }
  }

  protected final def event_entities = {
    collect_entities_in_module {
      case x: PEntityEntity if (x.modelEntity.isInstanceOf[SMDomainEvent]) => x
    }
  }

  protected final def resource_entities = {
    collect_entities_in_module {
      case x: PEntityEntity if (x.modelEntity.isInstanceOf[SMDomainResource]) => x
    }
  }
}
