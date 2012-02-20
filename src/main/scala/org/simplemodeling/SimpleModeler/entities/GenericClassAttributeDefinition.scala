package org.simplemodeling.SimpleModeler.entities

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * Generic Class Attribute Definition
 * 
 * In Domain model, association has association types show below.
 *  
 * <dl>
 *   <dt>composition</dt>
 *   <dd></dd>
 *   <dt>aggregation</dt>
 *   <dd></dd>
 *   <dt>(plain) association</dt>
 *   <dd></dd>
 * </dl>
 * 
 * Primary attribute realizations:
 * 
 * <dl>
 *   <dt>plain reference</dt>
 *   <dd>holds whole object references</dd>
 *   <dt>id</dt>
 *   <dd>holds whole id values</dd>
 *   <dt>query</dt>
 *   <dd></dd>
 * </dl>
 * 
 * Realization vabialbe kinds:
 * 
 * <dl>
 *   <dt>primary variable for object references</dt>
 *   <dd></dd>
 *   <dt>primary variable for ids</dt>
 *   <dd></dd>
 *   <dt>auxility variable for object references</dt>
 *   <dd>"_association", "_part", "_powertype"</dd>
 *   <dt>auxility variable for ids</dt>
 *   <dd>"_id"</dd>
 *   <dt>title</dt>
 *   <dd>(e.g. powertype)</dd>
 *   <dt>cache timestamp</dt>
 *   <dd></dd>
 *   <dt>cache reference</dt>
 *   <dd>(XXX: auxility variable)</dd>
 *   <dt>update time</dt>
 *   <dd>(XXX: cache timestamp)</dd>
 * </dl>
 * 
 * 
 * @since   Jun. 20, 2011
 *  version Aug. 19, 2011
 * @version Feb. 19, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class GenericClassAttributeDefinition(
    val pContext: PEntityContext,
    val aspects: Seq[GenericAspect],
    val attr: PAttribute,
    val owner: GenericClassDefinition) {
  type ATTR_DEF <: GenericClassAttributeDefinition
  
  var isImmutable: Boolean = false // XXX attr.readonly
  def isInject = attr.inject
  var entityPersistentKind: EntityAttributeKind = {
    if (is_no_persistent) {
      SimpleEntityAttributeKind
    } else if (is_query_property) {
      QueryEntityAttributeKind
    } else if (is_id_property) {
      if (is_composition_property) { // typically composition
        CompositionIdEntityAttributeKind
      } else if (is_aggregation_property) { // typically aggregation
        AggregationIdEntityAttributeKind
      } else {
        AssociationIdEntityAttributeKind
      }
    } else if (is_id_reference_property) {
      if (is_composition_property) { // typically composition
        CompositionIdReferenceEntityAttributeKind
      } else if (is_aggregation_property) { // typically aggregation
        AggregationIdReferenceEntityAttributeKind
      } else {
        AssociationIdReferenceEntityAttributeKind
      }
    } else {
      if (is_composition_property) { // typically composition
        CompositionReferenceEntityAttributeKind
      } else if (is_aggregation_property) { // typically aggregation
        AggregationReferenceEntityAttributeKind
      } else {
        AssociationReferenceEntityAttributeKind
      }
    }
  }

  def withImmutable(value: Boolean): ATTR_DEF = {
    isImmutable = value
    this.asInstanceOf[ATTR_DEF]
  }

  /*
   * Utilities
   */
  //
  final protected def attr_name() = {
    pContext.attributeName(attr)
  }

  final protected def var_name() = {
    pContext.variableName(attr)
  }

  final protected def param_name() = {
    pContext.variableName(attr); // XXX
  }

  final protected def doc_attr_name() = {
    pContext.documentAttributeName(attr)
  }

  final protected def doc_var_name() = {
    pContext.documentVariableName(attr)
  }

  final protected def entity_ref_assoc_var_name() = {
    var_name + "_association"
  }

  final protected def entity_ref_part_var_name() = {
    var_name + "_part"
  }

  final protected def entity_ref_powertype_var_name() = {
    var_name + "_powertype"
  }

  //
  final protected def entity_ref_updated_var_name() = {
    var_name + "_updated"
  }

  final protected def entity_ref_label_var_name() = {
    var_name + "_label"
  }

  final protected def entity_ref_cache_var_name() = {
    var_name + "_cache"
  }

  final protected def entity_ref_cache_timestamp_var_name() = {
    var_name + "_cache_timestamp"
  }

  final protected def java_type() = {
    attr.typeName
  }

  final protected def java_element_type() = {
    attr.elementTypeName
  }

  final protected def persistent_type() = {
    attr.jdoTypeName
  }

  final protected def persistent_element_type() = {
    attr.jdoElementTypeName
  }

  final protected def java_doc_type() = {
    if (attr.isHasMany) {
      "List<" + java_doc_element_type() + ">"
    } else {
      java_doc_element_type()
    }
  }

  final protected def java_doc_element_type() = {
    attr.attributeType match {
      case p: PEntityPartType => {
        p.part.documentName
      }
      case p: PPowertypeType => "String"
      case _                 => java_element_type()
    }
  }

  def isSettable = is_settable

  protected final def is_settable(): Boolean = {
    if (isImmutable) {
      return false
    }
    if (attr.readonly) {
      return false
    }
    // if (!attr.isId || attr.isId) {
    attr.kind match {
      case NullAttributeKind => true
      case IdAttributeKind => attr.idPolicy match {
        case SMAutoIdPolicy => false
        case SMApplicationIdPolicy => true
      }
      case NameAttributeKind     => true
      case UserAttributeKind     => true
      case TitleAttributeKind    => true
      case SubTitleAttributeKind => true
      case SummaryAttributeKind  => true
      case CategoryAttributeKind => true
      case AuthorAttributeKind   => true
      case IconAttributeKind     => true
      case LogoAttributeKind     => true
      case LinkAttributeKind     => true
      case ContentAttributeKind  => true
      case CreatedAttributeKind  => false
      case UpdatedAttributeKind  => false
    }
  }

  protected final def is_logical_operation: Boolean = {
    //    modelEntity.appEngine.logical_operation
    error("not supported yet")
  }

  protected final def is_logical_operation(entityType: PEntityType) = {
    entityType.entity.modelEntity.appEngine.logical_operation
  }

  protected def is_no_persistent = {
    !owner.usePersistent
  }

  // app engine owned property
  // XXX devide app engine part
  protected def is_composition_property = {
    (attr.modelAssociation != null &&
      attr.modelAssociation.isComposition) || 
      (owner.modelEntityOption match {
        case Some(entity) => entity.appEngine.use_owned_property
        case None         => false
      })
  }

  protected def is_aggregation_property = {
    (attr.modelAssociation != null &&
      attr.modelAssociation.isAggregation)
  }

  protected def is_query_property = {
    attr.modelAssociation != null &&
      attr.modelAssociation.isQueryReference
  }

  protected def is_id_property = {
    false // XXX
  }

  protected def is_id_reference_property = {
    false // XXX
  }

  // unused
  protected final def entity_ref_persistent_var_name() = {
    if (is_composition_property) {
      var_name
    } else {
      attr.attributeType match {
        case e: PEntityType => var_name + "_id"
        case _              => var_name
      }
    }
  }

  // unused
  protected final def entity_ref_is_loaded_var_name() = {
    "is_loaded_" + entity_ref_persistent_var_name
  }

  // unused
  protected final def back_reference_var_name(): String = {
    //    back_reference_var_name(modelEntity, attr.modelAssociation)
    error("not supported yet")
  }

  // unused
  protected final def back_reference_var_name(source: SMObject, assoc: SMAssociation): String = {
    assoc.backReferenceNameOption match {
      case Some(name) => name
      //      case None => "_backref_%s_%s".format(gaejContext.termName(source), gaejContext.termName(assoc))
    }
  }

  //
  //
  //
  val attrName = attr_name
  val paramName = param_name
  val varName = var_name
  val erPersistentVarName = entity_ref_persistent_var_name
  val erAssocVarName = entity_ref_assoc_var_name
  val erPartVarName = entity_ref_part_var_name
  val erPowerVarName = entity_ref_powertype_var_name
  val erUpdatedVarName = entity_ref_updated_var_name
  val erCacheVarName = entity_ref_cache_var_name
  val erCacheTimestampVarName = entity_ref_cache_timestamp_var_name
  val javaType = java_type
  val javaElementType = java_element_type
  val propertyLiteral = attrName
  val propertyConstantName = "PROPERTY_" + pContext.constantName(attrName)
  val xmlElementName = attrName

  def isSystemType: Boolean = {
    attr.attributeType match {
      case e: PEntityType     => false
      case p: PEntityPartType => false
      case p: PPowertypeType  => false
      case d: PDocumentType   => false
      case v: PValueType      => false
      case _                  => true
    }
  }

  /*
   * Import
   */
  def head_imports {
    head_imports_Extension
  }

  protected def head_imports_Extension {
  }

  /*
   * Constants
   */
  def constant_property {
  }

  /*
   * Instance variables
   */
  def variable_id {
    val varName = var_name
    if (aspects.find(_.weaveIdAttributeSlot(attr, varName) == true).isDefined) {}
    else if (variable_id_Id(java_type, varName)) {}
    else variable_plain_Attribute_Instance_Variable(java_type, varName)
    aspects.foreach(_.weaveAttributeSlot(attr, varName))
  }

  protected def variable_id_Id(typeName: String, varName: String): Boolean = false

  def variable_plain {
    //      println("Attr %s, kind = %s".format(attr.name, attr.kind)) // 2009-10-28
    val varName = var_name
    aspects.foreach(_.weaveAttributeSlot(attr, varName))
    attr.kind match {
      case NullAttributeKind => {
        attr.attributeType match {
          case e: PEntityType     => variable_plain_entity_attribute(e)
          case p: PEntityPartType => variable_plain_entity_part_attribute(p)
          case p: PPowertypeType  => variable_plain_powertype_atttribute(p)
          case _                  => variable_plain_plain_attribute
        }
      }
      case IdAttributeKind       => {}
      /*
        case IdAttributeKind => {
          attr.idPolicy match {
            case SMAutoIdPolicy => key_long
            case SMApplicationIdPolicy => key_unencoded_string
          }
        }
*/
      case NameAttributeKind     => variable_plain_plain_attribute
      case UserAttributeKind     => variable_plain_plain_attribute
      case TitleAttributeKind    => variable_plain_plain_attribute
      case SubTitleAttributeKind => variable_plain_plain_attribute
      case SummaryAttributeKind  => variable_plain_plain_attribute
      case CategoryAttributeKind => variable_plain_plain_attribute
      case AuthorAttributeKind   => {}
      case IconAttributeKind     => variable_plain_plain_attribute
      case LogoAttributeKind     => variable_plain_plain_attribute
      case LinkAttributeKind     => variable_plain_plain_attribute
      case ContentAttributeKind  => variable_plain_plain_attribute
      case CreatedAttributeKind  => {}
      case UpdatedAttributeKind  => {}
    }
  }

  /*
   * layer 1
   */
  // plain
  protected def variable_plain_plain_attribute {
    if (attr.inject) {
      variable_plain_inject_annotation
    }
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable(persistent_element_type, varName);
  }

  // entity
  protected def variable_plain_entity_attribute(e: PEntityType) {
    entityPersistentKind {
      new EntityAttributeKindFunction[Unit] {
        def apply_Simple() = variable_plain_plain_attribute
        def apply_Composition_Reference() = variable_plain_entity_composition_reference_property(e)
        def apply_Aggregation_Reference() = variable_plain_entity_aggregation_reference_property(e)
        def apply_Association_Reference() = variable_plain_entity_association_reference_property(e)
        def apply_Composition_Id() = variable_plain_entity_composition_id_property(e)
        def apply_Aggregation_Id() = variable_plain_entity_aggregation_id_property(e)
        def apply_Association_Id() = variable_plain_entity_association_id_property(e)
        def apply_Composition_Id_Reference() = variable_plain_entity_composition_id_reference_property(e)
        def apply_Aggregation_Id_Reference() = variable_plain_entity_aggregation_id_reference_property(e)
        def apply_Association_Id_Reference() = variable_plain_entity_association_id_reference_property(e)
        def apply_Query() = variable_plain_entity_query_property(e)
      }
    }
  }

  // part
  protected def variable_plain_entity_part_attribute(p: PEntityPartType) {
    if (aspects.find(_.weavePartAttributeSlot(attr, erPartVarName)).isDefined) {}
    else variable_plain_Transient_Instance_Variable(java_type, erPartVarName);
  }

  protected def variable_plain_entity_part_attribute0(p: PEntityPartType) {
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable("Text", erPersistentVarName); // XXX Text google app engine
    variable_plain_not_persistent_annotation
    variable_plain_Transient_Instance_Variable(java_type, erPartVarName);
  }

  // powertype
  protected def variable_plain_powertype_atttribute(p: PPowertypeType) {
    if (aspects.find(_.weavePowertypeAttributeSlot(attr, varName)).isDefined) {}
    else  {
      variable_plain_Attribute_Instance_Variable(java_type, varName)
    }
  }

  protected def variable_plain_powertype_atttribute0(p: PPowertypeType) {
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable("String", erPersistentVarName);
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable("String", entity_ref_label_var_name)
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable("Date", entity_ref_updated_var_name)
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable("Date", entity_ref_cache_timestamp_var_name)
    variable_plain_not_persistent_annotation
    variable_plain_Transient_Instance_Variable(java_type, entity_ref_powertype_var_name)
  }

  protected def variable_plain_persistent_annotation {
    aspects.foreach(_.weavePersistentAnnotation(attr))
    variable_plain_Persistent_Annotation
  }

  protected def variable_plain_not_persistent_annotation {
    aspects.foreach(_.weaveNotPersistentAnnotation(attr))
    variable_plain_Not_Persistent_Annotation
  }

  protected def variable_plain_inject_annotation {
    variable_plain_Inject_Annotation
  }

  /*
   * layer 2
   */
  // Unused
  protected def variable_plain_entity_owned_property(entityType: PEntityType) {
    variable_plain_entity_owned_slot(entityType)
    variable_plain_entity_is_loaded_slot(entityType)
    //        entity_updated_slot(entityType)
    variable_plain_entity_object_slot(entityType)
    variable_plain_entity_cache_slot(entityType)
  }

  protected def variable_plain_entity_composition_reference_property(entityType: PEntityType) {
    variable_plain_entity_reference_slot(entityType)
  }

  protected def variable_plain_entity_aggregation_reference_property(entityType: PEntityType) {
    variable_plain_entity_reference_slot(entityType)
  }

  protected def variable_plain_entity_association_reference_property(entityType: PEntityType) {
    variable_plain_entity_reference_slot(entityType)
  }

  protected def variable_plain_entity_composition_id_property(entityType: PEntityType) {
    variable_plain_entity_id_property(entityType);
  }

  protected def variable_plain_entity_aggregation_id_property(entityType: PEntityType) {
    variable_plain_entity_id_property(entityType);
  }

  protected def variable_plain_entity_association_id_property(entityType: PEntityType) {
    variable_plain_entity_id_property(entityType);
  }

  protected def variable_plain_entity_composition_id_reference_property(entityType: PEntityType) {
    variable_plain_entity_id_with_association_property(entityType);
  }

  protected def variable_plain_entity_aggregation_id_reference_property(entityType: PEntityType) {
    variable_plain_entity_id_with_association_property(entityType);
  }

  protected def variable_plain_entity_association_id_reference_property(entityType: PEntityType) {
    variable_plain_entity_id_with_association_property(entityType);
  }

  protected def variable_plain_entity_query_property(entityType: PEntityType) {
    // no variables
  }

  protected def variable_plain_entity_id_property(entityType: PEntityType) {
    variable_plain_entity_id_slot(entityType)
  }

  protected def variable_plain_entity_id_with_association_property(entityType: PEntityType) {
    // XXX appengine
    entityType.entity.idPolicy match {
      case SMAutoIdPolicy => variable_plain_entity_key_long(entityType)
      case SMApplicationIdPolicy => variable_plain_entity_key_unencoded_string(entityType)
    }
  }

  protected def variable_plain_entity_key_long(entityType: PEntityType) {
    variable_plain_entity_id_slot(entityType)
    //        entity_updated_slot(entityType)
    variable_plain_entity_object_slot(entityType)
    variable_plain_entity_cache_slot(entityType)
  }

  protected def variable_plain_entity_key_unencoded_string(entityType: PEntityType) {
    variable_plain_entity_id_slot(entityType)
    //        entity_updated_slot(entityType)
    variable_plain_entity_object_slot(entityType)
    variable_plain_entity_cache_slot(entityType)
  }

  /*
   * layer 3
   */
  protected def variable_plain_entity_reference_slot(entityType: PEntityType) {
    val id_type = entityType.entity.idAttr.typeName
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable(id_type, varName)
  }

  protected def variable_plain_entity_id_slot(entityType: PEntityType) {
    val id_type = entityType.entity.idAttr.typeName
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable(id_type, varName)
  }

  protected def variable_plain_entity_is_loaded_slot(entityType: PEntityType) {
    val id_type = entityType.entity.idAttr.typeName
    variable_plain_not_persistent_annotation
    //      variable_plain_Attribute_Instance_Variable(entityType.entity.idAttr, "boolean", entity_ref_is_loaded_var_name) 
    variable_plain_Attribute_Instance_Variable("boolean", entity_ref_is_loaded_var_name)
  }

  protected def variable_plain_entity_owned_slot(entityType: PEntityType) {
    val refType = entityType.entity.name
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable(refType, varName) // XXX erPersistentVarName)
  }

  protected def variable_plain_entity_updated_slot(entityType: PEntityType) {
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable("Date", erUpdatedVarName)
  }

  protected def variable_plain_entity_object_slot(entityType: PEntityType) {
    variable_plain_not_persistent_annotation
    variable_plain_Transient_Instance_Variable(persistent_type, erAssocVarName);
  }

  protected def variable_plain_entity_cache_slot(entityType: PEntityType) {
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable("Date", erCacheTimestampVarName);
    variable_plain_persistent_annotation
    variable_plain_Attribute_Instance_Variable(java_doc_type, erCacheVarName);
  }

  /*
   * 
   */
  protected def variable_plain_Persistent_Annotation() {}
  protected def variable_plain_Not_Persistent_Annotation() {}
  protected def variable_plain_Inject_Annotation() {}

  protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String)
  protected def variable_plain_Transient_Instance_Variable(typename: String, varname: String)

  /*
   * 
   */
  def method_id {
    if (aspects.find(_.weaveIdMethods(attr, attrName, varName, paramName, javaType) == true).isDefined) {}
    else if (method_id_Id(attrName, varName, paramName, javaType)) {}
    else method_bean()
  }

  protected def method_id_Id(attrName: String, varName: String, paramName: String, javaType: String): Boolean = false

  def method_bean() {
    val attrName = attr.name;
    val paramName = attr.name;
    val varName = {
      attr.kind match {
        case NullAttributeKind     => attr.name
        case IdAttributeKind       => attr.name
        case NameAttributeKind     => attr.name
        case UserAttributeKind     => attr.name
        case TitleAttributeKind    => attr.name
        case SubTitleAttributeKind => attr.name
        case SummaryAttributeKind  => attr.name
        case CategoryAttributeKind => attr.name
        case AuthorAttributeKind   => "_entity_author"
        case IconAttributeKind     => attr.name
        case LogoAttributeKind     => attr.name
        case LinkAttributeKind     => attr.name
        case ContentAttributeKind  => attr.name
        case CreatedAttributeKind  => "_entity_created"
        case UpdatedAttributeKind  => "_entity_updated"
      }
    }
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_bean_multi_entity(e)
        case p: PEntityPartType => method_bean_multi_part(p)
        case p: PPowertypeType  => method_bean_multi_powertype(p)
        case v: PByteType       => method_bean_multi_byte
        case v: PIntegerType    => method_bean_multi_integer
        case v: PDecimalType    => method_bean_multi_decimal
        case _                  => method_bean_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_bean_single_entity(e)
        case p: PEntityPartType => method_bean_single_part(p)
        case p: PPowertypeType  => method_bean_single_powertype(p)
        case v: PByteType       => method_bean_single_byte
        case v: PIntegerType    => method_bean_single_integer
        case v: PDecimalType    => method_bean_single_decimal
        case _                  => method_bean_single_plain
      }
    }
  }

  protected def method_bean_single_plain()
  protected def method_bean_single_byte()
  protected def method_bean_single_integer()
  protected def method_bean_single_decimal()
  protected def method_bean_single_part(p: PEntityPartType)
  protected def method_bean_single_powertype(e: PPowertypeType)
  protected def method_bean_multi_plain()
  protected def method_bean_multi_byte()
  protected def method_bean_multi_integer()
  protected def method_bean_multi_decimal()
  protected def method_bean_multi_part(p: PEntityPartType)
  protected def method_bean_multi_powertype(e: PPowertypeType)

  protected def method_bean_single_entity(e: PEntityType) {
    entityPersistentKind {
      new EntityAttributeKindFunction[Unit] {
        def apply_Simple() = method_bean_single_entity_Simple(e)
        def apply_Composition_Reference() = method_bean_single_entity_Composition_Reference_Property(e)
        def apply_Aggregation_Reference() = method_bean_single_entity_Aggregation_Reference_Property(e)
        def apply_Association_Reference() = method_bean_single_entity_Association_Reference_Property(e)
        def apply_Composition_Id() = method_bean_single_entity_Composition_Id_Property(e)
        def apply_Aggregation_Id() = method_bean_single_entity_Aggregation_Id_Property(e)
        def apply_Association_Id() = method_bean_single_entity_Association_Id_Property(e)
        def apply_Composition_Id_Reference() = method_bean_single_entity_Composition_Id_Reference_Property(e)
        def apply_Aggregation_Id_Reference() = method_bean_single_entity_Aggregation_Id_Reference_Property(e)
        def apply_Association_Id_Reference() = method_bean_single_entity_Association_Id_Reference_Property(e)
        def apply_Query() = method_bean_single_entity_Query_Property(e)
      }
    }
  }

  def method_bean_single_entity_Simple(e: PEntityType)
  def method_bean_single_entity_Composition_Reference_Property(e: PEntityType)
  def method_bean_single_entity_Aggregation_Reference_Property(e: PEntityType)
  def method_bean_single_entity_Association_Reference_Property(e: PEntityType)
  def method_bean_single_entity_Composition_Id_Property(e: PEntityType)
  def method_bean_single_entity_Aggregation_Id_Property(e: PEntityType)
  def method_bean_single_entity_Association_Id_Property(e: PEntityType)
  def method_bean_single_entity_Composition_Id_Reference_Property(e: PEntityType)
  def method_bean_single_entity_Aggregation_Id_Reference_Property(e: PEntityType)
  def method_bean_single_entity_Association_Id_Reference_Property(e: PEntityType)
  def method_bean_single_entity_Query_Property(e: PEntityType)

  protected def method_bean_multi_entity(e: PEntityType) {
    entityPersistentKind {
      new EntityAttributeKindFunction[Unit] {
        def apply_Simple() = method_bean_multi_entity_Simple(e)
        def apply_Composition_Reference() = method_bean_multi_entity_Composition_Reference_Property(e)
        def apply_Aggregation_Reference() = method_bean_multi_entity_Aggregation_Reference_Property(e)
        def apply_Association_Reference() = method_bean_multi_entity_Association_Reference_Property(e)
        def apply_Composition_Id() = method_bean_multi_entity_Composition_Id_Property(e)
        def apply_Aggregation_Id() = method_bean_multi_entity_Aggregation_Id_Property(e)
        def apply_Association_Id() = method_bean_multi_entity_Association_Id_Property(e)
        def apply_Composition_Id_Reference() = method_bean_multi_entity_Composition_Id_Reference_Property(e)
        def apply_Aggregation_Id_Reference() = method_bean_multi_entity_Aggregation_Id_Reference_Property(e)
        def apply_Association_Id_Reference() = method_bean_multi_entity_Association_Id_Reference_Property(e)
        def apply_Query() = method_bean_multi_entity_Query_Property(e)
      }
    }
  }

  def method_bean_multi_entity_Simple(e: PEntityType)
  def method_bean_multi_entity_Composition_Reference_Property(e: PEntityType)
  def method_bean_multi_entity_Aggregation_Reference_Property(e: PEntityType)
  def method_bean_multi_entity_Association_Reference_Property(e: PEntityType)
  def method_bean_multi_entity_Composition_Id_Property(e: PEntityType)
  def method_bean_multi_entity_Aggregation_Id_Property(e: PEntityType)
  def method_bean_multi_entity_Association_Id_Property(e: PEntityType)
  def method_bean_multi_entity_Composition_Id_Reference_Property(e: PEntityType)
  def method_bean_multi_entity_Aggregation_Id_Reference_Property(e: PEntityType)
  def method_bean_multi_entity_Association_Id_Reference_Property(e: PEntityType)
  def method_bean_multi_entity_Query_Property(e: PEntityType)

  /*
   * method_as
   */
  def method_as {
    method_as_string
    method_as_xml
    method_as_json
    method_as_yaml
    method_as_csv
    method_as_urlencode
  }

  def method_as_string {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_as_string_multi_entity(e)
        case p: PEntityPartType => method_as_string_multi_part(p)
        case p: PPowertypeType  => method_as_string_multi_powertype(p)
        case v: PByteType       => method_as_string_multi_byte
        case v: PIntegerType    => method_as_string_multi_integer
        case v: PDecimalType    => method_as_string_multi_decimal
        case _                  => method_as_string_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_as_string_single_entity(e)
        case p: PEntityPartType => method_as_string_single_part(p)
        case p: PPowertypeType  => method_as_string_single_powertype(p)
        case v: PByteType       => method_as_string_single_byte
        case v: PIntegerType    => method_as_string_single_integer
        case v: PDecimalType    => method_as_string_single_decimal
        case _                  => method_as_string_single_plain
      }
    }
  }

  def method_as_string_multi_entity(e: PEntityType) {

  }

  def method_as_string_multi_part(e: PEntityPartType) {

  }

  def method_as_string_multi_powertype(e: PPowertypeType) {

  }

  def method_as_string_multi_byte {

  }

  def method_as_string_multi_integer {

  }

  def method_as_string_multi_decimal {

  }

  def method_as_string_multi_plain {

  }

  def method_as_string_single_entity(e: PEntityType) {

  }

  def method_as_string_single_part(e: PEntityPartType) {

  }

  def method_as_string_single_powertype(e: PPowertypeType) {

  }

  def method_as_string_single_byte {

  }

  def method_as_string_single_integer {

  }

  def method_as_string_single_decimal {

  }

  def method_as_string_single_plain {

  }

  def method_as_xml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_as_xml_multi_entity(e)
        case p: PEntityPartType => method_as_xml_multi_part(p)
        case p: PPowertypeType  => method_as_xml_multi_powertype(p)
        case v: PByteType       => method_as_xml_multi_byte
        case v: PIntegerType    => method_as_xml_multi_integer
        case v: PDecimalType    => method_as_xml_multi_decimal
        case _                  => method_as_xml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_as_xml_single_entity(e)
        case p: PEntityPartType => method_as_xml_single_part(p)
        case p: PPowertypeType  => method_as_xml_single_powertype(p)
        case v: PByteType       => method_as_xml_single_byte
        case v: PIntegerType    => method_as_xml_single_integer
        case v: PDecimalType    => method_as_xml_single_decimal
        case _                  => method_as_xml_single_plain
      }
    }

  }

  def method_as_xml_multi_entity(e: PEntityType) {

  }

  def method_as_xml_multi_part(e: PEntityPartType) {

  }

  def method_as_xml_multi_powertype(e: PPowertypeType) {

  }

  def method_as_xml_multi_byte {

  }

  def method_as_xml_multi_integer {

  }

  def method_as_xml_multi_decimal {

  }

  def method_as_xml_multi_plain {

  }

  def method_as_xml_single_entity(e: PEntityType) {

  }

  def method_as_xml_single_part(e: PEntityPartType) {

  }

  def method_as_xml_single_powertype(e: PPowertypeType) {

  }

  def method_as_xml_single_byte {

  }

  def method_as_xml_single_integer {

  }

  def method_as_xml_single_decimal {

  }

  def method_as_xml_single_plain {

  }

  def method_as_json {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_as_json_multi_entity(e)
        case p: PEntityPartType => method_as_json_multi_part(p)
        case p: PPowertypeType  => method_as_json_multi_powertype(p)
        case v: PByteType       => method_as_json_multi_byte
        case v: PIntegerType    => method_as_json_multi_integer
        case v: PDecimalType    => method_as_json_multi_decimal
        case _                  => method_as_json_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_as_json_single_entity(e)
        case p: PEntityPartType => method_as_json_single_part(p)
        case p: PPowertypeType  => method_as_json_single_powertype(p)
        case v: PByteType       => method_as_json_single_byte
        case v: PIntegerType    => method_as_json_single_integer
        case v: PDecimalType    => method_as_json_single_decimal
        case _                  => method_as_json_single_plain
      }
    }

  }

  def method_as_json_multi_entity(e: PEntityType) {

  }

  def method_as_json_multi_part(e: PEntityPartType) {

  }

  def method_as_json_multi_powertype(e: PPowertypeType) {

  }

  def method_as_json_multi_byte {

  }

  def method_as_json_multi_integer {

  }

  def method_as_json_multi_decimal {

  }

  def method_as_json_multi_plain {

  }

  def method_as_json_single_entity(e: PEntityType) {

  }

  def method_as_json_single_part(e: PEntityPartType) {

  }

  def method_as_json_single_powertype(e: PPowertypeType) {

  }

  def method_as_json_single_byte {

  }

  def method_as_json_single_integer {

  }

  def method_as_json_single_decimal {

  }

  def method_as_json_single_plain {

  }

  def method_as_yaml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_as_yaml_multi_entity(e)
        case p: PEntityPartType => method_as_yaml_multi_part(p)
        case p: PPowertypeType  => method_as_yaml_multi_powertype(p)
        case v: PByteType       => method_as_yaml_multi_byte
        case v: PIntegerType    => method_as_yaml_multi_integer
        case v: PDecimalType    => method_as_yaml_multi_decimal
        case _                  => method_as_yaml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_as_yaml_single_entity(e)
        case p: PEntityPartType => method_as_yaml_single_part(p)
        case p: PPowertypeType  => method_as_yaml_single_powertype(p)
        case v: PByteType       => method_as_yaml_single_byte
        case v: PIntegerType    => method_as_yaml_single_integer
        case v: PDecimalType    => method_as_yaml_single_decimal
        case _                  => method_as_yaml_single_plain
      }
    }
  }

  def method_as_yaml_multi_entity(e: PEntityType) {

  }

  def method_as_yaml_multi_part(e: PEntityPartType) {

  }

  def method_as_yaml_multi_powertype(e: PPowertypeType) {

  }

  def method_as_yaml_multi_byte {

  }

  def method_as_yaml_multi_integer {

  }

  def method_as_yaml_multi_decimal {

  }

  def method_as_yaml_multi_plain {

  }

  def method_as_yaml_single_entity(e: PEntityType) {

  }

  def method_as_yaml_single_part(e: PEntityPartType) {

  }

  def method_as_yaml_single_powertype(e: PPowertypeType) {

  }

  def method_as_yaml_single_byte {

  }

  def method_as_yaml_single_integer {

  }

  def method_as_yaml_single_decimal {

  }

  def method_as_yaml_single_plain {

  }

  def method_as_csv {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_as_csv_multi_entity(e)
        case p: PEntityPartType => method_as_csv_multi_part(p)
        case p: PPowertypeType  => method_as_csv_multi_powertype(p)
        case v: PByteType       => method_as_csv_multi_byte
        case v: PIntegerType    => method_as_csv_multi_integer
        case v: PDecimalType    => method_as_csv_multi_decimal
        case _                  => method_as_csv_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_as_csv_single_entity(e)
        case p: PEntityPartType => method_as_csv_single_part(p)
        case p: PPowertypeType  => method_as_csv_single_powertype(p)
        case v: PByteType       => method_as_csv_single_byte
        case v: PIntegerType    => method_as_csv_single_integer
        case v: PDecimalType    => method_as_csv_single_decimal
        case _                  => method_as_csv_single_plain
      }
    }

  }

  def method_as_csv_multi_entity(e: PEntityType) {

  }

  def method_as_csv_multi_part(e: PEntityPartType) {

  }

  def method_as_csv_multi_powertype(e: PPowertypeType) {

  }

  def method_as_csv_multi_byte {

  }

  def method_as_csv_multi_integer {

  }

  def method_as_csv_multi_decimal {

  }

  def method_as_csv_multi_plain {

  }

  def method_as_csv_single_entity(e: PEntityType) {

  }

  def method_as_csv_single_part(e: PEntityPartType) {

  }

  def method_as_csv_single_powertype(e: PPowertypeType) {

  }

  def method_as_csv_single_byte {

  }

  def method_as_csv_single_integer {

  }

  def method_as_csv_single_decimal {

  }

  def method_as_csv_single_plain {

  }

  def method_as_urlencode {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_as_urlencode_multi_entity(e)
        case p: PEntityPartType => method_as_urlencode_multi_part(p)
        case p: PPowertypeType  => method_as_urlencode_multi_powertype(p)
        case v: PByteType       => method_as_urlencode_multi_byte
        case v: PIntegerType    => method_as_urlencode_multi_integer
        case v: PDecimalType    => method_as_urlencode_multi_decimal
        case _                  => method_as_urlencode_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_as_urlencode_single_entity(e)
        case p: PEntityPartType => method_as_urlencode_single_part(p)
        case p: PPowertypeType  => method_as_urlencode_single_powertype(p)
        case v: PByteType       => method_as_urlencode_single_byte
        case v: PIntegerType    => method_as_urlencode_single_integer
        case v: PDecimalType    => method_as_urlencode_single_decimal
        case _                  => method_as_urlencode_single_plain
      }
    }

  }

  def method_as_urlencode_multi_entity(e: PEntityType) {

  }

  def method_as_urlencode_multi_part(e: PEntityPartType) {

  }

  def method_as_urlencode_multi_powertype(e: PPowertypeType) {

  }

  def method_as_urlencode_multi_byte {

  }

  def method_as_urlencode_multi_integer {

  }

  def method_as_urlencode_multi_decimal {

  }

  def method_as_urlencode_multi_plain {

  }

  def method_as_urlencode_single_entity(e: PEntityType) {

  }

  def method_as_urlencode_single_part(e: PEntityPartType) {

  }

  def method_as_urlencode_single_powertype(e: PPowertypeType) {

  }

  def method_as_urlencode_single_byte {

  }

  def method_as_urlencode_single_integer {

  }

  def method_as_urlencode_single_decimal {

  }

  def method_as_urlencode_single_plain {

  }

  /*
   * method_by
   */
  def method_by {
    method_by_string
    method_by_xml
    method_by_json
    method_by_yaml
    method_by_csv
    method_by_urlencode
  }

  def method_by_string {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_by_string_multi_entity(e)
        case p: PEntityPartType => method_by_string_multi_part(p)
        case p: PPowertypeType  => method_by_string_multi_powertype(p)
        case v: PByteType       => method_by_string_multi_byte
        case v: PIntegerType    => method_by_string_multi_integer
        case v: PDecimalType    => method_by_string_multi_decimal
        case _                  => method_by_string_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_by_string_single_entity(e)
        case p: PEntityPartType => method_by_string_single_part(p)
        case p: PPowertypeType  => method_by_string_single_powertype(p)
        case v: PByteType       => method_by_string_single_byte
        case v: PIntegerType    => method_by_string_single_integer
        case v: PDecimalType    => method_by_string_single_decimal
        case _                  => method_by_string_single_plain
      }
    }

  }

  def method_by_string_multi_entity(e: PEntityType) {

  }

  def method_by_string_multi_part(e: PEntityPartType) {

  }

  def method_by_string_multi_powertype(e: PPowertypeType) {

  }

  def method_by_string_multi_byte {

  }

  def method_by_string_multi_integer {

  }

  def method_by_string_multi_decimal {

  }

  def method_by_string_multi_plain {

  }

  def method_by_string_single_entity(e: PEntityType) {

  }

  def method_by_string_single_part(e: PEntityPartType) {

  }

  def method_by_string_single_powertype(e: PPowertypeType) {

  }

  def method_by_string_single_byte {

  }

  def method_by_string_single_integer {

  }

  def method_by_string_single_decimal {

  }

  def method_by_string_single_plain {

  }

  def method_by_xml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_by_xml_multi_entity(e)
        case p: PEntityPartType => method_by_xml_multi_part(p)
        case p: PPowertypeType  => method_by_xml_multi_powertype(p)
        case v: PByteType       => method_by_xml_multi_byte
        case v: PIntegerType    => method_by_xml_multi_integer
        case v: PDecimalType    => method_by_xml_multi_decimal
        case _                  => method_by_xml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_by_xml_single_entity(e)
        case p: PEntityPartType => method_by_xml_single_part(p)
        case p: PPowertypeType  => method_by_xml_single_powertype(p)
        case v: PByteType       => method_by_xml_single_byte
        case v: PIntegerType    => method_by_xml_single_integer
        case v: PDecimalType    => method_by_xml_single_decimal
        case _                  => method_by_xml_single_plain
      }
    }

  }

  def method_by_xml_multi_entity(e: PEntityType) {

  }

  def method_by_xml_multi_part(e: PEntityPartType) {

  }

  def method_by_xml_multi_powertype(e: PPowertypeType) {

  }

  def method_by_xml_multi_byte {

  }

  def method_by_xml_multi_integer {

  }

  def method_by_xml_multi_decimal {

  }

  def method_by_xml_multi_plain {

  }

  def method_by_xml_single_entity(e: PEntityType) {

  }

  def method_by_xml_single_part(e: PEntityPartType) {

  }

  def method_by_xml_single_powertype(e: PPowertypeType) {

  }

  def method_by_xml_single_byte {

  }

  def method_by_xml_single_integer {

  }

  def method_by_xml_single_decimal {

  }

  def method_by_xml_single_plain {

  }

  def method_by_json {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_by_json_multi_entity(e)
        case p: PEntityPartType => method_by_json_multi_part(p)
        case p: PPowertypeType  => method_by_json_multi_powertype(p)
        case v: PByteType       => method_by_json_multi_byte
        case v: PIntegerType    => method_by_json_multi_integer
        case v: PDecimalType    => method_by_json_multi_decimal
        case _                  => method_by_json_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_by_json_single_entity(e)
        case p: PEntityPartType => method_by_json_single_part(p)
        case p: PPowertypeType  => method_by_json_single_powertype(p)
        case v: PByteType       => method_by_json_single_byte
        case v: PIntegerType    => method_by_json_single_integer
        case v: PDecimalType    => method_by_json_single_decimal
        case _                  => method_by_json_single_plain
      }
    }

  }

  def method_by_json_multi_entity(e: PEntityType) {

  }

  def method_by_json_multi_part(e: PEntityPartType) {

  }

  def method_by_json_multi_powertype(e: PPowertypeType) {

  }

  def method_by_json_multi_byte {

  }

  def method_by_json_multi_integer {

  }

  def method_by_json_multi_decimal {

  }

  def method_by_json_multi_plain {

  }

  def method_by_json_single_entity(e: PEntityType) {

  }

  def method_by_json_single_part(e: PEntityPartType) {

  }

  def method_by_json_single_powertype(e: PPowertypeType) {

  }

  def method_by_json_single_byte {

  }

  def method_by_json_single_integer {

  }

  def method_by_json_single_decimal {

  }

  def method_by_json_single_plain {

  }

  def method_by_yaml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_by_yaml_multi_entity(e)
        case p: PEntityPartType => method_by_yaml_multi_part(p)
        case p: PPowertypeType  => method_by_yaml_multi_powertype(p)
        case v: PByteType       => method_by_yaml_multi_byte
        case v: PIntegerType    => method_by_yaml_multi_integer
        case v: PDecimalType    => method_by_yaml_multi_decimal
        case _                  => method_by_yaml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_by_yaml_single_entity(e)
        case p: PEntityPartType => method_by_yaml_single_part(p)
        case p: PPowertypeType  => method_by_yaml_single_powertype(p)
        case v: PByteType       => method_by_yaml_single_byte
        case v: PIntegerType    => method_by_yaml_single_integer
        case v: PDecimalType    => method_by_yaml_single_decimal
        case _                  => method_by_yaml_single_plain
      }
    }

  }

  def method_by_yaml_multi_entity(e: PEntityType) {

  }

  def method_by_yaml_multi_part(e: PEntityPartType) {

  }

  def method_by_yaml_multi_powertype(e: PPowertypeType) {

  }

  def method_by_yaml_multi_byte {

  }

  def method_by_yaml_multi_integer {

  }

  def method_by_yaml_multi_decimal {

  }

  def method_by_yaml_multi_plain {

  }

  def method_by_yaml_single_entity(e: PEntityType) {

  }

  def method_by_yaml_single_part(e: PEntityPartType) {

  }

  def method_by_yaml_single_powertype(e: PPowertypeType) {

  }

  def method_by_yaml_single_byte {

  }

  def method_by_yaml_single_integer {

  }

  def method_by_yaml_single_decimal {

  }

  def method_by_yaml_single_plain {

  }

  def method_by_csv {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_by_csv_multi_entity(e)
        case p: PEntityPartType => method_by_csv_multi_part(p)
        case p: PPowertypeType  => method_by_csv_multi_powertype(p)
        case v: PByteType       => method_by_csv_multi_byte
        case v: PIntegerType    => method_by_csv_multi_integer
        case v: PDecimalType    => method_by_csv_multi_decimal
        case _                  => method_by_csv_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_by_csv_single_entity(e)
        case p: PEntityPartType => method_by_csv_single_part(p)
        case p: PPowertypeType  => method_by_csv_single_powertype(p)
        case v: PByteType       => method_by_csv_single_byte
        case v: PIntegerType    => method_by_csv_single_integer
        case v: PDecimalType    => method_by_csv_single_decimal
        case _                  => method_by_csv_single_plain
      }
    }

  }

  def method_by_csv_multi_entity(e: PEntityType) {

  }

  def method_by_csv_multi_part(e: PEntityPartType) {

  }

  def method_by_csv_multi_powertype(e: PPowertypeType) {

  }

  def method_by_csv_multi_byte {

  }

  def method_by_csv_multi_integer {

  }

  def method_by_csv_multi_decimal {

  }

  def method_by_csv_multi_plain {

  }

  def method_by_csv_single_entity(e: PEntityType) {

  }

  def method_by_csv_single_part(e: PEntityPartType) {

  }

  def method_by_csv_single_powertype(e: PPowertypeType) {

  }

  def method_by_csv_single_byte {

  }

  def method_by_csv_single_integer {

  }

  def method_by_csv_single_decimal {

  }

  def method_by_csv_single_plain {

  }

  def method_by_urlencode {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_by_urlencode_multi_entity(e)
        case p: PEntityPartType => method_by_urlencode_multi_part(p)
        case p: PPowertypeType  => method_by_urlencode_multi_powertype(p)
        case v: PByteType       => method_by_urlencode_multi_byte
        case v: PIntegerType    => method_by_urlencode_multi_integer
        case v: PDecimalType    => method_by_urlencode_multi_decimal
        case _                  => method_by_urlencode_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_by_urlencode_single_entity(e)
        case p: PEntityPartType => method_by_urlencode_single_part(p)
        case p: PPowertypeType  => method_by_urlencode_single_powertype(p)
        case v: PByteType       => method_by_urlencode_single_byte
        case v: PIntegerType    => method_by_urlencode_single_integer
        case v: PDecimalType    => method_by_urlencode_single_decimal
        case _                  => method_by_urlencode_single_plain
      }
    }

  }

  def method_by_urlencode_multi_entity(e: PEntityType) {

  }

  def method_by_urlencode_multi_part(e: PEntityPartType) {

  }

  def method_by_urlencode_multi_powertype(e: PPowertypeType) {

  }

  def method_by_urlencode_multi_byte {

  }

  def method_by_urlencode_multi_integer {

  }

  def method_by_urlencode_multi_decimal {

  }

  def method_by_urlencode_multi_plain {

  }

  def method_by_urlencode_single_entity(e: PEntityType) {

  }

  def method_by_urlencode_single_part(e: PEntityPartType) {

  }

  def method_by_urlencode_single_powertype(e: PPowertypeType) {

  }

  def method_by_urlencode_single_byte {

  }

  def method_by_urlencode_single_integer {

  }

  def method_by_urlencode_single_decimal {

  }

  def method_by_urlencode_single_plain {

  }

  /*
   * method_with
   */
  def method_with {
    method_with_plain
    method_with_string
    method_with_xml
    method_with_json
    method_with_yaml
    method_with_csv
    method_with_urlencode
  }

  def method_with_plain {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_with_plain_multi_entity(e)
        case p: PEntityPartType => method_with_plain_multi_part(p)
        case p: PPowertypeType  => method_with_plain_multi_powertype(p)
        case v: PByteType       => method_with_plain_multi_byte
        case v: PIntegerType    => method_with_plain_multi_integer
        case v: PDecimalType    => method_with_plain_multi_decimal
        case _                  => method_with_plain_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_with_plain_single_entity(e)
        case p: PEntityPartType => method_with_plain_single_part(p)
        case p: PPowertypeType  => method_with_plain_single_powertype(p)
        case v: PByteType       => method_with_plain_single_byte
        case v: PIntegerType    => method_with_plain_single_integer
        case v: PDecimalType    => method_with_plain_single_decimal
        case _                  => method_with_plain_single_plain
      }
    }

  }

  def method_with_plain_multi_entity(e: PEntityType) {

  }

  def method_with_plain_multi_part(e: PEntityPartType) {

  }

  def method_with_plain_multi_powertype(e: PPowertypeType) {

  }

  def method_with_plain_multi_byte {

  }

  def method_with_plain_multi_integer {

  }

  def method_with_plain_multi_decimal {

  }

  def method_with_plain_multi_plain {

  }

  def method_with_plain_single_entity(e: PEntityType) {

  }

  def method_with_plain_single_part(e: PEntityPartType) {

  }

  def method_with_plain_single_powertype(e: PPowertypeType) {

  }

  def method_with_plain_single_byte {

  }

  def method_with_plain_single_integer {

  }

  def method_with_plain_single_decimal {

  }

  def method_with_plain_single_plain {

  }

  def method_with_string {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_with_string_multi_entity(e)
        case p: PEntityPartType => method_with_string_multi_part(p)
        case p: PPowertypeType  => method_with_string_multi_powertype(p)
        case v: PByteType       => method_with_string_multi_byte
        case v: PIntegerType    => method_with_string_multi_integer
        case v: PDecimalType    => method_with_string_multi_decimal
        case _                  => method_with_string_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_with_string_single_entity(e)
        case p: PEntityPartType => method_with_string_single_part(p)
        case p: PPowertypeType  => method_with_string_single_powertype(p)
        case v: PByteType       => method_with_string_single_byte
        case v: PIntegerType    => method_with_string_single_integer
        case v: PDecimalType    => method_with_string_single_decimal
        case _                  => method_with_string_single_plain
      }
    }

  }

  def method_with_string_multi_entity(e: PEntityType) {

  }

  def method_with_string_multi_part(e: PEntityPartType) {

  }

  def method_with_string_multi_powertype(e: PPowertypeType) {

  }

  def method_with_string_multi_byte {

  }

  def method_with_string_multi_integer {

  }

  def method_with_string_multi_decimal {

  }

  def method_with_string_multi_plain {

  }

  def method_with_string_single_entity(e: PEntityType) {

  }

  def method_with_string_single_part(e: PEntityPartType) {

  }

  def method_with_string_single_powertype(e: PPowertypeType) {

  }

  def method_with_string_single_byte {

  }

  def method_with_string_single_integer {

  }

  def method_with_string_single_decimal {

  }

  def method_with_string_single_plain {

  }

  def method_with_xml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_with_xml_multi_entity(e)
        case p: PEntityPartType => method_with_xml_multi_part(p)
        case p: PPowertypeType  => method_with_xml_multi_powertype(p)
        case v: PByteType       => method_with_xml_multi_byte
        case v: PIntegerType    => method_with_xml_multi_integer
        case v: PDecimalType    => method_with_xml_multi_decimal
        case _                  => method_with_xml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_with_xml_single_entity(e)
        case p: PEntityPartType => method_with_xml_single_part(p)
        case p: PPowertypeType  => method_with_xml_single_powertype(p)
        case v: PByteType       => method_with_xml_single_byte
        case v: PIntegerType    => method_with_xml_single_integer
        case v: PDecimalType    => method_with_xml_single_decimal
        case _                  => method_with_xml_single_plain
      }
    }

  }

  def method_with_xml_multi_entity(e: PEntityType) {

  }

  def method_with_xml_multi_part(e: PEntityPartType) {

  }

  def method_with_xml_multi_powertype(e: PPowertypeType) {

  }

  def method_with_xml_multi_byte {

  }

  def method_with_xml_multi_integer {

  }

  def method_with_xml_multi_decimal {

  }

  def method_with_xml_multi_plain {

  }

  def method_with_xml_single_entity(e: PEntityType) {

  }

  def method_with_xml_single_part(e: PEntityPartType) {

  }

  def method_with_xml_single_powertype(e: PPowertypeType) {

  }

  def method_with_xml_single_byte {

  }

  def method_with_xml_single_integer {

  }

  def method_with_xml_single_decimal {

  }

  def method_with_xml_single_plain {

  }

  def method_with_json {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_with_json_multi_entity(e)
        case p: PEntityPartType => method_with_json_multi_part(p)
        case p: PPowertypeType  => method_with_json_multi_powertype(p)
        case v: PByteType       => method_with_json_multi_byte
        case v: PIntegerType    => method_with_json_multi_integer
        case v: PDecimalType    => method_with_json_multi_decimal
        case _                  => method_with_json_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_with_json_single_entity(e)
        case p: PEntityPartType => method_with_json_single_part(p)
        case p: PPowertypeType  => method_with_json_single_powertype(p)
        case v: PByteType       => method_with_json_single_byte
        case v: PIntegerType    => method_with_json_single_integer
        case v: PDecimalType    => method_with_json_single_decimal
        case _                  => method_with_json_single_plain
      }
    }
  }

  def method_with_json_multi_entity(e: PEntityType) {

  }

  def method_with_json_multi_part(e: PEntityPartType) {

  }

  def method_with_json_multi_powertype(e: PPowertypeType) {

  }

  def method_with_json_multi_byte {

  }

  def method_with_json_multi_integer {

  }

  def method_with_json_multi_decimal {

  }

  def method_with_json_multi_plain {

  }

  def method_with_json_single_entity(e: PEntityType) {

  }

  def method_with_json_single_part(e: PEntityPartType) {

  }

  def method_with_json_single_powertype(e: PPowertypeType) {

  }

  def method_with_json_single_byte {

  }

  def method_with_json_single_integer {

  }

  def method_with_json_single_decimal {

  }

  def method_with_json_single_plain {

  }
  def method_with_yaml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_with_yaml_multi_entity(e)
        case p: PEntityPartType => method_with_yaml_multi_part(p)
        case p: PPowertypeType  => method_with_yaml_multi_powertype(p)
        case v: PByteType       => method_with_yaml_multi_byte
        case v: PIntegerType    => method_with_yaml_multi_integer
        case v: PDecimalType    => method_with_yaml_multi_decimal
        case _                  => method_with_yaml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_with_yaml_single_entity(e)
        case p: PEntityPartType => method_with_yaml_single_part(p)
        case p: PPowertypeType  => method_with_yaml_single_powertype(p)
        case v: PByteType       => method_with_yaml_single_byte
        case v: PIntegerType    => method_with_yaml_single_integer
        case v: PDecimalType    => method_with_yaml_single_decimal
        case _                  => method_with_yaml_single_plain
      }
    }
  }

  def method_with_yaml_multi_entity(e: PEntityType) {

  }

  def method_with_yaml_multi_part(e: PEntityPartType) {

  }

  def method_with_yaml_multi_powertype(e: PPowertypeType) {

  }

  def method_with_yaml_multi_byte {

  }

  def method_with_yaml_multi_integer {

  }

  def method_with_yaml_multi_decimal {

  }

  def method_with_yaml_multi_plain {

  }

  def method_with_yaml_single_entity(e: PEntityType) {

  }

  def method_with_yaml_single_part(e: PEntityPartType) {

  }

  def method_with_yaml_single_powertype(e: PPowertypeType) {

  }

  def method_with_yaml_single_byte {

  }

  def method_with_yaml_single_integer {

  }

  def method_with_yaml_single_decimal {

  }

  def method_with_yaml_single_plain {

  }

  def method_with_csv {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_with_csv_multi_entity(e)
        case p: PEntityPartType => method_with_csv_multi_part(p)
        case p: PPowertypeType  => method_with_csv_multi_powertype(p)
        case v: PByteType       => method_with_csv_multi_byte
        case v: PIntegerType    => method_with_csv_multi_integer
        case v: PDecimalType    => method_with_csv_multi_decimal
        case _                  => method_with_csv_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_with_csv_single_entity(e)
        case p: PEntityPartType => method_with_csv_single_part(p)
        case p: PPowertypeType  => method_with_csv_single_powertype(p)
        case v: PByteType       => method_with_csv_single_byte
        case v: PIntegerType    => method_with_csv_single_integer
        case v: PDecimalType    => method_with_csv_single_decimal
        case _                  => method_with_csv_single_plain
      }
    }

  }

  def method_with_csv_multi_entity(e: PEntityType) {

  }

  def method_with_csv_multi_part(e: PEntityPartType) {

  }

  def method_with_csv_multi_powertype(e: PPowertypeType) {

  }

  def method_with_csv_multi_byte {

  }

  def method_with_csv_multi_integer {

  }

  def method_with_csv_multi_decimal {

  }

  def method_with_csv_multi_plain {

  }

  def method_with_csv_single_entity(e: PEntityType) {

  }

  def method_with_csv_single_part(e: PEntityPartType) {

  }

  def method_with_csv_single_powertype(e: PPowertypeType) {

  }

  def method_with_csv_single_byte {

  }

  def method_with_csv_single_integer {

  }

  def method_with_csv_single_decimal {

  }

  def method_with_csv_single_plain {

  }

  def method_with_urlencode {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_with_urlencode_multi_entity(e)
        case p: PEntityPartType => method_with_urlencode_multi_part(p)
        case p: PPowertypeType  => method_with_urlencode_multi_powertype(p)
        case v: PByteType       => method_with_urlencode_multi_byte
        case v: PIntegerType    => method_with_urlencode_multi_integer
        case v: PDecimalType    => method_with_urlencode_multi_decimal
        case _                  => method_with_urlencode_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_with_urlencode_single_entity(e)
        case p: PEntityPartType => method_with_urlencode_single_part(p)
        case p: PPowertypeType  => method_with_urlencode_single_powertype(p)
        case v: PByteType       => method_with_urlencode_single_byte
        case v: PIntegerType    => method_with_urlencode_single_integer
        case v: PDecimalType    => method_with_urlencode_single_decimal
        case _                  => method_with_urlencode_single_plain
      }
    }

  }

  def method_with_urlencode_multi_entity(e: PEntityType) {

  }

  def method_with_urlencode_multi_part(e: PEntityPartType) {

  }

  def method_with_urlencode_multi_powertype(e: PPowertypeType) {

  }

  def method_with_urlencode_multi_byte {

  }

  def method_with_urlencode_multi_integer {

  }

  def method_with_urlencode_multi_decimal {

  }

  def method_with_urlencode_multi_plain {

  }

  def method_with_urlencode_single_entity(e: PEntityType) {

  }

  def method_with_urlencode_single_part(e: PEntityPartType) {

  }

  def method_with_urlencode_single_powertype(e: PPowertypeType) {

  }

  def method_with_urlencode_single_byte {

  }

  def method_with_urlencode_single_integer {

  }

  def method_with_urlencode_single_decimal {

  }

  def method_with_urlencode_single_plain {

  }

  /*
   * macro_to
   */
  def to_string {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_to_string_multi_entity(e)
        case p: PEntityPartType => method_to_string_multi_part(p)
        case p: PPowertypeType  => method_to_string_multi_powertype(p)
        case v: PByteType       => method_to_string_multi_byte
        case v: PIntegerType    => method_to_string_multi_integer
        case v: PDecimalType    => method_to_string_multi_decimal
        case _                  => method_to_string_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_to_string_single_entity(e)
        case p: PEntityPartType => method_to_string_single_part(p)
        case p: PPowertypeType  => method_to_string_single_powertype(p)
        case v: PByteType       => method_to_string_single_byte
        case v: PIntegerType    => method_to_string_single_integer
        case v: PDecimalType    => method_to_string_single_decimal
        case _                  => method_to_string_single_plain
      }
    }

  }

  def method_to_string_multi_entity(e: PEntityType) {

  }

  def method_to_string_multi_part(e: PEntityPartType) {

  }

  def method_to_string_multi_powertype(e: PPowertypeType) {

  }

  def method_to_string_multi_byte {

  }

  def method_to_string_multi_integer {

  }

  def method_to_string_multi_decimal {

  }

  def method_to_string_multi_plain {

  }

  def method_to_string_single_entity(e: PEntityType) {

  }

  def method_to_string_single_part(e: PEntityPartType) {

  }

  def method_to_string_single_powertype(e: PPowertypeType) {

  }

  def method_to_string_single_byte {

  }

  def method_to_string_single_integer {

  }

  def method_to_string_single_decimal {

  }

  def method_to_string_single_plain {

  }

  def to_xml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_to_xml_multi_entity(e)
        case p: PEntityPartType => method_to_xml_multi_part(p)
        case p: PPowertypeType  => method_to_xml_multi_powertype(p)
        case v: PByteType       => method_to_xml_multi_byte
        case v: PIntegerType    => method_to_xml_multi_integer
        case v: PDecimalType    => method_to_xml_multi_decimal
        case _                  => method_to_xml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_to_xml_single_entity(e)
        case p: PEntityPartType => method_to_xml_single_part(p)
        case p: PPowertypeType  => method_to_xml_single_powertype(p)
        case v: PByteType       => method_to_xml_single_byte
        case v: PIntegerType    => method_to_xml_single_integer
        case v: PDecimalType    => method_to_xml_single_decimal
        case _                  => method_to_xml_single_plain
      }
    }

  }

  def method_to_xml_multi_entity(e: PEntityType) {

  }

  def method_to_xml_multi_part(e: PEntityPartType) {

  }

  def method_to_xml_multi_powertype(e: PPowertypeType) {

  }

  def method_to_xml_multi_byte {

  }

  def method_to_xml_multi_integer {

  }

  def method_to_xml_multi_decimal {

  }

  def method_to_xml_multi_plain {

  }

  def method_to_xml_single_entity(e: PEntityType) {

  }

  def method_to_xml_single_part(e: PEntityPartType) {

  }

  def method_to_xml_single_powertype(e: PPowertypeType) {

  }

  def method_to_xml_single_byte {

  }

  def method_to_xml_single_integer {

  }

  def method_to_xml_single_decimal {

  }

  def method_to_xml_single_plain {

  }

  def to_json {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_to_json_multi_entity(e)
        case p: PEntityPartType => method_to_json_multi_part(p)
        case p: PPowertypeType  => method_to_json_multi_powertype(p)
        case v: PByteType       => method_to_json_multi_byte
        case v: PIntegerType    => method_to_json_multi_integer
        case v: PDecimalType    => method_to_json_multi_decimal
        case _                  => method_to_json_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_to_json_single_entity(e)
        case p: PEntityPartType => method_to_json_single_part(p)
        case p: PPowertypeType  => method_to_json_single_powertype(p)
        case v: PByteType       => method_to_json_single_byte
        case v: PIntegerType    => method_to_json_single_integer
        case v: PDecimalType    => method_to_json_single_decimal
        case _                  => method_to_json_single_plain
      }
    }

  }

  def method_to_json_multi_entity(e: PEntityType) {

  }

  def method_to_json_multi_part(e: PEntityPartType) {

  }

  def method_to_json_multi_powertype(e: PPowertypeType) {

  }

  def method_to_json_multi_byte {

  }

  def method_to_json_multi_integer {

  }

  def method_to_json_multi_decimal {

  }

  def method_to_json_multi_plain {

  }

  def method_to_json_single_entity(e: PEntityType) {

  }

  def method_to_json_single_part(e: PEntityPartType) {

  }

  def method_to_json_single_powertype(e: PPowertypeType) {

  }

  def method_to_json_single_byte {

  }

  def method_to_json_single_integer {

  }

  def method_to_json_single_decimal {

  }

  def method_to_json_single_plain {

  }

  def to_yaml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_to_yaml_multi_entity(e)
        case p: PEntityPartType => method_to_yaml_multi_part(p)
        case p: PPowertypeType  => method_to_yaml_multi_powertype(p)
        case v: PByteType       => method_to_yaml_multi_byte
        case v: PIntegerType    => method_to_yaml_multi_integer
        case v: PDecimalType    => method_to_yaml_multi_decimal
        case _                  => method_to_yaml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_to_yaml_single_entity(e)
        case p: PEntityPartType => method_to_yaml_single_part(p)
        case p: PPowertypeType  => method_to_yaml_single_powertype(p)
        case v: PByteType       => method_to_yaml_single_byte
        case v: PIntegerType    => method_to_yaml_single_integer
        case v: PDecimalType    => method_to_yaml_single_decimal
        case _                  => method_to_yaml_single_plain
      }
    }

  }

  def method_to_yaml_multi_entity(e: PEntityType) {

  }

  def method_to_yaml_multi_part(e: PEntityPartType) {

  }

  def method_to_yaml_multi_powertype(e: PPowertypeType) {

  }

  def method_to_yaml_multi_byte {

  }

  def method_to_yaml_multi_integer {

  }

  def method_to_yaml_multi_decimal {

  }

  def method_to_yaml_multi_plain {

  }

  def method_to_yaml_single_entity(e: PEntityType) {

  }

  def method_to_yaml_single_part(e: PEntityPartType) {

  }

  def method_to_yaml_single_powertype(e: PPowertypeType) {

  }

  def method_to_yaml_single_byte {

  }

  def method_to_yaml_single_integer {

  }

  def method_to_yaml_single_decimal {

  }

  def method_to_yaml_single_plain {

  }

  def to_csv {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_to_csv_multi_entity(e)
        case p: PEntityPartType => method_to_csv_multi_part(p)
        case p: PPowertypeType  => method_to_csv_multi_powertype(p)
        case v: PByteType       => method_to_csv_multi_byte
        case v: PIntegerType    => method_to_csv_multi_integer
        case v: PDecimalType    => method_to_csv_multi_decimal
        case _                  => method_to_csv_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_to_csv_single_entity(e)
        case p: PEntityPartType => method_to_csv_single_part(p)
        case p: PPowertypeType  => method_to_csv_single_powertype(p)
        case v: PByteType       => method_to_csv_single_byte
        case v: PIntegerType    => method_to_csv_single_integer
        case v: PDecimalType    => method_to_csv_single_decimal
        case _                  => method_to_csv_single_plain
      }
    }

  }

  def method_to_csv_multi_entity(e: PEntityType) {

  }

  def method_to_csv_multi_part(e: PEntityPartType) {

  }

  def method_to_csv_multi_powertype(e: PPowertypeType) {

  }

  def method_to_csv_multi_byte {

  }

  def method_to_csv_multi_integer {

  }

  def method_to_csv_multi_decimal {

  }

  def method_to_csv_multi_plain {

  }

  def method_to_csv_single_entity(e: PEntityType) {

  }

  def method_to_csv_single_part(e: PEntityPartType) {

  }

  def method_to_csv_single_powertype(e: PPowertypeType) {

  }

  def method_to_csv_single_byte {

  }

  def method_to_csv_single_integer {

  }

  def method_to_csv_single_decimal {

  }

  def method_to_csv_single_plain {

  }

  def to_urlencode {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_to_urlencode_multi_entity(e)
        case p: PEntityPartType => method_to_urlencode_multi_part(p)
        case p: PPowertypeType  => method_to_urlencode_multi_powertype(p)
        case v: PByteType       => method_to_urlencode_multi_byte
        case v: PIntegerType    => method_to_urlencode_multi_integer
        case v: PDecimalType    => method_to_urlencode_multi_decimal
        case _                  => method_to_urlencode_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_to_urlencode_single_entity(e)
        case p: PEntityPartType => method_to_urlencode_single_part(p)
        case p: PPowertypeType  => method_to_urlencode_single_powertype(p)
        case v: PByteType       => method_to_urlencode_single_byte
        case v: PIntegerType    => method_to_urlencode_single_integer
        case v: PDecimalType    => method_to_urlencode_single_decimal
        case _                  => method_to_urlencode_single_plain
      }
    }

  }

  def method_to_urlencode_multi_entity(e: PEntityType) {

  }

  def method_to_urlencode_multi_part(e: PEntityPartType) {

  }

  def method_to_urlencode_multi_powertype(e: PPowertypeType) {

  }

  def method_to_urlencode_multi_byte {

  }

  def method_to_urlencode_multi_integer {

  }

  def method_to_urlencode_multi_decimal {

  }

  def method_to_urlencode_multi_plain {

  }

  def method_to_urlencode_single_entity(e: PEntityType) {

  }

  def method_to_urlencode_single_part(e: PEntityPartType) {

  }

  def method_to_urlencode_single_powertype(e: PPowertypeType) {

  }

  def method_to_urlencode_single_byte {

  }

  def method_to_urlencode_single_integer {

  }

  def method_to_urlencode_single_decimal {

  }

  def method_to_urlencode_single_plain {

  }

  /*
   * macro_from
   */
  def from_string {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_from_string_multi_entity(e)
        case p: PEntityPartType => method_from_string_multi_part(p)
        case p: PPowertypeType  => method_from_string_multi_powertype(p)
        case v: PByteType       => method_from_string_multi_byte
        case v: PIntegerType    => method_from_string_multi_integer
        case v: PDecimalType    => method_from_string_multi_decimal
        case _                  => method_from_string_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_from_string_single_entity(e)
        case p: PEntityPartType => method_from_string_single_part(p)
        case p: PPowertypeType  => method_from_string_single_powertype(p)
        case v: PByteType       => method_from_string_single_byte
        case v: PIntegerType    => method_from_string_single_integer
        case v: PDecimalType    => method_from_string_single_decimal
        case _                  => method_from_string_single_plain
      }
    }

  }

  def method_from_string_multi_entity(e: PEntityType) {

  }

  def method_from_string_multi_part(e: PEntityPartType) {

  }

  def method_from_string_multi_powertype(e: PPowertypeType) {

  }

  def method_from_string_multi_byte {

  }

  def method_from_string_multi_integer {

  }

  def method_from_string_multi_decimal {

  }

  def method_from_string_multi_plain {

  }

  def method_from_string_single_entity(e: PEntityType) {

  }

  def method_from_string_single_part(e: PEntityPartType) {

  }

  def method_from_string_single_powertype(e: PPowertypeType) {

  }

  def method_from_string_single_byte {

  }

  def method_from_string_single_integer {

  }

  def method_from_string_single_decimal {

  }

  def method_from_string_single_plain {

  }

  def from_xml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_from_xml_multi_entity(e)
        case p: PEntityPartType => method_from_xml_multi_part(p)
        case p: PPowertypeType  => method_from_xml_multi_powertype(p)
        case v: PByteType       => method_from_xml_multi_byte
        case v: PIntegerType    => method_from_xml_multi_integer
        case v: PDecimalType    => method_from_xml_multi_decimal
        case _                  => method_from_xml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_from_xml_single_entity(e)
        case p: PEntityPartType => method_from_xml_single_part(p)
        case p: PPowertypeType  => method_from_xml_single_powertype(p)
        case v: PByteType       => method_from_xml_single_byte
        case v: PIntegerType    => method_from_xml_single_integer
        case v: PDecimalType    => method_from_xml_single_decimal
        case _                  => method_from_xml_single_plain
      }
    }

  }

  def method_from_xml_multi_entity(e: PEntityType) {

  }

  def method_from_xml_multi_part(e: PEntityPartType) {

  }

  def method_from_xml_multi_powertype(e: PPowertypeType) {

  }

  def method_from_xml_multi_byte {

  }

  def method_from_xml_multi_integer {

  }

  def method_from_xml_multi_decimal {

  }

  def method_from_xml_multi_plain {

  }

  def method_from_xml_single_entity(e: PEntityType) {

  }

  def method_from_xml_single_part(e: PEntityPartType) {

  }

  def method_from_xml_single_powertype(e: PPowertypeType) {

  }

  def method_from_xml_single_byte {

  }

  def method_from_xml_single_integer {

  }

  def method_from_xml_single_decimal {

  }

  def method_from_xml_single_plain {

  }

  def from_json {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_from_json_multi_entity(e)
        case p: PEntityPartType => method_from_json_multi_part(p)
        case p: PPowertypeType  => method_from_json_multi_powertype(p)
        case v: PByteType       => method_from_json_multi_byte
        case v: PIntegerType    => method_from_json_multi_integer
        case v: PDecimalType    => method_from_json_multi_decimal
        case _                  => method_from_json_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_from_json_single_entity(e)
        case p: PEntityPartType => method_from_json_single_part(p)
        case p: PPowertypeType  => method_from_json_single_powertype(p)
        case v: PByteType       => method_from_json_single_byte
        case v: PIntegerType    => method_from_json_single_integer
        case v: PDecimalType    => method_from_json_single_decimal
        case _                  => method_from_json_single_plain
      }
    }

  }

  def method_from_json_multi_entity(e: PEntityType) {

  }

  def method_from_json_multi_part(e: PEntityPartType) {

  }

  def method_from_json_multi_powertype(e: PPowertypeType) {

  }

  def method_from_json_multi_byte {

  }

  def method_from_json_multi_integer {

  }

  def method_from_json_multi_decimal {

  }

  def method_from_json_multi_plain {

  }

  def method_from_json_single_entity(e: PEntityType) {

  }

  def method_from_json_single_part(e: PEntityPartType) {

  }

  def method_from_json_single_powertype(e: PPowertypeType) {

  }

  def method_from_json_single_byte {

  }

  def method_from_json_single_integer {

  }

  def method_from_json_single_decimal {

  }

  def method_from_json_single_plain {

  }

  def from_yaml {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_from_yaml_multi_entity(e)
        case p: PEntityPartType => method_from_yaml_multi_part(p)
        case p: PPowertypeType  => method_from_yaml_multi_powertype(p)
        case v: PByteType       => method_from_yaml_multi_byte
        case v: PIntegerType    => method_from_yaml_multi_integer
        case v: PDecimalType    => method_from_yaml_multi_decimal
        case _                  => method_from_yaml_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_from_yaml_single_entity(e)
        case p: PEntityPartType => method_from_yaml_single_part(p)
        case p: PPowertypeType  => method_from_yaml_single_powertype(p)
        case v: PByteType       => method_from_yaml_single_byte
        case v: PIntegerType    => method_from_yaml_single_integer
        case v: PDecimalType    => method_from_yaml_single_decimal
        case _                  => method_from_yaml_single_plain
      }
    }

  }

  def method_from_yaml_multi_entity(e: PEntityType) {

  }

  def method_from_yaml_multi_part(e: PEntityPartType) {

  }

  def method_from_yaml_multi_powertype(e: PPowertypeType) {

  }

  def method_from_yaml_multi_byte {

  }

  def method_from_yaml_multi_integer {

  }

  def method_from_yaml_multi_decimal {

  }

  def method_from_yaml_multi_plain {

  }

  def method_from_yaml_single_entity(e: PEntityType) {

  }

  def method_from_yaml_single_part(e: PEntityPartType) {

  }

  def method_from_yaml_single_powertype(e: PPowertypeType) {

  }

  def method_from_yaml_single_byte {

  }

  def method_from_yaml_single_integer {

  }

  def method_from_yaml_single_decimal {

  }

  def method_from_yaml_single_plain {

  }

  def from_csv {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_from_csv_multi_entity(e)
        case p: PEntityPartType => method_from_csv_multi_part(p)
        case p: PPowertypeType  => method_from_csv_multi_powertype(p)
        case v: PByteType       => method_from_csv_multi_byte
        case v: PIntegerType    => method_from_csv_multi_integer
        case v: PDecimalType    => method_from_csv_multi_decimal
        case _                  => method_from_csv_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_from_csv_single_entity(e)
        case p: PEntityPartType => method_from_csv_single_part(p)
        case p: PPowertypeType  => method_from_csv_single_powertype(p)
        case v: PByteType       => method_from_csv_single_byte
        case v: PIntegerType    => method_from_csv_single_integer
        case v: PDecimalType    => method_from_csv_single_decimal
        case _                  => method_from_csv_single_plain
      }
    }

  }

  def method_from_csv_multi_entity(e: PEntityType) {

  }

  def method_from_csv_multi_part(e: PEntityPartType) {

  }

  def method_from_csv_multi_powertype(e: PPowertypeType) {

  }

  def method_from_csv_multi_byte {

  }

  def method_from_csv_multi_integer {

  }

  def method_from_csv_multi_decimal {

  }

  def method_from_csv_multi_plain {

  }

  def method_from_csv_single_entity(e: PEntityType) {

  }

  def method_from_csv_single_part(e: PEntityPartType) {

  }

  def method_from_csv_single_powertype(e: PPowertypeType) {

  }

  def method_from_csv_single_byte {

  }

  def method_from_csv_single_integer {

  }

  def method_from_csv_single_decimal {

  }

  def method_from_csv_single_plain {

  }

  def from_urlencode {
    if (attr.isHasMany) {
      attr.attributeType match {
        case e: PEntityType     => method_from_urlencode_multi_entity(e)
        case p: PEntityPartType => method_from_urlencode_multi_part(p)
        case p: PPowertypeType  => method_from_urlencode_multi_powertype(p)
        case v: PByteType       => method_from_urlencode_multi_byte
        case v: PIntegerType    => method_from_urlencode_multi_integer
        case v: PDecimalType    => method_from_urlencode_multi_decimal
        case _                  => method_from_urlencode_multi_plain
      }
    } else {
      attr.attributeType match {
        case e: PEntityType     => method_from_urlencode_single_entity(e)
        case p: PEntityPartType => method_from_urlencode_single_part(p)
        case p: PPowertypeType  => method_from_urlencode_single_powertype(p)
        case v: PByteType       => method_from_urlencode_single_byte
        case v: PIntegerType    => method_from_urlencode_single_integer
        case v: PDecimalType    => method_from_urlencode_single_decimal
        case _                  => method_from_urlencode_single_plain
      }
    }

  }

  def method_from_urlencode_multi_entity(e: PEntityType) {

  }

  def method_from_urlencode_multi_part(e: PEntityPartType) {

  }

  def method_from_urlencode_multi_powertype(e: PPowertypeType) {

  }

  def method_from_urlencode_multi_byte {

  }

  def method_from_urlencode_multi_integer {

  }

  def method_from_urlencode_multi_decimal {

  }

  def method_from_urlencode_multi_plain {

  }

  def method_from_urlencode_single_entity(e: PEntityType) {

  }

  def method_from_urlencode_single_part(e: PEntityPartType) {

  }

  def method_from_urlencode_single_powertype(e: PPowertypeType) {

  }

  def method_from_urlencode_single_byte {

  }

  def method_from_urlencode_single_integer {

  }

  def method_from_urlencode_single_decimal {

  }

  def method_from_urlencode_single_plain {

  }

  /*
   * Document
   */
  def method_document_plain() {
  }

  def method_document_shallow() {
  }

  def document_methods_update_attribute() {
  }
}
