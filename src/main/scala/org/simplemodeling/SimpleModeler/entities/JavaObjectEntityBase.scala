package org.simplemodeling.SimpleModeler.entities

import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.goldenport.util.Booleans
import java.io.BufferedWriter
import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity._
import java.text.SimpleDateFormat
import java.util.TimeZone
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   Apr. 22, 2011
 *  version Aug. 20, 2011
 *  version Dec. 15, 2011
 *  version May.  5, 2012
 *  version Oct. 26, 2012
 * @version Nov.  1, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class JavaObjectEntityBase(val javaContext: JavaEntityContextBase)
    extends PObjectEntity(javaContext) with JavaMakerHolder {

  val fileSuffix = "java"

  private val _aspects = new ArrayBuffer[JavaAspect]
  private var _maker: JavaMaker = null

//  def modelEntity = modelObject.asInstanceOf[SMEntity]
//  val documentName = ""
//  var documentName: String = ""

  /**
   * Java6 entities (e.g. Java6EntityEntity) does not use this method.
   * These entities override the method and use a JavaclassDefinition object
   * to produces Java source codes.
   */
  override protected def write_Content(out: BufferedWriter) {
    println("JavaObjectEntityBase")
    _maker = new JavaMaker
    _aspects.foreach(_.open(_maker))
    make_package
    make_imports
    make_class_open
    make_attribute_variables
    make_null_constructor
    make_attribute_methods
    make_init_method
    make_update_method
    make_document_method
    make_make_persistent_method
    make_delete_persistent_method
    make_get_entity_method
    make_get_entities_method
    make_delete_entity_method
    make_entity_fill_method
    make_entity_restore_method
    make_entity_info
    make_class_close
    out.append(_maker.toString)
    out.flush()
  }

  def make_package {
    require(packageName != null)
    if (packageName != "") {
      jm_package(packageName)
    }
    jm_end_package_section()
  }

  def make_imports {
    jm_import("java.util.*")
    jm_import("java.math.*")
    if (_baseObject != null) {
      make_import_object(_baseObject)
    }
    val attrs = attributes.filter(_.attributeType.isEntity)
    for (attr <- attrs) {
      val entity = attr.attributeType.asInstanceOf[PEntityType]
      make_import_entity(entity)
    }
    make_Imports()
    _aspects.foreach(_.weaveImports)
    jm_end_import_section()
  }

  protected final def make_import_object(anObject: PObjectReferenceType) {
    if (packageName != anObject.packageName) {
      jm_import(anObject.qualifiedName)
    }
  }

  protected final def make_import_entity(anEntity: PEntityType) {
    if (packageName != anEntity.packageName) {
      jm_import(anEntity.qualifiedName)
    }
  }

  protected def make_Imports() {
  }

  protected def make_class_open {
    jm_p("@PersistenceCapable(identityType=IdentityType.APPLICATION")
    if (modelEntity.jdo.detachable) {
      jm_p(", detachable=\"true\"")
    }
    if (notNull(modelEntity.jdo.catalog)) {
      jm_p(", catalog=\"")
      jm_p(modelEntity.jdo.catalog)
      jm_p("\"")
    }
    if (notNull(modelEntity.jdo.schema)) {
      jm_p(", schema=\"")
      jm_p(modelEntity.jdo.schema)
      jm_p("\"")
    }
    if (notNull(modelEntity.jdo.table)) {
      jm_p(", table=\"")
      jm_p(modelEntity.jdo.table)
      jm_p("\"")
    } else if (notNull(modelEntity.sql.table)) {
      jm_p(", table=\"")
      jm_p(modelEntity.sql.table)
      jm_p("\"")
    }
    jm_pln(")")
    jm_p("public class ")
    jm_p(name)
    getBaseObject match {
      case Some(base) => {
        jm_p(" extends ")
        jm_p(base.name)
      }
      case None => {}
    }
    jm_pln(" {")
  }

  protected def make_class_close {
    jm_pln("}")
  }

  def make_attribute_variables {
    def non_id_attributes {
      for (attr <- attributes if !attr.isId) {
        make_attribute_variable(attr)
        jm_pln()
      }
    }

    def aggregate_attributes {
      def back_reference(source: SMObject, assoc: SMAssociation) {
        // XXX think PParticipation
        val idTypeName = "String"
          val typeName = assoc.backReferenceMultiplicityOption match {
          case Some(m: SMMultiplicityOne)      => idTypeName
          case Some(m: SMMultiplicityZeroOne)  => idTypeName
          case Some(m: SMMultiplicityOneMore)  => "List<" + idTypeName + ">"
          case Some(m: SMMultiplicityZeroMore) => "List<" + idTypeName + ">"
          case None                            => "String" // XXX
        }
        jm_pln("@Persistent")
        jm_pln("private %s %s;".format(typeName, back_reference_var_name(source, assoc)))
        jm_pln()
      }

      def is_back_reference(participation: SMParticipation) = {
        participation.roleType match {
        case CompositionParticipationRole => true
        case AggregationParticipationRole => true
        case AssociationParticipationRole => {
          participation.association.isBinary ||
          participation.association.isQueryReference
        }
        }
      }

      for (participation <- modelObject.participations) {
        if (is_back_reference(participation)) {
          back_reference(participation.element, participation.association)
        }
      }
    }

    def extension_attribuets {
      val build = {
        val format = new SimpleDateFormat("yyyyMMddHHmmss")
        format.setTimeZone(TimeZone.getTimeZone("UTC"))
        format.format(new java.util.Date)
      }
      val pkg = modelObject.parent.asInstanceOf[SMPackage]
      record_trace("pkg name = " + pkg.qualifiedName)
      val modelName = pkg.qualifiedName
      val modelVersion = pkg.version match {
        case "" => "alpha"
        case v  => v
      }
      val modelBuild = build
      val className = qualifiedName
      val classVersion = modelObject.version match {
        case "" => "alpha"
        case v  => v
      }
      val classBuild = build
      jm_pln("@Persistent")
      jm_pln("private User _entity_author = null;")
      jm_pln("@Persistent")
      jm_pln("private Date _entity_created = null;")
      jm_pln("@Persistent")
      jm_pln("private Date _entity_updated = null;")
      jm_pln("@Persistent")
      jm_pln("private Date _entity_removed = null;")
      jm_pln("@Persistent")
      jm_pln("private String _entity_model_name = \"%s\";".format(modelName))
      jm_pln("@Persistent")
      jm_pln("private String _entity_model_version = \"%s\";".format(modelVersion))
      jm_pln("@Persistent")
      jm_pln("private String _entity_model_build = \"%s\";".format(modelBuild))
      jm_pln("@Persistent")
      jm_pln("private String _entity_class_name = \"%s\";".format(className))
      jm_pln("@Persistent")
      jm_pln("private String _entity_class_version = \"%s\";".format(classVersion))
      jm_pln("@Persistent")
      jm_pln("private String _entity_class_build = \"%s\";".format(classBuild))
      jm_pln("@NotPersistent")
      jm_pln("private transient Key _parent_key = null;")
      jm_pln("@Persistent")
      jm_pln("private Text _entity_info = null;")
      jm_pln("@NotPersistent")
      jm_pln("private transient MDEntityInfo entity_info;")
      jm_pln("@NotPersistent")
      jm_pln("  private final transient %s entity_context = %s.getFactory().createContext();".format(contextName, factoryName))
      jm_pln()
    }

    id_attribute
    non_id_attributes
    aggregate_attributes
    extension_attribuets
  }

  def id_Attribute_Slot(idAttr: PAttribute, varName: String) = false

  def id_attribute {
    val varName = var_name(idAttr)
    if (_aspects.find(_.weaveIdAttributeSlot(idAttr, varName) == true).isDefined) {
      println("JavaObjectEntityBase#id_attribute[%s] aspect".format(varName))
    } else if (id_Attribute_Slot(idAttr, varName)) {
      println("JavaObjectEntityBase#id_attribute[%s] subclass".format(varName))
    } else {
      jm_private_instance_variable(idAttr, idAttr.typeName, varName)
      println("JavaObjectEntityBase#id_attribute[%s] default".format(varName))
    }
  }

  def make_attribute_variable(attr: PAttribute) {
    val attrName = attr.name
    val paramName = attr.name
    val varName = var_name(attr)
    val jdoVarName = entity_ref_jdo_var_name(attr)
    val refVarName = entity_ref_assoc_var_name(attr)
    val partVarName = entity_ref_part_var_name(attr)
    val updatedVarName = entity_ref_updated_var_name(attr)
    val cacheVarName = entity_ref_cache_var_name(attr)
    val cacheTimestampVarName = entity_ref_cache_timestamp_var_name(attr)

      def make_persistent_annotation {
        _aspects.foreach(_.weavePersistentAnnotation(attr))
        make_Persistent_Annotation(attr)
      }
    
      def make_not_persistent_annotation {
        _aspects.foreach(_.weaveNotPersistentAnnotation(attr))
        make_Not_Persistent_Annotation(attr)
      }

      // reference
      def make_reference_key_long(entityType: PEntityType) {
        make_reference_id_slot(entityType)
        //        make_reference_updated_slot(entityType)
        make_reference_object_slot(entityType)
        make_reference_cache_slot(entityType)
      }

      def make_reference_key_unencoded_string(entityType: PEntityType) {
        make_reference_id_slot(entityType)
        //        make_reference_updated_slot(entityType)
        make_reference_object_slot(entityType)
        make_reference_cache_slot(entityType)
      }

      def make_reference_owned_property(entityType: PEntityType) {
        make_reference_owned_slot(entityType)
        make_reference_is_loaded_slot(entityType)
        //        make_reference_updated_slot(entityType)
        make_reference_object_slot(entityType)
        make_reference_cache_slot(entityType)
      }

      def make_reference_query_property(entityType: PEntityType) {
        make_reference_object_slot(entityType)
        make_reference_cache_slot(entityType)
      }

      def make_reference_id_slot(entityType: PEntityType) {
        val id_type = entityType.entity.idAttr.typeName
        make_persistent_annotation
        jm_private_instance_variable(idAttr, id_type, jdoVarName);
      }

      def make_reference_is_loaded_slot(entityType: PEntityType) {
          val id_type = entityType.entity.idAttr.typeName
        make_not_persistent_annotation
        jm_private_instance_variable(entityType.entity.idAttr, "boolean", entity_ref_is_loaded_var_name(attr)) 
      }

      def make_reference_owned_slot(entityType: PEntityType) {
        val refType = entityType.entity.name
        make_persistent_annotation
        jm_private_instance_variable(attr, refType, jdoVarName)
      }

      def make_reference_updated_slot(entityType: PEntityType) {
        make_persistent_annotation
        jm_private_instance_variable(attr, "Date", updatedVarName) 
      }

      def make_reference_object_slot(entityType: PEntityType) {
        make_not_persistent_annotation
        jm_private_transient_instance_variable(attr, jdo_type(attr), refVarName);
      }

      def make_reference_cache_slot(entityType: PEntityType) {
        make_persistent_annotation
        jm_private_instance_variable_single("Date", cacheTimestampVarName);
        make_persistent_annotation
        jm_private_instance_variable(attr, java_doc_type(attr), cacheVarName);
      }

      def make_plain_attribute {
        make_persistent_annotation
        jm_private_instance_variable(attr, jdo_type(attr), jdoVarName);
      }

      def make_entity_attribute(e: PEntityType) {
        if (is_owned_property(attr)) {
          make_reference_owned_property(e)
        } else if (is_query_property(attr)) {
          make_reference_query_property(e)
        } else {
          e.entity.idPolicy match {
          case SMAutoIdPolicy => make_reference_key_long(e)
          case SMApplicationIdPolicy => make_reference_key_unencoded_string(e)
          }
        }
      }

      def make_entity_part_attribute(p: PEntityPartType) {
        make_persistent_annotation
        jm_private_instance_variable_single("Text", jdoVarName); // XXX Text google app engine
        make_not_persistent_annotation
        jm_private_transient_instance_variable(attr, java_type(attr), partVarName);
      }

      def make_powertype_atttribute(p: PPowertypeType) {
        make_persistent_annotation
        jm_private_instance_variable(attr, "String", jdoVarName);
        make_persistent_annotation
        jm_private_instance_variable(attr, "String", entity_ref_label_var_name(attr))
        make_persistent_annotation
        jm_private_instance_variable_single("Date", entity_ref_updated_var_name(attr))
        make_persistent_annotation
        jm_private_instance_variable_single("Date", entity_ref_cache_timestamp_var_name(attr))
        make_not_persistent_annotation
        jm_private_transient_instance_variable(attr, java_type(attr), entity_ref_powertype_var_name(attr))
      }

      //      println("Attr %s, kind = %s".format(attr.name, attr.kind)) // 2009-10-28
    attr.kind match {
      case NullAttributeKind => {
        attr.attributeType match {
          case e: PEntityType => make_entity_attribute(e)
          case p: PEntityPartType => make_entity_part_attribute(p)
          case p: PPowertypeType => make_powertype_atttribute(p)
          case _ => make_plain_attribute
        }
      }
      case IdAttributeKind       => {}
      /*
        case IdAttributeKind => {
          attr.idPolicy match {
            case SMAutoIdPolicy => make_key_long
            case SMApplicationIdPolicy => make_key_unencoded_string
          }
        }
*/
      case NameAttributeKind     => make_plain_attribute
      case UserAttributeKind     => make_plain_attribute
      case TitleAttributeKind    => make_plain_attribute
      case SubTitleAttributeKind => make_plain_attribute
      case SummaryAttributeKind  => make_plain_attribute
      case CategoryAttributeKind => make_plain_attribute
      case AuthorAttributeKind   => {}
      case IconAttributeKind     => make_plain_attribute
      case LogoAttributeKind     => make_plain_attribute
      case LinkAttributeKind     => make_plain_attribute
      case ContentAttributeKind  => make_plain_attribute
      case CreatedAttributeKind  => {}
      case UpdatedAttributeKind  => {}
    }
  }

  protected def make_Persistent_Annotation(attr: PAttribute) {}
  protected def make_Not_Persistent_Annotation(attr: PAttribute) {}

  /*
   * Methods
   */
  def make_attribute_methods {
    id_methods
    non_id_methods
  }

  def id_methods {
    val attrName = attr_name(idAttr)
    val varName = var_name(idAttr)
    val paramName = attr_name(idAttr)
    val javaType = java_type(idAttr)
    if (_aspects.find(_.weaveIdMethods(idAttr, attrName, varName, paramName, javaType) == true).isDefined) {
      println("JavaObjectEntityBase#id_attribute[%s] aspect".format(varName))
    } else if (make_Id_Methods(idAttr, attrName, varName, paramName, javaType)) {
      println("JavaObjectEntityBase#id_attribute[%s] subclass".format(varName))
    } else {
      jm_private_instance_variable(idAttr, java_type(idAttr), varName)
      println("JavaObjectEntityBase#id_attribute[%s] default".format(varName))
    }
  }

  def non_id_methods {
    for (attr <- attributes if !attr.isId) {
      make_attribute_method(attr)
      jm_pln
    }
  }

  protected def make_Id_Methods(idAttr: PAttribute, attrName: String, varName: String, paramName: String, javaType: String): Boolean = false

  def make_attribute_method(attr: PAttribute) {
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
    val jdoVarName = entity_ref_jdo_var_name(attr)
    val refVarName = entity_ref_assoc_var_name(attr)
    val partVarName = entity_ref_part_var_name(attr)
    val powerVarName = entity_ref_powertype_var_name(attr)
    val updatedVarName = entity_ref_updated_var_name(attr)
    val cacheVarName = entity_ref_cache_var_name(attr)
    val cacheTimestampVarName = entity_ref_cache_timestamp_var_name(attr)

    // Single
    def make_single_reference_attribute_method(entityType: PEntityType) {
      def make_single_fill {
        make_pm_delegate_method("fill_" + attrName)
        jm_protected_final_void_method("fill_" + attrName + "(PersistenceManager pm)") {
          jm_if_return(refVarName + " != null")
          if (attr.isCache) {
            jm_if("entity_context.isAssociationCache(this, %s) && %s != null".format(cacheTimestampVarName, cacheVarName)) {
              jm_try {
                jm_assign_new(refVarName, entityType.entity.name)
                jm_pln("%s.init_document(%s, pm);".format(refVarName, cacheVarName))
                jm_return
              }
              jm_catch_end("Exception e") {
                jm_assign_null(cacheTimestampVarName)
                jm_assign_null(cacheVarName)
              }
            }
          }
          if (is_owned_property(attr)) {
            jm_assign(refVarName, jdoVarName)
            jm_assign_true(entity_ref_is_loaded_var_name(attr))
          } else if (is_query_property(attr)) {
            jm_code("""      // query single
      Query query = pm.newQuery(%entityName%.class);
      query.setFilter("%backRefVarName% == idParam");
      query.setOrdering("updated desc");
      query.declareParameters("Long idParam");
      List<%entityName%> entities = (List<%entityName%>)query.execute(getId());
      if (entities.size() > 0) {
        %refVarName% = entities.get(0);       
      }
      if (entities.size() > 1) {
        // TODO warning
      }
""", Map("%entityName%" -> entityType.entity.name,
    "%refVarName%" -> refVarName,
    "%backRefVarName%" -> back_reference_var_name(attr)))
          } else {
            jm_if(jdoVarName + " != null") {
              jm_assign(refVarName, "%s.get_entity(%s, pm)", entityType.entity.name, pContext.variableName4RefId(attr))
            }
          }
        }
      }

      def make_single_is_update_cache {
        if (is_owned_property(attr)) {
          jm_private_method("boolean is_update_cache_%s()", attrName) {
             jm_code("""        if (%varName%_association == null) return false;
        if (%varName%_cache == null) return true;
        Long id = %varName%_association.getId();
        Date updated = %varName%_association.entity_getUpdated();
        if (is_loaded_%varName% && !(id.equals(%varName%.getId()))) return true;
        if (!id.equals(%varName%_cache.id)) return true;
        if (!updated.equals(%varName%_cache.entity_info.updated)) return true;
        return false;
""", Map("%varName%" -> varName))
          }
        } else if (is_query_property(attr)) {
          jm_private_method("private boolean is_update_cache_%s()", attrName) {
            jm_code("""        if (%varName%_association == null) return false;
        if (%varName%_cache == null) return true;
        Long id = %varName%_association.getId();
        Date updated = %varName%_association.entity_getUpdated();
        if (!id.equals(%varName%_cache.id)) return true;
        if (!updated.equals(%varName%_cache.entity_info.updated)) return true;
        return false;
""", Map("%varName%" -> varName))
          }
        } else {
          jm_private_method("boolean is_update_cache_%s()", attrName) {
            jm_code("""        if (%varName%_association == null) return false;
        if (%varName%_cache == null) return true;
        Long id = %varName%_association.getId();
        Date updated = %varName%_association.entity_getUpdated();
        if (!id.equals(%varName%_id)) return true;
        if (!id.equals(%varName%_cache.id)) return true;
        if (!updated.equals(%varName%_cache.entity_info.updated)) return true;
        return false;
""", Map("%varName%" -> varName))
          }
        }
      }

      def make_single_restore {
        if (is_owned_property(attr) || is_query_property(attr)) {
          jm_protected_final_void_method("restore_%s()", attrName) {
            jm_code("""        if (is_update_cache_%varName%()) {
            %varName%_cache = %varName%_association.make_document();
            %varName%_cache_timestamp = new Date();
      } else {
          %varName%_cache_timestamp = null;
          %varName%_cache = null;
      }
""", Map("%varName%" -> varName,
    "%docName%" -> jdo_element_type(attr)))
          }
        } else {
          jm_protected_final_void_method("restore_%s()", attrName) {
            jm_code("""        if (is_update_cache_%varName%()) {
            %varName%_id = %varName%_association.getId();
            %varName%_cache = %varName%_association.make_document();
            %varName%_cache_timestamp = new Date();
      } else {
          %varName%_cache_timestamp = null;
          %varName%_cache = null;
      }
""", Map("%varName%" -> varName,
    "%docName%" -> jdo_element_type(attr)))
          }
        }
      }

      def make_single_get {
        jm_public_method("%s get%s()", java_type(attr), attrName.capitalize) {
          jm_pln("fill_%s();", attrName);
          jm_return(refVarName);
        }
      }

      def make_single_set {
        if (is_settable(attr)) {
          jm_public_void_method("set%s(%s %s)", attrName.capitalize, java_type(attr), paramName) {
            if (is_owned_property(attr)) {
              jm_pln("// TODO owned (set part built from doc)")
            } else if (is_query_property(attr)) {
              jm_pln("// TODO query (set part built from doc)")
            } else {
              jm_assign_this(refVarName, paramName);
              jm_assign_this(jdoVarName, "%s.get%s()", paramName, entityType.entity.idName.capitalize) 
            }
          }
        }
      }
      
      make_single_fill
      make_single_is_update_cache
      make_single_restore
      make_single_get
      make_single_set
    } // make_single_reference_attribute_method

    def make_single_part_attribute_method(partType: PEntityPartType) {
      make_pm_delegate_method("fill_" + attrName)
      jm_protected_final_void_method("fill_%s(PersistenceManager pm)", attrName) { 
        jm_if_else("%s != null && %s == null", jdoVarName, partVarName) {
          jm_assign_new(partVarName, java_element_type(attr), "%s.toString(), pm", jdoVarName)
        }
        jm_else_if_else("%s == null && %s == null", jdoVarName, partVarName) {
        }
        jm_else {
        }
      }
      jm_protected_final_void_method("restore_%s()", attrName) {
        jm_if(partVarName + " != null") {
          jm_assign_new(jdoVarName, "Text", "%s.toString()", partVarName)
        }
      }
      jm_public_get_method(java_type(attr), attrName, partVarName)
      if (attr.attributeType == PBooleanType) {
        jm_public_is_method(attrName, varName)
      }
      if (is_settable(attr)) {
        jm_public_set_method(attrName, java_type(attr), paramName, partVarName)
      }
    }

    def make_single_powertype_attribute_method(powertypeType: PPowertypeType) {
      make_pm_delegate_method("fill_" + attrName)
      jm_protected_final_void_method("fill_%s(PersistenceManager pm)", attrName) {
        jm_if("%s != null", powerVarName) {
          jm_return
        }
        jm_if("entity_context.isAssociationCache(this, %s) && %s != null", cacheTimestampVarName, entity_ref_label_var_name(attr)) {
          jm_assign(powerVarName, "%s.get(%s, %s, %s)", java_element_type(attr), jdoVarName, entity_ref_label_var_name(attr), entity_ref_updated_var_name(attr))
          jm_return
        }
        jm_if("%s != null", jdoVarName) {
          jm_assign(powerVarName, "%s.get(%s, pm)", java_element_type(attr), jdoVarName)
        }
      }
      jm_protected_final_void_method("restore_%s()", attrName) {
        jm_if("%s != null", powerVarName) {
          jm_assign(jdoVarName, "%s.getKey()", powerVarName)
          jm_assign(entity_ref_label_var_name(attr), "%s.getLabel()", powerVarName)
          jm_assign(entity_ref_updated_var_name(attr), "%s.getUpdated()", powerVarName) 
        }
      }
      jm_public_get_method(java_type(attr), attrName, powerVarName); 
      if (attr.attributeType == PBooleanType) {
        jm_public_is_method(attrName, varName)
      }
      if (is_settable(attr)) {
        jm_public_set_method(attrName, java_type(attr), paramName, powerVarName);
      }
    }

    def make_mapping_single_value_attribute_method(getter: String, setter: String) {
      jm_public_get_or_null_method(java_type(attr), attrName, getter, attrName)
      if (attr.attributeType == PBooleanType) {
        jm_public_is_method(attrName, varName);
      }
      if (is_settable(attr)) {
        jm_public_set_or_null_method(attrName, java_type(attr), paramName, setter)
      }
    }

    def make_single_value_attribute_method {
      jm_public_get_method(java_type(attr), attrName, varName)
      if (attr.attributeType == PBooleanType) {
        jm_public_is_method(attrName, varName)
      }
      if (is_settable(attr)) {
        jm_public_set_method(attrName, java_type(attr), paramName, varName)
      }
    }

    //
    // Multi
    //
    def make_multi_reference_attribute_method(entityType: PEntityType) {
      def make_multi_try_reuse_cache {
        jm_if("%s != null", refVarName) {
          jm_return
        }
        jm_if("entity_context.isAssociationCache(this, %s) && %s != null", cacheTimestampVarName, cacheVarName) {
          jm_try {
            jm_assign_new_ArrayList(refVarName, entityType.entity.name)
            jm_for(entityType.entity.documentName, "doc", cacheVarName) {
              jm_pln("%s entity = new %s();", entityType.entity.name, entityType.entity.name)
              jm_pln("entity.init_document(doc, pm);")
              jm_pln("%s.add(entity);", refVarName)
            }
            jm_return
          }
          jm_catch_end("Exception e") {
            jm_assign_null(refVarName)
            jm_assign_null(cacheTimestampVarName)
            jm_assign_null(cacheVarName)
          }
        }
      }

      def make_multi_fill {
        make_pm_delegate_method("fill_" + attrName)
        jm_protected_final_void_method("fill_%s(PersistenceManager pm)", attrName) {
          make_multi_try_reuse_cache
          if (is_owned_property(attr)) {
            jm_pln("// owned")
            jm_assign_new_ArrayList(refVarName, jdo_element_type(attr), jdoVarName)
            jm_assign_true(entity_ref_is_loaded_var_name(attr))
          } else if (is_query_property(attr)) {
            jm_pln("// query")
            jm_code("""%entityName%Query query = query%capitalizePropertyName%(pm);
%entityName%Chunks chunks = query.execute();
%refVarName% = new ArrayList<%entityName%>(chunks.next());
""", Map("%entityName%" -> attr.elementTypeName,
    "%capitalizePropertyName%" -> attrName.capitalize,
    "%refVarName%" -> refVarName))
          } else {
            jm_if_not_null(jdoVarName) {
              jm_assign(refVarName, "%s.get_entities(%s, pm);", pContext.variableName4RefId(attr)) 
            }
          }
        }
                
        def make_multi_is_update_cache {
          if (is_owned_property(attr)) {
            jm_private_method("boolean is_update_cache_%s()", attrName) {
              jm_code("""        if (%varName%_association == null) return false;
        if (!%varName%_association.isEmpty()) return false;
        if (%varName%_cache == null) return true;
        int length = %varName%_association.size();
        if (%isLoadedVarName% && length != %varName%.size()) return true;
        if (length != %varName%_cache.size()) return true;
        for (int i = 0;i < length;i++) {
            Long id = %varName%_association.get(i).getId();
            Date updated = %varName%_association.get(i).entity_getUpdated();
            if (%isLoadedVarName% && !id.equals(%varName%.get(i))) return true;
            if (!id.equals(%varName%_cache.get(i).id)) return true;
            if (!updated.equals(%varName%_cache.get(i).entity_info.updated)) {
                return true;
            }
        }
        return false;
""", Map("%varName%" -> varName,
    "%isLoadedVarName%" -> entity_ref_is_loaded_var_name(attr)))
            }
          } else if (is_query_property(attr)) {
            jm_private_method("boolean is_update_cache_%s()".format(attrName)) {
              jm_code("""        if (%varName%_association == null) return false;
        if (!%varName%_association.isEmpty()) return false;
        if (%varName%_cache == null) return true;
        int length = %varName%_association.size();
        if (length != %varName%_cache.size()) return true;
        for (int i = 0;i < length;i++) {
            Long id = %varName%_association.get(i).getId();
            Date updated = %varName%_association.get(i).entity_getUpdated();
            if (!id.equals(%varName%_cache.get(i).id)) return true;
            if (!updated.equals(%varName%_cache.get(i).entity_info.updated)) {
                return true;
            }
        }
        return false;
""", Map("%varName%" -> varName,
    "%isLoadedVarName%" -> entity_ref_is_loaded_var_name(attr)))
            }
          } else {
            jm_private_method("boolean is_update_cache_%s()", attrName) {
              jm_code("""        if (%varName%_association == null) return false;
        if (!%varName%_association.isEmpty()) return false;
        if (%varName%_cache == null) return true;
        int length = %varName%_association.size();
        if (length != %varName%_id.size()) return true;
        if (length != %varName%_cache.size()) return true;
        for (int i = 0;i < length;i++) {
            Long id = %varName%_association.get(i).getId();
            Date updated = %varName%_association.get(i).entity_getUpdated();
            if (!id.equals(%varName%_id.get(i))) return true;
            if (!id.equals(%varName%_cache.get(i).id)) return true;
            if (!updated.equals(%varName%_cache.get(i).entity_info.updated)) {
                return true;
            }
        }
        return false;
""", Map("%varName%" -> varName))
            }
          }
        }

        def make_multi_restore {
          if (is_owned_property(attr) || is_query_property(attr)) {
            jm_protected_final_void_method("restore_" + attrName + "()") {
              jm_code("""        // multi owned or query
        if (is_update_cache_%varName%()) {
            %varName%_cache = new ArrayList<%docName%>();
            for (%entityName% entity: %varName%_association) {
                %varName%_cache.add(entity.make_document());
            }
            %varName%_cache_timestamp = new Date();
        } else {
            %varName%_cache_timestamp = null;
            %varName%_cache = null;
        }
""", Map("%varName%" -> varName,
    "%entityName%" -> java_element_type(attr),
    "%docName%" -> java_doc_element_type(attr)))
            }
          } else {
            jm_protected_final_void_method("restore_" + attrName + "()") {
              jm_code("""        // multi plain
        if (is_update_cache_%varName%()) {
            %varName%_cache = new ArrayList<%docName%>();
            for (%entityName% entity: %varName%_association) {
                %varName%_id.add(entity.getId());
                %varName%_cache.add(entity.make_document());
            }
            %varName%_cache_timestamp = new Date();
        } else {
            %varName%_cache_timestamp = null;
            %varName%_cache = null;
        }
""", Map("%varName%" -> varName,
    "%entityName%" -> java_element_type(attr),
    "%docName%" -> java_doc_element_type(attr)))
            }
          }
        }

        def make_multi_get {
          jm_public_method("%s get%s()", java_type(attr), attrName.capitalize) {
            jm_pln("fill_%s();".format(attrName))
            jm_if_else(refVarName + " != null") {
              jm_pln("return Collections.unmodifiableList(%s);", refVarName)
            }
            jm_else {
              jm_pln("return Collections.emptyList();")
            }
          }
        }

        def make_multi_query {
          if (is_query_property(attr)) {
            jm_public_method("%sQuery query%s(PersistenceManager pm)", attr.elementTypeName, attrName.capitalize) {
              jm_return("new %sQuery(pm)", attr.elementTypeName)
            }
            if (is_logical_operation(entityType)) {
              jm_code("""
    public class %entityName%Query {
        private final PersistenceManager pm;

        public %entityName%Query(PersistenceManager pm) {
            this.pm = pm;
        }

        public %entityName%Chunks execute() {
            return execute(1000);
        }

        public %entityName%Chunks execute(int length) {
            return new %entityName%Chunks(length, pm);
        }
    }

    public class %entityName%Chunks {
        private final int length;
        private final PersistenceManager pm;
        private final List<%entityName%> entities = new ArrayList<%entityName%>();
        private int index = 0;

        public %entityName%Chunks(int length, PersistenceManager pm) {
            this.length = length;
            this.pm = pm;
            Query query = pm.newQuery(%entityName%.class);
            query.setFilter("%backRefIdVarName% == idParam");
            query.setOrdering("updated desc");
            query.declareParameters("%idTypeName% idParam");
            Set<%idTypeName%> registered = new HashSet<%idTypeName%>();
            List<%entityName%> candidates = (List<%entityName%>)query.execute(getId());
            for (%entityName% candidate: candidates) {
                if (registered.add(candidate.getId())) {
                    if (candidate.entity_getRemoved() == null) {
                        this.entities.add(candidate);
                    }
                }
            }
        }

        public boolean hasNext() {
            return length == index;
        }

        public List<%entityName%> next() {
            List<%entityName%> chunk = new ArrayList<%entityName%>();
            int last = Math.min(index + length, entities.size());
            for (int i = index;i < last;i++) {
                chunk.add(entities.get(i));
            }
            index = last;
            return chunk;
        }
    }
""", Map("%entityName%" -> attr.elementTypeName,
    "%refIdVarName%" -> entityType.entity.idName,
    "%idTypeName%" -> entityType.entity.idAttr.typeName,
    "%backRefIdVarName%" -> back_reference_var_name(attr)))
            } else {
              jm_code("""
    public class %entityName%Query {
        private final PersistenceManager pm;

        public %entityName%Query(PersistenceManager pm) {
            this.pm = pm;
        }

        public %entityName%Chunks execute() {
            return execute(1000);
        }

        public %entityName%Chunks execute(int length) {
            return new %entityName%Chunks(length, pm);
        }
    }

    public class %entityName%Chunks {
        private final int length;
        private final PersistenceManager pm;
        private final List<%entityName%> entities;
        private int index = 0;

        public %entityName%Chunks(int length, PersistenceManager pm) {
            this.length = length;
            this.pm = pm;
            Query query = pm.newQuery(%entityName%.class);
            query.setFilter("%backRefIdVarName% == idParam");
            query.declareParameters("%idTypeName% idParam");
            this.entities = (List<%entityName%>)query.execute(getId());
        }

        public boolean hasNext() {
            return length == index;
        }

        public List<%entityName%> next() {
            List<%entityName%> chunk = new ArrayList<%entityName%>();
            int last = Math.min(index + length, entities.size());
            for (int i = index;i < last;i++) {
                chunk.add(entities.get(i));
            }
            index = last;
            return chunk;
        }
    }
""", Map("%entityName%" -> attr.elementTypeName,
    "%refIdVarName%" -> entityType.entity.idName,
    "%idTypeName%" -> entityType.entity.idAttr.typeName,
    "%backRefIdVarName%" -> back_reference_var_name(attr)))
            }
          }
        }

        def make_multi_set {
          if (is_settable(attr)) {
            jm_public_void_method("set%s(%s %s)", attrName.capitalize, java_type(attr), paramName) {
              jm_assign_this(refVarName, "new ArrayList<%s>(%s)", attr.elementTypeName, paramName)    
              if (is_owned_property(attr)) {
                jm_pln("// TODO crates owned multi entites.")
              } else if (is_query_property(attr)) {
                jm_pln("// TODO crates query multi entites.")
              } else {
                jm_for(jdo_element_type(attr) + " element: this." + refVarName) {
                  jm_pln("this.%s..add(element.get());", jdoVarName, entityType.entity.idName.capitalize) 
                } 
              }
            }
          }
        }

        make_multi_fill
        make_multi_is_update_cache
        make_multi_restore
        make_multi_get
        make_multi_query
        make_multi_set
      } // make_multi_reference_attribute_method

      def make_multi_part_attribute_method(partType: PEntityPartType) {
        make_pm_delegate_method("fill_" + attrName)
        jm_protected_final_void_method("fill_" + attrName + "(PersistenceManager pm)") {
          jm_if_else(jdoVarName + " != null && " + partVarName + " == null") {
            jm_pln("List<org.w3c.dom.Element> elements = Util.getElements(%s.toString());", partVarName);
            jm_for("org.w3c.dom.Element element: elements") {
              jm_pln("%s.add(new %s(element, pm));", partVarName, java_element_type(attr));
              jm_else_if_else(jdoVarName + " == null && " + partVarName + " == null") {
              }
              jm_else {
              }
            }
            jm_protected_final_void_method("restore_" + attrName + "()") {
              jm_if(partVarName + " != null") {
                jm_var_new_StringBuilder
                jm_append_String("<list>")
                jm_for(java_element_type(attr) + " element: " + partVarName) {
                  jm_pln("element.entity_asXmlString(buf);")
                }
                jm_append_String("</list>")
                jm_assign(jdoVarName, "new Text(buf.toString());"); 
              }
            }
            jm_public_method("public " + java_type(attr) + " get" + attrName.capitalize + " ()") {
              jm_pln("fill_%s();", attrName)
              jm_if_else(partVarName + " != null") {
                jm_pln("return Collections.unmodifiableList(%s);", partVarName)
              }
              jm_else {
                jm_pln("return Collections.emptyList();")
              }
            }
            if (is_settable(attr)) {
              jm_public_void_method("set" + attrName.capitalize + "(" + java_type(attr) + " " + paramName + ")") {
                jm_assign_this(partVarName, "new ArrayList<%s>(%s)", java_element_type(attr), paramName)  
              }
              jm_public_void_method("public void add" + attrName.capitalize + "(" + java_element_type(attr) + " " + paramName + ")") {
                jm_if_null("this." + partVarName) {
                  jm_assign_this(partVarName, "new ArrayList<%s>()", java_element_type(attr)) 
                }
                jm_pln("this.%s.add(%s);", partVarName, paramName) 
              }
            }
          }
        } // make_multi_reference_attribute_method

        def make_multi_powertype_attribute_method(powertypeType: PPowertypeType) { // XXX
          make_pm_delegate_method("fill_" + attrName)
          jm_protected_final_void_method("fill_" + attrName + "(PersistenceManager pm)") {
            jm_if_else(jdoVarName + " != null && " + partVarName + " == null") {
              jm_pln("List<org.w3c.dom.Element> elements = Util.getElements(%s.toString());", partVarName)
              jm_for("org.w3c.dom.Element element: elements") {
                jm_pln("%s.add(new %s(element, pm));", partVarName, java_element_type(attr))
              }
            }
            jm_else_if_else(jdoVarName + " == null && " + partVarName + " == null") {
            }
            jm_else {
            }
          }
          jm_protected_final_void_method("restore_" + attrName + "()") {
            jm_if(partVarName + " != null") {
              jm_var_new_StringBuilder
              jm_append_String("<list>")
              jm_for(java_element_type(attr) + " element: " + partVarName) {
                jm_pln("element.entity_asXmlString(buf);")
              }
              jm_append_String("</list>")
              jm_assign(jdoVarName, "new Text(buf.toString());") 
            }
          }
          jm_public_get_list_method_prologue(java_type(attr), attrName, partVarName) {
            jm_pln("fill_%s()", attrName)
          }
          if (is_settable(attr)) {
            jm_public_set_list_method(attrName, java_element_type(attr), paramName, partVarName)
            jm_public_add_list_element_method(attrName, java_element_type(attr), paramName, partVarName)
          }
        }

        def make_multi_value_attribute_method {
          jm_public_get_list_method(java_type(attr), attrName, partVarName)
          if (is_settable(attr)) {
            jm_public_set_list_method(attrName, java_element_type(attr), paramName, partVarName)
            jm_public_add_list_element_method(attrName, java_element_type(attr), paramName, partVarName)
          }
        }

        def make_mapping_multi_value_attribute_method(getter: String, setter: String) {
          jm_public_method("%s get%s()", java_type(attr), attrName.capitalize) {
            jm_if_else_not_null(attrName) {
              jm_var_List_new_ArrayList("result", attr.elementTypeName); 
              jm_for(jdo_element_type(attr), "elem", attrName) {
                jm_pln("result.add(%s);", getter.format("elem"))
              }
              jm_return("result")
            }
            jm_else {
              jm_return("Collections.emptyList()");
            }
          }
          if (is_settable(attr)) {
            jm_public_set_list_method(attrName, java_element_type(attr), paramName, partVarName)
            jm_public_add_list_element_method(attrName, java_element_type(attr), paramName, partVarName)
          }
        }

        if (attr.isHasMany) {
          attr.attributeType match {
            case e: PEntityType     => make_multi_reference_attribute_method(e)
            case p: PEntityPartType => make_multi_part_attribute_method(p)
            case p: PPowertypeType  => make_multi_powertype_attribute_method(p)
            case v: PByteType       => make_mapping_multi_value_attribute_method("%s.byteValue()", "%s.shortValue()")
            case v: PIntegerType    => make_mapping_multi_value_attribute_method("new BigInteger(%s)", "%s.toString()")
            case v: PDecimalType    => make_mapping_multi_value_attribute_method("new BigDecimal(%s)", "%s.toString()")
            case _                  => make_multi_value_attribute_method
          }
        } else {
          attr.attributeType match {
            case e: PEntityType     => make_single_reference_attribute_method(e)
            case p: PEntityPartType => make_single_part_attribute_method(p)
            case p: PPowertypeType  => make_single_powertype_attribute_method(p)
            case v: PByteType       => make_mapping_single_value_attribute_method("%s.byteValue()", "%s.shortValue()")
            case v: PIntegerType    => make_mapping_single_value_attribute_method("new BigInteger(%s)", "%s.toString()")
            case v: PDecimalType    => make_mapping_single_value_attribute_method("new BigDecimal(%s)", "%s.toString()")
            case _                  => make_single_value_attribute_method
          }
        }
      }
    }
  } // make_attribute_method

  def make_pm_delegate_method(name: String) {
    jm_private_method("void " + name + "()") {
      jm_var("PersistenceManager", "pm", "Util.getPersistenceManager()")
      jm_try {
        jm_pln("%s(pm);".format(name))
      }
      jm_finally {
        jm_pln("pm.close();")
      }
    }
  }

  def make_null_constructor {
    jm_public_constructor(name) {
    }
  }

  def make_init_method {
    jm_public_method("void init_document(%s doc, PersistenceManager pm)", documentName) {
      for (attr <- attributes) {
        make_update_attribute(attr)
      }
      jm_pln("entity_fill(pm);");
    }
  }

  def make_update_method {
    jm_public_void_method("update_document(%s doc, PersistenceManager pm)", documentName) {
      jm_if_not_null(idName) {
        jm_if_not_equals_expr("doc.%s", idName)("get%s()", idName.capitalize) {
          jm_pln("throw new IllegalArgumentException(\"XXX\");")
        }
      }
      for (attr <- attributes if !attr.isId) {
        make_update_attribute(attr)
      }
    }
  }

  def make_update_attribute(attr: PAttribute) {
    val attrName = attr.name;
    val paramName = attr.name;
    val varName = var_name(attr)
    val jdoVarName = entity_ref_jdo_var_name(attr)
    val refVarName = entity_ref_assoc_var_name(attr)
    val partVarName = entity_ref_part_var_name(attr)
    val powerVarName = entity_ref_powertype_var_name(attr)
    val fromName = "doc." + varName
    val toName = "this." + varName

    def make_update_attribute_one {
      def update(converter: String) {
        jm_if_else_null(fromName) {
          jm_assign_null(toName)
        }
        jm_else {
          jm_assign(toName, converter.format(fromName)) 
        }
      }

      attr.attributeType match { // sync PDomainServiceEntity
        case t: PDateTimeType => {
          jm_if("doc.%s != null || doc.%s_date != null || doc.%s_time != null || doc.%s_now", varName, varName, varName, varName) {
            jm_assign_this(varName, "Util.makeDateTime(doc.%s, doc.%s_date, doc.%s_time, doc.%s_now)", varName, varName, varName, varName)
          }
        }
        case e: PEntityType => {
          jm_if_not_null("doc." + pContext.variableName4RefId(attr)) {
            jm_assign_this(jdoVarName, "doc." + pContext.variableName4RefId(attr))
          }
        }
        case p: PEntityPartType => {
        }
        case p: PPowertypeType => {
        }
        case v: PByteType    => update("%s.shortValue()")
        case v: PIntegerType => update("%s.toString()")
        case v: PDecimalType => update("%s.toString()")
        case _ => jm_assign_this(varName, "doc." + varName)
      }
    }

    def make_update_attribute_many {
      def update(converter: String) {
        jm_assign_new_ArrayList(toName, jdo_element_type(attr))
        jm_for(attr.elementTypeName, "elem", fromName) {
          jm_pln("%s.add(%s);", toName, converter.format("elem")) 
        }
      }

      attr.attributeType match { // sync PDomainServiceEntity
        case t: PDateTimeType => { // XXX
          jm_if("doc.%s != null || doc.%s_date != null || doc.%s_time != null || doc.%s_now", varName, varName, varName, varName) {
            jm_assign_this(varName, "Util.makeDateTime(doc.%s, doc.%s_date, doc.%s_time, doc.%s_now)", varName, varName, varName, varName)
          }
        }
        case e: PEntityType => {
          // make_reference_key_unencoded_string
          if (is_owned_property(attr)) {
            jm_pln("// TODO owned")
          } else if (is_query_property(attr)) {
            jm_pln("// TODO query")
          } else {
            jm_pln("// plain")
            val idVarName = "this." + jdoVarName
            jm_assign_new_ArrayList(idVarName, e.entity.idAttr.elementTypeName, "doc." + pContext.variableName4RefId(attr))
          }
        }
        case p: PEntityPartType => {
          jm_if_not_null("doc." + varName) {
            jm_assign_this_new_ArrayList(partVarName, java_element_type(attr))
            jm_for(java_doc_element_type(attr) + " element: doc." + attrName) {
              jm_pln("this.%s.add(new %s(element, pm));", partVarName, java_element_type(attr)) 
            }
          }
        }
        case p: PPowertypeType => error("not supported yet")
        case v: PByteType      => update("%s.shortValue()")
        case v: PIntegerType   => update("%s.toString()")
        case v: PDecimalType   => update("%s.toString()")
        case _ => jm_assign_this_new_ArrayList(varName, jdo_element_type(attr), "doc." + varName)
      }

      def make_update_owned_property_many {
        jm_pln("// TODO owned many")
      }

      def make_update_owned_property_one {
        jm_pln("// TODO owned one")
      }

      def make_update_query_property_many {
        jm_pln("// TODO owned many")
      }

      def make_update_query_property_one {
        jm_pln("// TODO owned one")
      }

      if (is_settable(attr)) {
        if (is_owned_property(attr)) {
          if (attr.isHasMany) {
            make_update_owned_property_many
          } else {
            make_update_owned_property_one
          }
        } else if (is_query_property(attr)) {
          if (attr.isHasMany) {
            make_update_query_property_many
          } else {
            make_update_query_property_one
          }
        } else {
          if (attr.isHasMany) {
            make_update_attribute_many
          } else {
            make_update_attribute_one
          }
        }
      }
    }
  } // make_update_attribute

  def make_document_method {
    def make_attribute_plain(attr: PAttribute) {
      make_attribute_common(attr, true)
    }

    def make_attribute_shallow(attr: PAttribute) {
      make_attribute_common(attr, false)
    }

    def make_attribute_common(attr: PAttribute, isDeepCopy: Boolean) {
      val attrName = attr.name;
      val varName = var_name(attr)
      val jdoVarName = entity_ref_jdo_var_name(attr)
      val refVarName = entity_ref_assoc_var_name(attr)
      val partVarName = entity_ref_part_var_name(attr)
      val powerVarName = entity_ref_powertype_var_name(attr)
      val fromName = "this." + attrName
      val toName = "doc." + attrName

      def make_one {
        def update(converter: String) {
          jm_if_else_null(fromName) {
            jm_assign_null(toName)
          }
          jm_else {
            jm_assign(toName, converter.format(fromName)) 
          }
        }

        attr.attributeType match {
          case t: PDateTimeType => {
            jm_assign("doc." + varName, "this." + varName)
            jm_assign("doc.%s_date".format(varName), "Util.makeDate(this.%s)", varName)
            jm_assign("doc.%s_time".format(varName), "Util.makeTime(this.%s)", varName)
            jm_assign("doc.%s_now".format(varName), "false")
          }
          case e: PEntityType => {
            def make_id {
              jm_assign("doc." + pContext.variableName4RefId(attr), "this." + jdoVarName) 
            }

            def make_get_id {
              jm_assign("doc." + pContext.variableName4RefId(attr), "this.%s.getId()", refVarName)
            }

            def make_deep {
              jm_pln("fill_%s();".format(attrName))
              jm_if_not_null(refVarName) {
                jm_assign("doc.%s".format(varName), "%s.make_document()", refVarName)
              }
            }

            def make_shallow {
              jm_pln("fill_%s();".format(attrName))
              jm_if_not_null(refVarName) {
                jm_assign("doc.%s".format(varName), "%s.make_document_shallow()", refVarName)
              }
            }

            if (is_owned_property(attr)) {
              jm_pln("fill_%s();".format(attrName))
              jm_if_not_null(refVarName) {
                make_get_id
                jm_pln("doc.%s = %s.make_document();".format(varName, refVarName))
              }
            } else if (is_query_property(attr)) {
              if (attr.isComposition) {
                make_deep
              } else if (attr.isAggregation) {
                if (isDeepCopy) {
                  make_shallow
                }
              }
            } else {
              if (attr.isComposition) {
                jm_pln("fill_%s();".format(attrName))
                jm_if_not_null(refVarName) {
                  make_get_id
                  jm_pln("doc.%s = %s.make_document();".format(varName, refVarName))
                }
              } else if (attr.isAggregation) {
                make_id
                if (isDeepCopy) {
                  make_shallow
                }
              } else {
                make_id
              }
            }
          }
          case p: PEntityPartType => {
            jm_pln("doc.%s = this.%s.make_document();", varName, refVarName)
          }
          case p: PPowertypeType => {
            jm_if_not_null("this." + varName) {
              jm_pln("doc.%s = this.%s.getKey()", varName, powerVarName)
            }
          }
          case v: PByteType    => update("%s.byteValue()")
          case v: PIntegerType => update("new BigInteger(%s)")
          case v: PDecimalType => update("new BigDecimal(%s)")
          case _ => jm_pln("doc.%s = this.%s;", varName, varName)
        }
      } // make_one

      def make_many {
        def update(converter: String) {
          jm_if_not_null(fromName) {
            jm_for(jdo_element_type(attr), "elem", fromName) {
              jm_pln("%s.add(%s);", toName, converter.format("elem")) 
            }
          }
        }

        attr.attributeType match {
        case t: PDateTimeType => { // XXX
          jm_pln("doc.%s = %s;", varName, varName)
          jm_pln("doc.%s_date = Util.makeDate(%s);", varName, varName)
          jm_pln("doc.%s_time = Util.makeTime(%s);", varName, varName)
          jm_pln("doc.%s_now = false;")
        }
        case e: PEntityType => {
          def make_id {
            jm_if_not_null("this." + jdoVarName) {
              jm_pln("doc.%s.addAll(this.%s);", pContext.variableName4RefId(attr), jdoVarName)
            }
          }

          def make_get_id {
            jm_if_not_null("this." + jdoVarName) {
              jm_pln("doc.%s.addAll(this.%s);", pContext.variableName4RefId(attr), jdoVarName) 
            }
          }

          def make_deep {
            jm_pln("fill_%s();".format(attrName))
            jm_for("%s entity: %s".format(e.entity.name, refVarName)) {
              jm_pln("doc.%s.add(entity.make_document());".format(varName))
            }
          }

          def make_shallow {
            jm_pln("fill_%s();".format(attrName))
            jm_for("%s entity: %s".format(e.entity.name, refVarName)) {
              jm_pln("doc.%s.add(entity.make_document_shallow());".format(varName))
            }
          }

          if (is_owned_property(attr)) {
            jm_for("%s entity: get%s()".format(e.entity.name, attrName.capitalize)) {
              jm_pln("doc.%s.add(entity.make_document());".format(varName))
              jm_pln("doc.%s.add(entity.getId());".format(pContext.variableName4RefId(attr)))
            }
          } else if (is_query_property(attr)) {
            if (attr.isComposition) {
              make_deep
            } else if (attr.isAggregation) {
              if (isDeepCopy) {
                make_shallow
              }
            }
          } else {
            if (attr.isComposition) {
              make_id
              make_deep
            } else if (attr.isAggregation) {
              make_id
              if (isDeepCopy) {
                make_shallow
              }
            } else {
              make_id
            }
          }
        }
        case p: PEntityPartType => {
          jm_pln("fill_%s();", attrName)
          jm_if_not_null("this." + partVarName) {
            jm_for(java_element_type(attr) + " element: " + partVarName) {
              jm_pln("doc.%s.add(element.make_document());", attrName)
            }
          }
        }
        case v: PByteType    => update("%s.byteValue()")
        case v: PIntegerType => update("new BigInteger(%s)")
        case v: PDecimalType => update("new BigDecimal(%s)")
        case _ => {
          jm_if_not_null("this." + varName) {
            jm_pln("doc.%s..addAll(this.%s);", varName, varName)  
          }
        }
        }

        if (is_settable(attr)) {
          if (attr.isHasMany) {
            make_many
          } else {
            make_one
          }
        }
      }
    }

    def make_document_common(methodName: String, makeAttr: PAttribute => Unit) {
      jm_public_method("%s %s()".format(documentName, methodName)) {
        jm_var_new(documentName, "doc")
        jm_pln("doc.%s = get%s();", idName, idName.capitalize)
        for (attr <- attributes if !attr.isId) {
          makeAttr(attr)
        }
        jm_pln("doc.entity_info = entity_getEntityInfo();")
        jm_return("doc")
      }
    }

    def make_document_plain {
      make_document_common("make_document", make_attribute_plain)
    }

    def make_document_shallow {
      make_document_common("make_document_shallow", make_attribute_shallow)
    }

    make_document_plain
    make_document_shallow
  } // make_document_method

  def make_make_persistent_method {
    jm_public_method("void make_persistent(PersistenceManager pm)") {
      if (is_logical_operation) {
        val varName = var_name(idAttr)
        if (notNull(modelEntity.jdo.table)) {
          jm_var_String("kind", "\"" + modelEntity.jdo.table + "\"")
        } else {
          jm_var_String("kind", "getClass().getSimpleName()")
        }
        jm_var_null("Key", "parent_key")
        jm_if_else_not_null("_parent_key") {
          jm_pln("parent_key = _parent_key;")
        }
        jm_else_if_not_null("key") {
          jm_pln("parent_key = key.getParent();")
        }
        idAttr.idPolicy match {
          case SMAutoIdPolicy => {
            jm_if_else_not_null("parent_key") {
              jm_pln("this.key = Util.allocateKey(kind, parent_key);")
            }
            jm_else {
              jm_pln("this.key = Util.allocateKey(kind);")
            }
            jm_if_null(varName) {
              jm_pln("%s = this.key.getId();".format(varName))
            }
          }
          case SMApplicationIdPolicy => {
            jm_if_else_not_null("parent_key") {
              jm_pln("this.key = Util.allocateAppUuidKey(kind, %s, parent_key);".format(varName))
            }
            jm_else {
              jm_pln("this.key = Util.allocateAppUuidKey(kind, %s);".format(varName))
            }
          }
        }
        jm_pln("entity_sync();")
      }
      jm_pln("int retryCount = 10; // XXX")
      jm_pln("int waitMillSec = 500; // XXX")
      jm_while("retryCount-- > 0") {
        jm_try {
          jm_pln("pm.makePersistent(this);")
          jm_return
        }
        jm_catch_end("Exception e") {
          jm_try {
            jm_pln("Thread.sleep(waitMillSec);")
          }
          jm_catch_nop("InterruptedException e2")
        }
      }
    }
    jm_pln("pm.makePersistent(this);")
  }

  def make_delete_persistent_method {
    jm_public_method("void delete_persistent(PersistenceManager pm)") {
      if (is_logical_operation) {
        jm_pln("_entity_removed = _entity_updated = new Date();")
        jm_pln("pm.makePersistent(this);")
      } else {
        jm_pln("pm.deletePersistent(this);")
      }
    }
  }

  def make_get_entity_method {
    jm_public_method("static %s get_entity(%s, id, PersistenceManager pm)", name, idAttr.typeName) {
      if (is_logical_operation) {
        jm_code("""Query query = pm.newQuery(%entityName%.class);
        query.setFilter("%idAttrName% == idParam");
        query.setOrdering("updated desc");
        query.declareParameters("String idParam");
        List<%entityName%> candidates = (List<%entityName%>)query.execute(id);
        if (candidates == null || candidates.size() == 0) {
            return null;
        }
        %entityName% entity = candidates.get(0);
        if (entity._entity_removed != null) {
            return null;
        }
""", Map("%entityName%" -> name,
    "%idAttrName%" -> idAttr.name))
      } else if (modelEntity.appEngine.use_key) {
        if (notNull(modelEntity.jdo.table)) {
          jm_var_String("kind", "\"" + modelEntity.jdo.table + "\"")
        } else {
          jm_var_String("kind", "%s.class.getSimpleName()".format(name))
        }
        // XXX parent
        jm_pln("Key key = KeyFactory.createKey(kind, id);")
        jm_var(name, "entity", "pm.getObjectById(" + name + ".class, key)")
      } else {
          jm_var(name, "entity", "pm.getObjectById(" + name + ".class, id)")
      }
      jm_pln("entity.entity_fill(pm);");
      jm_pln("return entity;")
    }
  }

  def make_get_entities_method {
    jm_public_method("static List<%s> get_entities(List<%s> keys, PersistenceManager pm)", name, idAttr.typeName) {
      jm_var_List_new_ArrayList("entities", name)
      jm_for(idAttr.typeName, "key", "keys") {
        jm_var(name, "entity", "%s.get_entity(key, pm)".format(name))
        jm_if_not_null("entity") {
          jm_pln("entities.add(entity);")
        }
      }
      jm_return("entities")
    }
  }

  def make_delete_entity_method {
    jm_public_method("static void delete_entity(%s key, PersistenceManager pm)".format(idAttr.typeName)) {
      jm_pln("Transaction tx = pm.currentTransaction();")
      jm_try {
        jm_pln("%s entity = get_entity(key, pm);", name)
        jm_pln("entity.delete_persistent(pm);")
        jm_pln("tx.commit();")
      }
      jm_finally {
        jm_if("tx.isActive()") {
          jm_pln("tx.rollback();")
        }
      }
    }
  }

  def make_entity_fill_method {
    jm_method("void entity_fill(PersistenceManager pm)") { // XXX
      for (attr <- attributes) {
        attr.attributeType match {
        case e: PEntityType => {
          jm_if("false") {
            jm_pln("fill_" + attr.name + "(pm);")
          }
        }
        case p: PEntityPartType => {
          jm_if("true") {
            jm_pln("fill_" + attr.name + "(pm);")
          }
        }
        case p: PPowertypeType => {
          jm_if("true") {
            jm_pln("fill_" + attr.name + "(pm);")
          }
        }
        case _ => //
        }
      }
    }
  }

  def make_entity_restore_method {
    jm_method("void entity_restore()") {
      for (attr <- attributes) {
        attr.attributeType match {
        case e: PEntityType => {
          jm_pln("restore_" + attr.name + "();")
        }
        case p: PEntityPartType => {
          jm_pln("restore_" + attr.name + "();")
        }
        case p: PPowertypeType => {
          jm_pln("restore_" + attr.name + "();")
        }
        case _ => //
        }
      }
    }
  }

  def make_entity_info {
    var code = """
  public Date entity_getCreated() {
    return _entity_created;
  }

  public Date entity_getUpdated() {
    return _entity_updated;
  }

  public Date entity_getRemoved() {
    return _entity_removed;
  }

  public MDEntityInfo entity_getEntityInfo() {
    ensure_entityInfo();
    return entity_info;
  }

  public void entity_sync() {
    entity_restore();
    if (entity_info == null) {
      if (_entity_info == null) {
        entity_create();
      } else {
        entity_update();
      }
    }
    if (_entity_created == null) {
      _entity_updated = _entity_created = new Date();
    } else {
      _entity_updated = new Date();
    }
  }

  public void entity_create() {
    ensure_entityInfo();
    entity_info.model = entity_model;
    entity_info.create();
    _entity_info = new Text(entity_info.toString());
  }

  public void entity_update() {
    ensure_entityInfo();
    entity_info.model = entity_model;
    entity_info.update();
    _entity_info = new Text(entity_info.toString());
  }

  public void entity_remove() {
    ensure_entityInfo();
    entity_info.model = entity_model;
    entity_info.remove();
    _entity_info = new Text(entity_info.toString());
  }

  private void ensure_entityInfo() {
    if (entity_info != null) {
      return;
    }
    if (_entity_info != null) {
      entity_info = MDEntityInfo.reconstituteMDEntityInfo(_entity_info.getValue());
    } else {
      entity_info = new MDEntityInfo();
    }
  }

  // XXX private
  public final static MDEntityInfo.MDModel entity_model = new MDEntityInfo.MDModel();

  static {
    entity_model.name = "";
    entity_model.version = "";
    entity_model.build = "";
    MDEntityInfo.MDModel.MDEntity entity = new MDEntityInfo.MDModel.MDEntity();
    entity.name = "%entityName%";
    entity.version = "";
    entity.build = "";
%addAttributes%
    entity_model.entity = entity;
  }
"""
    def add_attributes {
      jm_indent_up
      jm_indent_up
      for (attr <- attributes) {
        val attrName = attr_name(attr)
        val attrType = {
            if (attr.modelAttribute != null) {
              attr.modelAttribute.attributeType.qualifiedName
            } else if (attr.modelAssociation != null) {
              attr.modelAssociation.associationType.qualifiedName
            } else {
              "?"
            }
        }
        val columnName = attr.name
        val columnType = attr.attributeType.objectTypeName
        jm_pln("""entity.addAttribute("%s", "%s", "%s", "%s");""".
            format(attrName, attrType, columnName, columnType))
      }
      jm_indent_down
      jm_indent_down
    }

    jm_code(code, Map("%entityName%" -> qualifiedName))
    add_attributes
  }

  final protected def make_entity_xmlString {
    jm_public_method("String entity_xmlString()") {
      jm_var_new_StringBuilder
      jm_pln("entity_asXmlString(buf);")
      jm_return_StringBuilder
    }
  }

  final protected def make_entity_asXmlString {
    jm_public_method("void entity_asXmlString(StringBuilder buf)") {
      jm_append_String("<" + xmlElementName + " xmlns=\"" + xmlNamespace + "\">")
      for (attr <- attributes) {
        jm_pln(make_get_string_element(attr) + ";")
      }
      jm_append_String("</" + xmlElementName + ">")
    }
  }

  final protected def make_get_string_element(attr: PAttribute): String = {
    if (attr.isHasMany) {
      "build_xml_element(\"" + attr.name + "\", " + make_get_string_list_property(attr) + ", buf)"
    } else {
      "build_xml_element(\"" + attr.name + "\", " + make_get_string_property(attr) + ", buf)"
    }
  }

  final protected def make_get_string_property(name: String) = {
    "get" + name.capitalize + "_asString()"
  }

  final protected def make_get_xml_string_property(name: String) = {
    "Util.escapeXmlText(" + make_get_string_property(name) + ")"
  }

  final protected def make_get_string_property(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => {
        make_get_string_property(pContext.variableName4RefId(attr))
      }
      case _ => make_get_string_property(attr.name)
    }
  }

  final protected def make_get_string_list_property(name: String) = {
    "get" + name.capitalize + "_asStringList()"
  }

  final protected def make_get_string_list_property(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => {
        make_get_string_list_property(pContext.variableName4RefId(attr))
      }
      case _ => make_get_string_list_property(attr.name)
    }
  }

  final protected def make_single_datatype_get_asString(attrName: String, expr: String) {
    make_single_datatype_get_asString(attrName, attrName, expr)
  }

  final protected def make_single_datatype_get_asString(attrName: String, varName: String, expr: String) {
    jm_method("public String get" + attrName.capitalize + "_asString()") {
      jm_return(expr)
    }
  }

  final protected def make_single_object_get_asString(attrName: String, expr: String) {
    make_single_object_get_asString(attrName, attrName, expr)
  }

  final protected def make_single_object_get_asString(attrName: String, varName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString()") {
      jm_if_else_null(varName) {
        jm_return("\"\"")
      }
      jm_else {
        jm_return(expr)
      }
    }
  }

  final protected def make_multi_get_asString(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString()") {
      jm_if(varName + ".isEmpty()") {
        jm_return("\"\"")
      }
      jm_var_new_StringBuilder
      jm_var(typeName, "last", varName + ".get(" + varName + ".size() - 1)")
      jm_for(typeName + " elem: " + varName) {
        jm_append_expr(expr)
        jm_if("elem != last") {
          jm_append_String(", ")
        }
      }
      jm_return_StringBuilder
    }
  }

  final protected def make_multi_get_asStringIndex(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString(int index)") {
      jm_if_else(varName + ".size() <= index") {
        jm_return("\"\"")
      }
      jm_else {
        jm_var(typeName, "elem", varName + ".get(index)")
        if ("String".equals(typeName)) {
          jm_return(expr)
        } else {
          jm_return("Util.datatype2string(%s)".format(expr))
        }
      }
    }
  }

  final protected def make_multi_get_asStringList(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("List<String> get" + attrName.capitalize + "_asStringList()") {
      jm_var_List_new_ArrayList("String", "list")
      jm_for(typeName + " elem: " + varName) {
        jm_p("list.add(")
        if ("String".equals(typeName)) {
          jm_p(expr)
        } else {
          jm_p("Util.datatype2string(%s)".format(expr))
        }
        jm_pln(");")
      }
      jm_pln("return list;")
    }
  }

  final protected def make_multi_get_asString_methods(attrName: String, typeName: String, expr: String) {
    make_multi_get_asString_methods(attrName, attrName, typeName, expr)
  }

  final protected def make_multi_get_asString_methods(attrName: String, varName: String, typeName: String, expr: String) {
    make_multi_get_asString(attrName, varName, typeName, expr)
    make_multi_get_asStringIndex(attrName, varName, typeName, expr)
    make_multi_get_asStringList(attrName, varName, typeName, expr)
  }

  final protected def make_build_xml_element() {
    jm_private_method("void build_xml_element(String name, String value, StringBuilder buf)") {
      jm_append_String("<")
      jm_append_expr("name")
      jm_append_String(">")
      jm_append_expr("Util.escapeXmlText(value)")
      jm_append_String("</")
      jm_append_expr("name")
      jm_append_String(">")
    }
    jm_private_method("void build_xml_element(String name, List<String> values, StringBuilder buf)") {
      jm_for("String value: values") {
        jm_pln("build_xml_element(name, value, buf);")
      }
    }
    jm_private_method("void build_xml_element(String name, String[] values, StringBuilder buf)") {
      jm_for("String value: values") {
        jm_pln("build_xml_element(name, value, buf);")
      }
    }
  }

  // XXX consider definition location
  def java_type(anAttr: PAttribute) = {
    anAttr.typeName
  }

  def java_element_type(anAttr: PAttribute) = {
    anAttr.elementTypeName
  }

  def jdo_type(anAttr: PAttribute) = {
    anAttr.jdoTypeName
  }

  def jdo_element_type(anAttr: PAttribute) = {
    anAttr.jdoElementTypeName
  }

  def java_doc_type(anAttr: PAttribute) = {
    if (anAttr.isHasMany) {
      "List<" + java_doc_element_type(anAttr) + ">"
    } else {
      java_doc_element_type(anAttr)
    }
  }

  def java_doc_element_type(anAttr: PAttribute) = {
    anAttr.attributeType match {
      case e: PEntityType => {
        e.entity.documentName
      }
      case p: PEntityPartType => {
        p.part.documentName
      }
      case p: PPowertypeType => "String"
      case _ => java_element_type(anAttr)
    }
  }

/*
  def doc_attr_name(attr: GaejAttribute) = {
    attr.attributeType match {
      case e: GaejEntityType => e.entity.idName
      case _ => attr.name
    }
  }
*/
}
