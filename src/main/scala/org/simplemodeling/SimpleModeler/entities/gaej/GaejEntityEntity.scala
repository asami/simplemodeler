package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import java.util.{GregorianCalendar, TimeZone}
import java.text.SimpleDateFormat
import scala.collection.immutable.ListSet
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.dsl._
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._
import com.asamioffice.goldenport.text.UString.notNull

/*
 * @since   Apr. 10, 2009
 *  version Dec. 18, 2010
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class GaejEntityEntity(aContext: GaejEntityContext) extends GaejEntityObjectEntity(aContext) {
  var modelEntity: SMEntity = null

  //
  protected final def is_settable(attr: GaejAttribute) = {
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

  protected final def is_logical_operation = {
    modelEntity.appEngine.logical_operation
  }

  protected final def is_logical_operation(entityType: GaejEntityType) = {
    entityType.entity.modelEntity.appEngine.logical_operation
  }

  protected final def is_owned_property(attr: GaejAttribute) = {
    attr.modelAssociation != null && 
    attr.modelAssociation.isComposition &&
    modelEntity != null &&
    modelEntity.appEngine.use_owned_property
  }

  protected final def is_query_property(attr: GaejAttribute) = {
    attr.modelAssociation != null && 
    attr.modelAssociation.isQueryReference
  }

  protected final def entity_ref_jdo_var_name(attr: GaejAttribute) = {
    if (is_owned_property(attr)) {
      var_name(attr)
    } else {
      attr.attributeType match {
        case e: GaejEntityType => var_name(attr) + "_id"
        case _ => var_name(attr)
      }
    }
  }

  protected final def entity_ref_is_loaded_var_name(attr: GaejAttribute) = {
    "is_loaded_" + entity_ref_jdo_var_name(attr) 
  }


  protected final def back_reference_var_name(attr: GaejAttribute): String = {
    back_reference_var_name(modelEntity, attr.modelAssociation)
  }

  protected final def back_reference_var_name(source: SMObject, assoc: SMAssociation): String = {
    assoc.backReferenceNameOption match {
      case Some(name) => name
      case None => "_backref_%s_%s".format(gaejContext.termName(source), gaejContext.termName(assoc))
    }
  }

  //
  override protected def write_Content(out: BufferedWriter) {
    val buffer = new JavaTextMaker

    def make_package {
      require (packageName != null)
      if (packageName != "") {
        buffer.print("package ")
        buffer.print(packageName)
        buffer.println(";")
        buffer.println()
      }
    }

    def make_imports {
      var isPrint = false

      def make_import_object(anObject: GaejObjectReferenceType) {
        if (packageName != anObject.packageName) {
          make_import(anObject.qualifiedName)
        }
      }

      def make_import_entity(anEntity: GaejEntityType) {
        if (packageName != anEntity.packageName) {
          make_import(anEntity.qualifiedName)
        }
      }

      def make_import(aName: String) {
          buffer.print("import ")
          buffer.print(aName)
          buffer.println(";")
          isPrint = true
      }

      make_import("java.util.*")
      make_import("java.math.*")
      make_import("javax.jdo.PersistenceManager")
      make_import("javax.jdo.Query")
      make_import("javax.jdo.Transaction")
      make_import("javax.jdo.annotations.*")
      make_import("com.google.appengine.api.datastore.*")
      make_import("com.google.appengine.api.datastore.Key")
      make_import("com.google.appengine.api.users.User")
      if (_baseObject != null) {
        make_import_object(_baseObject)
      }
      val attrs = attributes.filter(_.attributeType.isEntity)
      for (attr <- attrs) {
        val entity = attr.attributeType.asInstanceOf[GaejEntityType]
        make_import_entity(entity)
      }
      if (isPrint)
        buffer.println()
    }

    make_package
    make_imports
    writeModel(buffer)
    out.append(buffer.toString)
    out.flush()
  }

  final def writeModel(buffer: JavaTextMaker) {
    // common
    def make_pm_delegate_method(name: String) {
      buffer.method("private void " + name + "()") {
        buffer.makeVar("pm", "PersistenceManager", "Util.getPersistenceManager()")
        buffer.makeTry {
          buffer.println("%s(pm);".format(name))
        }
        buffer.makeFinally {
          buffer.println("pm.close();")
        }
      }
    }

    //
    def make_attribute_variables {
      def id_attributes {
        val varName = var_name(idAttr)
        if (is_logical_operation) {
          buffer.println("@PrimaryKey")
          buffer.println("@Persistent")
          buffer.println("private Key key;")
          buffer.println("@Persistent")
          buffer.println("private %s %s;".format(idAttr.typeName, varName))
        } else if (modelEntity.appEngine.use_key) {
          idAttr.idPolicy match {
            case SMAutoIdPolicy => {
              buffer.println("@PrimaryKey")
              buffer.println("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
              buffer.println("private Key key;")
            }
            case SMApplicationIdPolicy => {
              buffer.println("@PrimaryKey")
              buffer.println("@Persistent")
              buffer.println("private Key key;")
            }
          }
        } else {
          idAttr.idPolicy match {
            case SMAutoIdPolicy => {
              buffer.println("@PrimaryKey")
              buffer.println("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
              buffer.println("private Long %s;".format(varName))
            }
            case SMApplicationIdPolicy => {
              buffer.println("@PrimaryKey")
              buffer.println("private String %s;".format(varName))
            }
          }
        }
        buffer.println()
      }

      def non_id_attributes {
        for (attr <- attributes if !attr.isId) {
          make_attribute_variable(attr)
          buffer.println()
        }
      }

      def aggregate_attributes {
        def back_reference(source: SMObject, assoc: SMAssociation) {
          // XXX think GaejParticipation
          val idTypeName = "String"
          val typeName = assoc.backReferenceMultiplicityOption match {
            case Some(m: SMMultiplicityOne) => idTypeName
            case Some(m: SMMultiplicityZeroOne) => idTypeName
            case Some(m: SMMultiplicityOneMore) => "List<" + idTypeName + ">"
            case Some(m: SMMultiplicityZeroMore) => "List<" + idTypeName + ">"
            case None => "String" // XXX
          }
          buffer.println("@Persistent")
          buffer.println("private %s %s;".format(typeName, back_reference_var_name(source, assoc)))
          buffer.println()
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

        for (participation <- modelEntity.participations) {
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
        val pkg = modelEntity.parent.asInstanceOf[SMPackage]
        println("pkg name = " + pkg.qualifiedName)
        val modelName = pkg.qualifiedName
        val modelVersion = pkg.version match {
          case "" => "alpha"
          case v => v
        }
        val modelBuild = build
        val className = qualifiedName
        val classVersion = modelEntity.version match {
          case "" => "alpha"
          case v => v
        }
        val classBuild = build
        buffer.println("@Persistent")
        buffer.println("private User _entity_author = null;")
        buffer.println("@Persistent")
        buffer.println("private Date _entity_created = null;")
        buffer.println("@Persistent")
        buffer.println("private Date _entity_updated = null;")
        buffer.println("@Persistent")
        buffer.println("private Date _entity_removed = null;")
        buffer.println("@Persistent")
        buffer.println("private String _entity_model_name = \"%s\";".format(modelName))
        buffer.println("@Persistent")
        buffer.println("private String _entity_model_version = \"%s\";".format(modelVersion))
        buffer.println("@Persistent")
        buffer.println("private String _entity_model_build = \"%s\";".format(modelBuild))
        buffer.println("@Persistent")
        buffer.println("private String _entity_class_name = \"%s\";".format(className))
        buffer.println("@Persistent")
        buffer.println("private String _entity_class_version = \"%s\";".format(classVersion))
        buffer.println("@Persistent")
        buffer.println("private String _entity_class_build = \"%s\";".format(classBuild))
        buffer.println("@NotPersistent")
        buffer.println("private transient Key _parent_key = null;")
        buffer.println("@Persistent")
        buffer.println("private Text _entity_info = null;")
        buffer.println("@NotPersistent")
        buffer.println("private transient MDEntityInfo entity_info;")
        buffer.println("@NotPersistent")
        buffer.println("  private final transient %s entity_context = %s.getFactory().createContext();".format(contextName, factoryName))
        buffer.println()
      }

      id_attributes
      non_id_attributes
      aggregate_attributes
      extension_attribuets
    }

    def make_attribute_variable(attr: GaejAttribute) {
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
        attr.isPersistentOption match {
          case Some(true) => buffer.println("@Persistent")
          case Some(false) => buffer.println("@NotPersistent")
          case None => // do nothing
        }
      }

/*
      // primary key
      def make_key_long {
        buffer.println("@PrimaryKey")
        buffer.println("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
        buffer.print("private Long ")
        buffer.print(jdoVarName)
        buffer.println(";")
      }

      def make_key_unencoded_string {
        buffer.println("@PrimaryKey")
        buffer.print("private String ")
        buffer.print(jdoVarName)
        buffer.println(";")
      }

      def make_key_key {
        buffer.println("@PrimaryKey")
        buffer.println("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
        buffer.print("private Key ")
        buffer.print(jdoVarName)
        buffer.println(";")
      }

      def make_key_as_encoded_string {
        buffer.println("@PrimaryKey")
        buffer.println("@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)")
        buffer.println("@Extension(vendorName=\"datanucleus\", key=\"gae.encoded-pk\", value=\"true\")")
        buffer.print("private String ")
        buffer.print(jdoVarName)
        buffer.println(";")
      }
*/

      // reference
      def make_reference_key_long(entityType: GaejEntityType) {
        make_reference_id_slot(entityType)
//        make_reference_updated_slot(entityType)
        make_reference_object_slot(entityType)
        make_reference_cache_slot(entityType)
      }

      def make_reference_key_unencoded_string(entityType: GaejEntityType) {
        make_reference_id_slot(entityType)
//        make_reference_updated_slot(entityType)
        make_reference_object_slot(entityType)
        make_reference_cache_slot(entityType)
      }

      def make_reference_owned_property(entityType: GaejEntityType) {
        make_reference_owned_slot(entityType)
        make_reference_is_loaded_slot(entityType)
//        make_reference_updated_slot(entityType)
        make_reference_object_slot(entityType)
        make_reference_cache_slot(entityType)
      }

      def make_reference_query_property(entityType: GaejEntityType) {
        make_reference_object_slot(entityType)
        make_reference_cache_slot(entityType)
      }

      def make_reference_id_slot(entityType: GaejEntityType) {
        def id_type = entityType.entity.idAttr.typeName

        val idType = if (attr.isHasMany) {
          "List<" + id_type + ">"
        } else {
          id_type
        }
        make_persistent_annotation
        buffer.print("private ")
        buffer.print(idType)
        buffer.print(" ")
        buffer.print(jdoVarName)
        if (attr.isHasMany) {
          buffer.print(" = new ArrayList<")
          buffer.print(entityType.entity.idAttr.typeName)
          buffer.print(">()")
        }
        buffer.println(";")
      }

      def make_reference_is_loaded_slot(entityType: GaejEntityType) {
        def id_type = entityType.entity.idAttr.typeName

        val idType = if (attr.isHasMany) {
          "List<" + id_type + ">"
        } else {
          id_type
        }
        buffer.println("@NotPersistent")
        buffer.println("private boolean %s;".format(entity_ref_is_loaded_var_name(attr)))
      }

      def make_reference_owned_slot(entityType: GaejEntityType) {
        val refType = entityType.entity.name
        make_persistent_annotation
        if (attr.isHasMany) {
          buffer.println("private List<%s> %s = new ArrayList<%s>();".format(refType, jdoVarName, refType))
        } else {
          buffer.println("private %s %s;".format(refType, jdoVarName))
        }
      }

      def make_reference_updated_slot(entityType: GaejEntityType) {
        make_persistent_annotation
        buffer.println("private Date %s;".format(updatedVarName))
      }

      def make_reference_object_slot(entityType: GaejEntityType) {
        buffer.println("@NotPersistent")
        buffer.print("private transient ")
        buffer.print(jdo_type(attr))
        buffer.print(" ")
        buffer.print(refVarName)
/*
        if (attr.isHasMany) {
          buffer.print(" = new ArrayList<")
          buffer.print(jdo_element_type(attr))
          buffer.print(">()")
        } else {
          buffer.print(" = null")
        }
*/
        buffer.print(" = null")
        buffer.println(";")
      }

      def make_reference_cache_slot(entityType: GaejEntityType) {
        buffer.println("@Persistent")
        buffer.print("private Date")
        buffer.print(" ")
        buffer.print(cacheTimestampVarName)
        buffer.println(" = null;")
        buffer.println("@Persistent")
        buffer.print("private ")
        buffer.print(java_doc_type(attr))
        buffer.print(" ")
        buffer.print(cacheVarName)
        if (attr.isHasMany) {
          buffer.print(" = new ArrayList<")
          buffer.print(java_doc_element_type(attr))
          buffer.print(">()")
        } else {
          buffer.print(" = null")
        }
        buffer.println(";")
      }

      def make_plain_attribute {
        make_persistent_annotation
        buffer.print("private ")
        buffer.print(jdo_type(attr))
        buffer.print(" ")
        buffer.print(jdoVarName)
        if (attr.isHasMany) {
          buffer.print(" = new ArrayList<")
          buffer.print(jdo_element_type(attr));
          buffer.print(">()")
        }
        buffer.println(";")
      }

//      println("Attr %s, kind = %s".format(attr.name, attr.kind)) // 2009-10-28
      attr.kind match {
        case NullAttributeKind => {
          attr.attributeType match {
            case e: GaejEntityType => {
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
            case p: GaejEntityPartType => {
              buffer.println("@Persistent")
              buffer.print("private Text ")
              buffer.print(jdoVarName)
              buffer.println(" = null;")
              buffer.println("@NotPersistent")
              buffer.print("private transient ")
              buffer.print(java_type(attr))
              buffer.print(" ")
              buffer.print(partVarName)
              buffer.println(" = null;")
            }
            case p: GaejPowertypeType => {
              buffer.println("@Persistent")
              buffer.print("private String ")
              buffer.print(jdoVarName)
              buffer.println(" = null;")
              buffer.println("@Persistent")
              buffer.print("private String ")
              buffer.print(entity_ref_label_var_name(attr))
              buffer.println(" = null;")
              buffer.println("@Persistent")
              buffer.print("private Date ")
              buffer.print(entity_ref_updated_var_name(attr))
              buffer.println(" = null;")
              buffer.println("@Persistent")
              buffer.print("private Date ")
              buffer.print(entity_ref_cache_timestamp_var_name(attr))
              buffer.println(" = null;")
              buffer.println("@NotPersistent")
              buffer.print("private transient ")
              buffer.print(java_type(attr))
              buffer.print(" ")
              buffer.print(entity_ref_powertype_var_name(attr))
              buffer.println(" = null;")
            }
            case _ => make_plain_attribute
          }
        }
        case IdAttributeKind => {}
/*
        case IdAttributeKind => {
          attr.idPolicy match {
            case SMAutoIdPolicy => make_key_long
            case SMApplicationIdPolicy => make_key_unencoded_string
          }
        }
*/
        case NameAttributeKind => make_plain_attribute
        case UserAttributeKind => make_plain_attribute
        case TitleAttributeKind => make_plain_attribute
        case SubTitleAttributeKind => make_plain_attribute
        case SummaryAttributeKind => make_plain_attribute
        case CategoryAttributeKind => make_plain_attribute
        case AuthorAttributeKind => {}
        case IconAttributeKind => make_plain_attribute
        case LogoAttributeKind => make_plain_attribute
        case LinkAttributeKind => make_plain_attribute
        case ContentAttributeKind => make_plain_attribute
        case CreatedAttributeKind => {}
        case UpdatedAttributeKind => {}
      }
    }

    def make_attribute_methods {
      def id_methods {
        val attrName = attr_name(idAttr)
        val varName = var_name(idAttr)
        val paramName = attr_name(idAttr)
        val javaType = java_type(idAttr)
        if (is_logical_operation) {
          buffer.method("public Key getKey()") {
            buffer.makeReturn("key")
          }
          buffer.method("public %s get%s()".format(javaType, attrName.capitalize)) {
            buffer.makeReturn(varName)
          }
          idAttr.idPolicy match {
            case SMAutoIdPolicy => {
              buffer.method("public void set%s(Key parent_key)".format(attrName.capitalize, paramName)) {
                buffer.println("this._parent_key = parent_key;")
              }
/*
              buffer.method("public void set%s(String %s)".format(attrName.capitalize, paramName)) {
                buffer.println("this.%s = %s;".format(varName, paramName))
                buffer.println("this.key = null;")
              }
              buffer.method("public void set%s(String %s, Key parent_key)".format(attrName.capitalize, paramName)) {
                if (notNull(modelEntity.jdo.table)) {
                  buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
                } else {
                  buffer.makeVar("kind", "String", "getClass().getSimpleName()")
                }
                buffer.println("this.%s = %s;".format(varName, paramName))
                buffer.println("this.key = Util.allocateKey(kind, parent_key);")
              }
*/
            }
            case SMApplicationIdPolicy => {
              buffer.method("public void set%s(String %s)".format(attrName.capitalize, paramName)) {
                buffer.println("this.%s = %s;".format(varName, paramName))
              }
              buffer.method("public void set%s(String %s, Key parent_key)".format(attrName.capitalize, paramName)) {
                buffer.println("this.%s = %s;".format(varName, paramName))
                buffer.println("this._parent_key = parent_key;")
              }
/*
              buffer.method("public void set%s(String %s)".format(attrName.capitalize, paramName)) {
                if (notNull(modelEntity.jdo.table)) {
                  buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
                } else {
                  buffer.makeVar("kind", "String", "getClass().getSimpleName()")
                }
                buffer.println("this.%s = %s;".format(varName, paramName))
                buffer.println("this.key = Util.allocateAppUuidKey(kind, %s);".format(paramName))
              }
              buffer.method("public void set%s(String %s, Key parent_key)".format(attrName.capitalize, paramName)) {
                if (notNull(modelEntity.jdo.table)) {
                  buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
                } else {
                  buffer.makeVar("kind", "String", "getClass().getSimpleName()")
                }
                buffer.println("this.%s = %s;".format(varName, paramName))
                buffer.println("this.key = Util.allocateAppUuidKey(kind, %s, parent_key);".format(paramName))
              }
*/
            }
          }
        } else if (modelEntity.appEngine.use_key) {
          buffer.method("public Key getKey()") {
            buffer.makeReturn("key")
          }
          idAttr.idPolicy match {
            case SMAutoIdPolicy => {
              buffer.method("public Long get%s()".format(attrName.capitalize)) {
                buffer.makeReturn("key.getId()")
              }
            }
            case SMApplicationIdPolicy => {
              buffer.method("public String get%s()".format(attrName.capitalize)) {
                buffer.makeReturn("key.getName()")
              }
              buffer.method("public void set%s(String %s)".format(attrName.capitalize, paramName)) {
                if (notNull(modelEntity.jdo.table)) {
                  buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
                } else {
                  buffer.makeVar("kind", "String", "getClass().getSimpleName()")
                }
                buffer.println("this.key = KeyFactory.createAppUuidKey(kind, %s);".format(paramName))
              }
              buffer.method("public void set%s(String %s, Key parent_key)".format(attrName.capitalize, paramName)) {
                if (notNull(modelEntity.jdo.table)) {
                  buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
                } else {
                  buffer.makeVar("kind", "String", "getClass().getSimpleName()")
                }
                buffer.println("this.key = KeyFactory.createAppUuidKey(kind, %s, parent_key);".format(paramName))
              }
            }
          }
        } else {
          buffer.method("public %s get%s()".format(javaType, attrName.capitalize)) {
            buffer.makeReturn(varName)
          }
          idAttr.idPolicy match {
            case SMAutoIdPolicy => {}
            case SMApplicationIdPolicy => {
              buffer.method("public void set%s(%s %s)".format(attrName.capitalize, javaType, paramName)) {
                buffer.println("this.%s = %s;".format(varName, paramName))
              }
            }
          }
        }
      }

      def non_id_methods {
        for (attr <- attributes if !attr.isId) {
          make_attribute_method(attr)
          buffer.println()
        }
      }

      id_methods
      non_id_methods
    }

    def make_attribute_method(attr: GaejAttribute) {
      val attrName = attr.name;
      val paramName = attr.name;
      val varName = {
        attr.kind match {
          case NullAttributeKind => attr.name
          case IdAttributeKind => attr.name
          case NameAttributeKind => attr.name
          case UserAttributeKind => attr.name
          case TitleAttributeKind => attr.name
          case SubTitleAttributeKind => attr.name
          case SummaryAttributeKind => attr.name
          case CategoryAttributeKind => attr.name
          case AuthorAttributeKind => "_entity_author"
          case IconAttributeKind => attr.name
          case LogoAttributeKind => attr.name
          case LinkAttributeKind => attr.name
          case ContentAttributeKind => attr.name
          case CreatedAttributeKind => "_entity_created"
          case UpdatedAttributeKind => "_entity_updated"
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
      def make_single_reference_attribute_method(entityType: GaejEntityType) {
        def make_single_fill {
          make_pm_delegate_method("fill_" + attrName)
          buffer.method("protected final void fill_" + attrName + "(PersistenceManager pm)") {
            buffer.makeIf(refVarName + " != null") {
              buffer.makeReturn
            }
            if (attr.isCache) {
              buffer.makeIf("entity_context.isAssociationCache(this, %s) && %s != null".format(cacheTimestampVarName, cacheVarName)) {
                buffer.makeTry {
                  buffer.println("%s = new %s();".format(refVarName, entityType.entity.name))
                  buffer.println("%s.init_document(%s, pm);".format(refVarName, cacheVarName))
                  buffer.makeReturn
                }
                buffer.makeCatchEnd("Exception e") {
                  buffer.makeSetVarNull(cacheTimestampVarName)
                  buffer.makeSetVarNull(cacheVarName)
                }
              }
            }
            if (is_owned_property(attr)) {
              buffer.println("%s = %s;".format(refVarName, jdoVarName))
              buffer.println("%s = true;".format(entity_ref_is_loaded_var_name(attr)))
            } else if (is_query_property(attr)) {
              buffer.append("""      // query single
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
              buffer.makeIf(jdoVarName + " != null") {
                buffer.print(refVarName)
                buffer.print(" = ")
                buffer.print(entityType.entity.name)
                buffer.print(".get_entity(")
                buffer.print(gaejContext.variableName4RefId(attr))
                buffer.println(", pm);")
              }
            }
          }
        }

        def make_single_is_update_cache {
          if (is_owned_property(attr)) {
            buffer.method("private boolean is_update_cache_%s()".format(attrName)) {
              buffer.append("""        if (%varName%_association == null) return false;
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
            buffer.method("private boolean is_update_cache_%s()".format(attrName)) {
              buffer.append("""        if (%varName%_association == null) return false;
        if (%varName%_cache == null) return true;
        Long id = %varName%_association.getId();
        Date updated = %varName%_association.entity_getUpdated();
        if (!id.equals(%varName%_cache.id)) return true;
        if (!updated.equals(%varName%_cache.entity_info.updated)) return true;
        return false;
""", Map("%varName%" -> varName))
            }
          } else {
            buffer.method("private boolean is_update_cache_%s()".format(attrName)) {
              buffer.append("""        if (%varName%_association == null) return false;
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
            buffer.method("protected final void restore_" + attrName + "()") {
              buffer.append("""        if (is_update_cache_%varName%()) {
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
            buffer.method("protected final void restore_" + attrName + "()") {
              buffer.append("""        if (is_update_cache_%varName%()) {
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
          buffer.method("public " + java_type(attr) + " get" + attrName.capitalize + "()") {
            buffer.print("fill_")
            buffer.print(attrName)
            buffer.println("();")
            buffer.print("return ")
            buffer.print(refVarName)
            buffer.println(";")
          }
        }

        def make_single_set {
          if (is_settable(attr)) {
            buffer.method("public void set" + attrName.capitalize + "(" + java_type(attr) + " " + paramName + ")") {
              if (is_owned_property(attr)) {
                buffer.println("// TODO owned (set part built from doc)")
              } else if (is_query_property(attr)) {
                buffer.println("// TODO query (set part built from doc)")
              } else {
                buffer.print("this.")
                buffer.print(refVarName)
                buffer.print(" = ")
                buffer.print(paramName)
                buffer.println(";")
                buffer.print("this.")
                buffer.print(jdoVarName)
                buffer.print(" = ")
                buffer.print(paramName)
                buffer.print(".")
                buffer.print("get")
                buffer.print(entityType.entity.idName.capitalize)
                buffer.println("();")
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

      def make_single_part_attribute_method(partType: GaejEntityPartType) {
        make_pm_delegate_method("fill_" + attrName)
        buffer.method("protected final void fill_" + attrName + "(PersistenceManager pm)") {
          buffer.makeIfElse(jdoVarName + " != null && " + partVarName + " == null") {
            buffer.print(partVarName)
            buffer.print(" = new ")
            buffer.print(java_element_type(attr))
            buffer.print("(")
            buffer.print(jdoVarName)
            buffer.println(".toString(), pm);");
          }
          buffer.makeElseIfElse(jdoVarName + " == null && " + partVarName + " == null") {
          }
          buffer.makeElse {
          }
        }
        buffer.method("protected final void restore_" + attrName + "()") {
          buffer.makeIf(partVarName + " != null") {
            buffer.print(jdoVarName)
            buffer.print(" = new Text(")
            buffer.print(partVarName)
            buffer.println(".toString());");
          }
        }
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attrName.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.print("return ")
        buffer.print(partVarName)
        buffer.println(";")
        buffer.indentDown
        buffer.println("}")
        if (attr.attributeType == GaejBooleanType) {
          buffer.method("public boolean is" + attrName.capitalize + "()") {
            buffer.makeReturn(varName)
          }
        }
        if (is_settable(attr)) {
          buffer.println()
          buffer.print("public void set")
          buffer.print(attrName.capitalize)
          buffer.print("(")
          buffer.print(java_type(attr))
          buffer.print(" ")
          buffer.print(paramName)
          buffer.println(") {")
          buffer.indentUp
          buffer.print("this.")
          buffer.print(partVarName)
          buffer.print(" = ")
          buffer.print(paramName)
          buffer.println(";")
          buffer.indentDown
          buffer.println("}")
        }
      }

      def make_single_powertype_attribute_method(powertypeType: GaejPowertypeType) {
        make_pm_delegate_method("fill_" + attrName)
        buffer.method("protected final void fill_" + attrName + "(PersistenceManager pm)") {
          buffer.makeIf(powerVarName + " != null") {
            buffer.makeReturn
          }
          buffer.makeIf("entity_context.isAssociationCache(this, %s) && %s != null".format(cacheTimestampVarName, entity_ref_label_var_name(attr))) {
            buffer.print(powerVarName)
            buffer.print(" = ")
            buffer.print(java_element_type(attr))
            buffer.print(".get(")
            buffer.print(jdoVarName)
            buffer.print(", ");
            buffer.print(entity_ref_label_var_name(attr))
            buffer.print(", ");
            buffer.print(entity_ref_updated_var_name(attr))
            buffer.println(");");
            buffer.makeReturn
          }
          buffer.makeIf("%s != null".format(jdoVarName)) {
            buffer.println("%s = %s.get(%s, pm);".format(powerVarName, java_element_type(attr), jdoVarName))
          }
        }
        buffer.method("protected final void restore_" + attrName + "()") {
          buffer.makeIf(powerVarName + " != null") {
            buffer.print(jdoVarName)
            buffer.print(" = ")
            buffer.print(powerVarName)
            buffer.println(".getKey();");
            buffer.print(entity_ref_label_var_name(attr))
            buffer.print(" = ")
            buffer.print(powerVarName)
            buffer.println(".getLabel();");
            buffer.print(entity_ref_updated_var_name(attr))
            buffer.print(" = ")
            buffer.print(powerVarName)
            buffer.println(".getUpdated();");
          }
        }
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attrName.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.print("return ")
        buffer.print(powerVarName)
        buffer.println(";")
        buffer.indentDown
        buffer.println("}")
        if (attr.attributeType == GaejBooleanType) {
          buffer.method("public boolean is" + attrName.capitalize + "()") {
            buffer.makeReturn(varName)
          }
        }
        if (is_settable(attr)) {
          buffer.println()
          buffer.print("public void set")
          buffer.print(attrName.capitalize)
          buffer.print("(")
          buffer.print(java_type(attr))
          buffer.print(" ")
          buffer.print(paramName)
          buffer.println(") {")
          buffer.indentUp
          buffer.print("this.")
          buffer.print(powerVarName)
          buffer.print(" = ")
          buffer.print(paramName)
          buffer.println(";")
          buffer.indentDown
          buffer.println("}")
        }
      }

      def make_mapping_single_value_attribute_method(getter: String, setter: String) {
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attrName.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.makeIfElse(attrName + " == null") {
          buffer.makeReturn("null")
        }
        buffer.makeElse {
          buffer.makeReturn(getter.format(attrName))
        }
        buffer.indentDown
        buffer.println("}")
        if (attr.attributeType == GaejBooleanType) {
          buffer.method("public boolean is" + attrName.capitalize + "()") {
            buffer.makeReturn(varName)
          }
        }
        if (is_settable(attr)) {
          buffer.println()
          buffer.print("public void set")
          buffer.print(attrName.capitalize)
          buffer.print("(")
          buffer.print(java_type(attr))
          buffer.print(" ")
          buffer.print(paramName)
          buffer.println(") {")
          buffer.indentUp
          buffer.makeIfElse(paramName + " == null") {
            buffer.print("this.")
            buffer.print(attrName)
            buffer.print(" = null;")
          }
          buffer.makeElse {
            buffer.print("this.")
            buffer.print(attrName)
            buffer.print(" = ")
            buffer.print(setter.format(attrName))
            buffer.println(";")
          }
          buffer.indentDown
          buffer.println("}")
        }
      }

      def make_single_value_attribute_method {
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attrName.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.print("return ")
        buffer.print(varName)
        buffer.println(";")
        buffer.indentDown
        buffer.println("}")
        if (attr.attributeType == GaejBooleanType) {
          buffer.method("public boolean is" + attrName.capitalize + "()") {
            buffer.makeReturn(varName)
          }
        }
        if (is_settable(attr)) {
          buffer.println()
          buffer.print("public void set")
          buffer.print(attrName.capitalize)
          buffer.print("(")
          buffer.print(java_type(attr))
          buffer.print(" ")
          buffer.print(paramName)
          buffer.println(") {")
          buffer.indentUp
          buffer.print("this.")
          buffer.print(varName)
          buffer.print(" = ")
          buffer.print(paramName)
          buffer.println(";")
          buffer.indentDown
          buffer.println("}")
        }
      }

      // Multi
      def make_multi_reference_attribute_method(entityType: GaejEntityType) {
        def make_multi_try_reuse_cache {
          buffer.makeIf(refVarName + " != null") {
            buffer.makeReturn
          }
          buffer.makeIf("entity_context.isAssociationCache(this, %s) && %s != null".format(cacheTimestampVarName, cacheVarName)) {
            buffer.makeTry {
              buffer.println("%s = new ArrayList<%s>();".format(refVarName, entityType.entity.name))
              buffer.makeFor(entityType.entity.documentName, "doc", cacheVarName) {
                buffer.println("%s entity = new %s();".format(entityType.entity.name, entityType.entity.name))
                buffer.println("entity.init_document(doc, pm);")
                buffer.println("%s.add(entity);".format(refVarName))
              }
              buffer.makeReturn
            }
            buffer.makeCatchEnd("Exception e") {
              buffer.makeSetVarNull(refVarName)
              buffer.makeSetVarNull(cacheTimestampVarName)
              buffer.makeSetVarNull(cacheVarName)
            }
          }
        }

        def make_multi_fill {
          make_pm_delegate_method("fill_" + attrName)
          buffer.method("protected final void fill_" + attrName + "(PersistenceManager pm)") {
            make_multi_try_reuse_cache
            if (is_owned_property(attr)) {
              buffer.println("// owned")
              buffer.println("%s = new ArrayList<%s>(%s);".format(refVarName, jdo_element_type(attr), jdoVarName))
              buffer.println("%s = true;".format(entity_ref_is_loaded_var_name(attr)))
            } else if (is_query_property(attr)) {
              buffer.println("// query")
              buffer.append("""%entityName%Query query = query%capitalizePropertyName%(pm);
%entityName%Chunks chunks = query.execute();
%refVarName% = new ArrayList<%entityName%>(chunks.next());
""", Map("%entityName%" -> attr.elementTypeName,
         "%capitalizePropertyName%" -> attrName.capitalize,
         "%refVarName%" -> refVarName))
            } else {
              buffer.makeIf(jdoVarName + " != null") {
                buffer.print(refVarName)
                buffer.print(" = ")
                buffer.print(entityType.entity.name)
                buffer.print(".get_entities(")
                buffer.print(gaejContext.variableName4RefId(attr))
                buffer.println(", pm);")
              }
            }
          }
        }

        def make_multi_is_update_cache {
          if (is_owned_property(attr)) {
            buffer.method("private boolean is_update_cache_%s()".format(attrName)) {
            buffer.append("""        if (%varName%_association == null) return false;
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
            buffer.method("private boolean is_update_cache_%s()".format(attrName)) {
            buffer.append("""        if (%varName%_association == null) return false;
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
            buffer.method("private boolean is_update_cache_%s()".format(attrName)) {
            buffer.append("""        if (%varName%_association == null) return false;
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
            buffer.method("protected final void restore_" + attrName + "()") {
              buffer.append("""        // multi owned or query
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
            buffer.method("protected final void restore_" + attrName + "()") {
              buffer.append("""        // multi plain
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
          buffer.method("public %s get%s()".format(java_type(attr), attrName.capitalize)) {
            buffer.println("fill_%s();".format(attrName))
            buffer.makeIfElse(refVarName + " != null") {
              buffer.print("return Collections.unmodifiableList(")
              buffer.print(refVarName)
              buffer.println(");")
            }
            buffer.makeElse {
              buffer.println("return Collections.emptyList();")
            }
          }
        }

        def make_multi_query {
          if (is_query_property(attr)) {
            buffer.method("public %sQuery query%s(PersistenceManager pm)".format(attr.elementTypeName, attrName.capitalize)) {
              buffer.makeReturn("new %sQuery(pm)".format(attr.elementTypeName))
            }
            if (is_logical_operation(entityType)) {
              buffer.append("""
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
              buffer.append("""
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
            buffer.method("public void set%s(%s %s)".format(attrName.capitalize, java_type(attr), paramName)) {
              buffer.print("this.")
              buffer.print(refVarName)
              buffer.print(" = new ArrayList<")
              buffer.print(attr.elementTypeName)
              buffer.print(">(")
              buffer.print(paramName)
              buffer.println(");")
              if (is_owned_property(attr)) {
                  buffer.println("// TODO crates owned multi entites.")
              } else if (is_query_property(attr)) {
                  buffer.println("// TODO crates query multi entites.")
              } else {
                buffer.makeFor(jdo_element_type(attr) + " element: this." + refVarName) {
                  buffer.print("this.")
                  buffer.print(jdoVarName)
                  buffer.print(".add(element.get")
                  buffer.print(entityType.entity.idName.capitalize)
                  buffer.println("());")
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

      def make_multi_part_attribute_method(partType: GaejEntityPartType) {
        make_pm_delegate_method("fill_" + attrName)
        buffer.method("protected final void fill_" + attrName + "(PersistenceManager pm)") {
          buffer.makeIfElse(jdoVarName + " != null && " + partVarName + " == null") {
            buffer.print("List<org.w3c.dom.Element> elements = Util.getElements(")
            buffer.print(partVarName)
            buffer.print(".toString());")
            buffer.makeFor("org.w3c.dom.Element element: elements") {
              buffer.print(partVarName)
              buffer.print(".add(new ")
              buffer.print(java_element_type(attr))
              buffer.println("(element, pm));");
            }
          }
          buffer.makeElseIfElse(jdoVarName + " == null && " + partVarName + " == null") {
          }
          buffer.makeElse {
          }
        }
        buffer.method("protected final void restore_" + attrName + "()") {
          buffer.makeIf(partVarName + " != null") {
            buffer.makeStringBuilderVar();
            buffer.makeAppendString("<list>")
            buffer.makeFor(java_element_type(attr) + " element: " + partVarName) {
              buffer.println("element.entity_asXmlString(buf);")
            }
            buffer.makeAppendString("</list>")
            buffer.print(jdoVarName)
            buffer.println(" = new Text(buf.toString());");
          }
        }
        buffer.method("public " + java_type(attr) + " get" + attrName.capitalize + " ()") {
          buffer.print("fill_")
          buffer.print(attrName)
          buffer.println("();")
          buffer.makeIfElse(partVarName + " != null") {
            buffer.print("return Collections.unmodifiableList(")
            buffer.print(partVarName)
            buffer.println(");")
          }
          buffer.makeElse {
            buffer.println("return Collections.emptyList();")
          }
        }
        if (is_settable(attr)) {
          buffer.method("public void set" + attrName.capitalize + "(" + java_type(attr) + " " + paramName + ")") {
            buffer.print("this.")
            buffer.print(partVarName)
            buffer.print(" = new ArrayList<")
            buffer.print(java_element_type(attr))
            buffer.print(">(")
            buffer.print(paramName)
            buffer.println(");")
          }
          buffer.method("public void add"  + attrName.capitalize + "(" + java_element_type(attr) + " " + paramName + ")") {
            buffer.makeIf("this." + partVarName + " == null") {
              buffer.print("this.")
              buffer.print(partVarName)
              buffer.print(" = new ArrayList<")
              buffer.print(java_element_type(attr))
              buffer.println(">();")
            }
            buffer.print("this.")
            buffer.print(partVarName)
            buffer.print(".add(")
            buffer.print(paramName)
            buffer.println(");")
          }
        }
      }

      def make_multi_powertype_attribute_method(powertypeType: GaejPowertypeType) { // XXX
        make_pm_delegate_method("fill_" + attrName)
        buffer.method("protected final void fill_" + attrName + "(PersistenceManager pm)") {
          buffer.makeIfElse(jdoVarName + " != null && " + partVarName + " == null") {
            buffer.print("List<org.w3c.dom.Element> elements = Util.getElements(")
            buffer.print(partVarName)
            buffer.print(".toString());")
            buffer.makeFor("org.w3c.dom.Element element: elements") {
              buffer.print(partVarName)
              buffer.print(".add(new ")
              buffer.print(java_element_type(attr))
              buffer.println("(element, pm));");
            }
          }
          buffer.makeElseIfElse(jdoVarName + " == null && " + partVarName + " == null") {
          }
          buffer.makeElse {
          }
        }
        buffer.method("protected final void restore_" + attrName + "()") {
          buffer.makeIf(partVarName + " != null") {
            buffer.makeStringBuilderVar();
            buffer.makeAppendString("<list>")
            buffer.makeFor(java_element_type(attr) + " element: " + partVarName) {
              buffer.println("element.entity_asXmlString(buf);")
            }
            buffer.makeAppendString("</list>")
            buffer.print(jdoVarName)
            buffer.println(" = new Text(buf.toString());");
          }
        }
        buffer.method("public " + java_type(attr) + " get" + attrName.capitalize + " ()") {
          buffer.print("fill_")
          buffer.print(attrName)
          buffer.println("();")
          buffer.makeIfElse(partVarName + " != null") {
            buffer.print("return Collections.unmodifiableList(")
            buffer.print(partVarName)
            buffer.println(");")
          }
          buffer.makeElse {
            buffer.println("return Collections.emptyList();")
          }
        }
        if (is_settable(attr)) {
          buffer.method("public void set" + attrName.capitalize + "(" + java_type(attr) + " " + paramName + ")") {
            buffer.print("this.")
            buffer.print(partVarName)
            buffer.print(" = new ArrayList<")
            buffer.print(java_element_type(attr))
            buffer.print(">(")
            buffer.print(paramName)
            buffer.println(");")
          }
          buffer.method("public void add"  + attrName.capitalize + "(" + java_element_type(attr) + " " + paramName + ")") {
            buffer.makeIf("this." + partVarName + " == null") {
              buffer.print("this.")
              buffer.print(partVarName)
              buffer.print(" = new ArrayList<")
              buffer.print(java_element_type(attr))
              buffer.println(">();")
            }
            buffer.print("this.")
            buffer.print(partVarName)
            buffer.print(".add(")
            buffer.print(paramName)
            buffer.println(");")
          }
        }
      }

      def make_mapping_multi_value_attribute_method(getter: String, setter: String) {
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attrName.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.makeIfElse(attrName + " != null") {
          buffer.makeVar("result", "List<" + attr.elementTypeName + ">", "new ArrayList<" + attr.elementTypeName + ">()")
          buffer.makeFor(jdo_element_type(attr), "elem", attrName) {
            buffer.print("result.add(")
            buffer.print(getter.format("elem"))
            buffer.println(");")
          }
          buffer.makeReturn("result")
        }
        buffer.makeElse {
          buffer.println("return Collections.emptyList();")
        }
        buffer.indentDown
        buffer.println("}")
        buffer.println()
        if (is_settable(attr)) {
          buffer.method("public void set" + attrName.capitalize + "(" + java_type(attr) + " " + paramName + ")") {
            buffer.print("this.")
            buffer.print(attrName)
            buffer.print(" = new ArrayList<")
            buffer.print(jdo_element_type(attr))
            buffer.print(">(")
            buffer.println(");")
            buffer.makeFor(attr.elementTypeName, "elem", paramName) {
              buffer.print("this.")
              buffer.print(attrName)
              buffer.print(".add(")
              buffer.print(setter.format("elem"))
              buffer.println(");")
            }
          }
          buffer.method("public void add"  + attrName.capitalize + "(" + jdo_element_type(attr) + " " + paramName + ")") {
            buffer.makeIf("this." + varName + " == null") {
              buffer.print("this.")
              buffer.print(varName)
              buffer.print(" = new ArrayList<")
              buffer.print(jdo_element_type(attr))
              buffer.println(">();")
            }
            buffer.print("this.")
            buffer.print(attrName)
            buffer.print(".add(")
            buffer.print(setter.format(paramName))
            buffer.println(");")
          }
        }
      }

      def make_multi_value_attribute_method {
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" get")
        buffer.print(attrName.capitalize)
        buffer.println("() {")
        buffer.indentUp
        buffer.makeIfElse(varName + " != null") {
          buffer.print("return Collections.unmodifiableList(")
          buffer.print(varName)
          buffer.println(");")
        }
        buffer.makeElse {
          buffer.println("return Collections.emptyList();")
        }
        buffer.indentDown
        buffer.println("}")
        buffer.println()
        if (is_settable(attr)) {
          buffer.method("public void set" + attrName.capitalize + "(" + java_type(attr) + " " + paramName + ")") {
            buffer.print("this.")
            buffer.print(varName)
            buffer.print(" = new ArrayList<")
            buffer.print(jdo_element_type(attr))
            buffer.print(">(")
            buffer.print(paramName)
            buffer.println(");")
          }
          buffer.method("public void add"  + attrName.capitalize + "(" + jdo_element_type(attr) + " " + paramName + ")") {
            buffer.makeIf("this." + varName + " == null") {
              buffer.print("this.")
              buffer.print(varName)
              buffer.print(" = new ArrayList<")
              buffer.print(jdo_element_type(attr))
              buffer.println(">();")
            }
            buffer.print("this.")
            buffer.print(varName)
            buffer.print(".add(")
            buffer.print(paramName)
            buffer.println(");")
          }
        }
      }

      if (attr.isHasMany) {
        attr.attributeType match {
          case e: GaejEntityType => make_multi_reference_attribute_method(e)
          case p: GaejEntityPartType => make_multi_part_attribute_method(p)
          case p: GaejPowertypeType => make_multi_powertype_attribute_method(p)
          case v: GaejByteType => make_mapping_multi_value_attribute_method("%s.byteValue()", "%s.shortValue()")
          case v: GaejIntegerType => make_mapping_multi_value_attribute_method("new BigInteger(%s)", "%s.toString()")
          case v: GaejDecimalType => make_mapping_multi_value_attribute_method("new BigDecimal(%s)", "%s.toString()")
          case _ => make_multi_value_attribute_method
        }
      } else {
        attr.attributeType match {
          case e: GaejEntityType => make_single_reference_attribute_method(e)
          case p: GaejEntityPartType => make_single_part_attribute_method(p)
          case p: GaejPowertypeType => make_single_powertype_attribute_method(p)
          case v: GaejByteType => make_mapping_single_value_attribute_method("%s.byteValue()", "%s.shortValue()")
          case v: GaejIntegerType => make_mapping_single_value_attribute_method("new BigInteger(%s)", "%s.toString()")
          case v: GaejDecimalType => make_mapping_single_value_attribute_method("new BigDecimal(%s)", "%s.toString()")
          case _ => make_single_value_attribute_method
        }
      }
    }

    def make_null_constructor {
      buffer.print("public ")
      buffer.print(name)
      buffer.println("() {")
      buffer.println("}")
    }

    def make_init_method {
      buffer.println()
      buffer.print("public void init_document(")
      buffer.print(documentName)
      buffer.println(" doc, PersistenceManager pm) {")
      buffer.indentUp
      for (attr <- attributes) {
        make_update_attribute(attr)
      }
      buffer.println("entity_fill(pm);");
      buffer.indentDown
      buffer.println("}")
    }

    def make_update_method {
      buffer.println()
      buffer.print("public void update_document(")
      buffer.print(documentName)
      buffer.println(" doc, PersistenceManager pm) {")
      buffer.indentUp
      buffer.print("if (doc.")
      buffer.print(idName)
      buffer.println(" != null) {")
      buffer.indentUp
      buffer.print("if (!doc.")
      buffer.print(idName)
      buffer.print(".equals(get")
      buffer.print(idName.capitalize)
      buffer.println("())) {")
      buffer.indentUp
      buffer.println("throw new IllegalArgumentException(\"XXX\");")
      buffer.indentDown
      buffer.println("}")
      buffer.indentDown
      buffer.println("}")
      for (attr <- attributes if !attr.isId) {
        make_update_attribute(attr)
      }
      buffer.indentDown
      buffer.println("}")
    }

    def make_update_attribute(attr: GaejAttribute) {
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
          buffer.makeIfElse(fromName + " == null") {
            buffer.print(toName)
            buffer.println(" = null;")
          }
          buffer.makeElse{
            buffer.print(toName)
            buffer.print(" = ")
            buffer.print(converter.format(fromName))
            buffer.println(";")
          }
        }

        attr.attributeType match { // sync GaejDomainServiceEntity
          case t: GaejDateTimeType => {
            buffer.print("if (doc.")
            buffer.print(varName)
            buffer.print(" != null || doc.")
            buffer.print(varName)
            buffer.print("_date != null || doc.")
            buffer.print(varName)
            buffer.print("_time != null || doc.")
            buffer.print(varName)
            buffer.print("_now")
            buffer.println(") {")
            buffer.indentUp
            buffer.print("this.")
            buffer.print(varName)
            buffer.print(" = ")
            buffer.print("Util.makeDateTime(")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print(", ")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print("_date, ")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print("_time, ")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print("_now")
            buffer.print(");")
            buffer.println()
            buffer.indentDown
            buffer.println("}")
          }
          case e: GaejEntityType => {
            buffer.print("if (doc.")
            buffer.print(gaejContext.variableName4RefId(attr))
            buffer.println(" != null) {")
            buffer.indentUp
            buffer.print("this.") // make_reference_key_unencoded_string
            buffer.print(jdoVarName)
            buffer.print(" = ")
            buffer.print("doc.")
            buffer.print(gaejContext.variableName4RefId(attr))
            buffer.println(";")
/* XXX
            buffer.print("this.")
            buffer.print(refVarName)
            buffer.print(" = ")
            buffer.print(e.entity.name)
            buffer.print(".get_entity(doc.")
            buffer.print(gaejContext.variableName4RefId(attr))
            buffer.print(", pm);")
*/
            buffer.println()
            buffer.indentDown
            buffer.println("}")
          }
          case p: GaejEntityPartType => {
/*
            buffer.makeIf("doc." + varName + " != null") {
              buffer.print("this.")
              buffer.print(partVarName)
              buffer.print(" = new ")
              buffer.print(java_type(attr))
              buffer.print("(doc.")
              buffer.print(varName)
              buffer.println(", pm);")
            }
*/
          }
          case p: GaejPowertypeType => {
/*
            val powertypeName = p.powertype.name
            buffer.makeIf("doc." + varName + " != null") {
              buffer.print("this.")
              buffer.print(powerVarName)
              buffer.print(" = ")
              buffer.print(powertypeName)
              buffer.print(".get(doc.")
              buffer.print(varName)
              buffer.println(", pm);")
            }
*/
          }
          case v: GaejByteType => update("%s.shortValue()")
          case v: GaejIntegerType => update("%s.toString()")
          case v: GaejDecimalType => update("%s.toString()")
          case _ => {
            buffer.print("this.")
            buffer.print(varName)
            buffer.print(" = ")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print(";")
            buffer.println()
/*
            if (attr.isDataType) {
              buffer.print("this.")
              buffer.print(varName)
              buffer.print(" = ")
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print(";")
              buffer.println()
            } else {
              buffer.print("if (doc.")
              buffer.print(varName)
              buffer.println(" != null) {")
              buffer.indentUp
              buffer.print("this.")
              buffer.print(varName)
              buffer.print(" = ")
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print(";")
              buffer.println()
              buffer.indentDown
              buffer.println("}")
            }
*/
          }
        }
      }

      def make_update_attribute_many {
        def update(converter: String) {
          buffer.print(toName)
          buffer.print(" = new ArrayList<")
          buffer.print(jdo_element_type(attr))
          buffer.print(">(")
          buffer.print(");")
          buffer.println()
          buffer.makeFor(attr.elementTypeName, "elem", fromName) {
            buffer.print(toName)
            buffer.print(".add(")
            buffer.print(converter.format("elem"))
            buffer.println(");")
          }
        }

        attr.attributeType match { // sync GaejDomainServiceEntity
          case t: GaejDateTimeType => { // XXX
            buffer.print("if (doc.")
            buffer.print(varName)
            buffer.print(" != null || doc.")
            buffer.print(varName)
            buffer.print("_date != null || doc.")
            buffer.print(varName)
            buffer.print("_time != null || doc.")
            buffer.print(varName)
            buffer.print("_now")
            buffer.println(") {")
            buffer.indentUp
            buffer.print("this.")
            buffer.print(varName)
            buffer.print(" = ")
            buffer.print("Util.makeDateTime(")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print(", ")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print("_date, ")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print("_time, ")
            buffer.print("doc.")
            buffer.print(varName)
            buffer.print("_now")
            buffer.print(");")
            buffer.println()
            buffer.indentDown
            buffer.println("}")
          }
          case e: GaejEntityType => {
            // make_reference_key_unencoded_string
            if (is_owned_property(attr)) {
              buffer.println("// TODO owned")
            } else if (is_query_property(attr)) {
              buffer.println("// TODO query")
            } else {
              buffer.println("// plain")
              val idVarName = "this." + jdoVarName
              buffer.print(idVarName)
              buffer.print(" = new ArrayList<")
              buffer.print(e.entity.idAttr.elementTypeName)
              buffer.print(">(")
              buffer.print("doc.")
              buffer.print(gaejContext.variableName4RefId(attr))
              buffer.println(");")
            }
          }
          case p: GaejEntityPartType => {
            buffer.makeIf("doc." + varName + " != null") {
              buffer.print("this.")
              buffer.print(partVarName)
              buffer.print(" = new ArrayList<")
              buffer.print(java_element_type(attr))
              buffer.println(">();")
//              buffer.print("this.")
//              buffer.print(attrName)
//              buffer.println(" = new ArrayList<String>();")
              buffer.makeFor(java_doc_element_type(attr) + " element: doc." + attrName) {
                buffer.print("this.")
                buffer.print(partVarName)
                buffer.print(".add(new ")
                buffer.print(java_element_type(attr))
                buffer.println("(element, pm));")
//                buffer.print("this.")
//                buffer.print(attrName)
//                buffer.println(".add(element.entity_xmlString());")
              }
            }
          }
          case p: GaejPowertypeType => error("not supported yet")
          case v: GaejByteType => update("%s.shortValue()")
          case v: GaejIntegerType => update("%s.toString()")
          case v: GaejDecimalType => update("%s.toString()")
          case _ => {
            buffer.print("this.")
            buffer.print(varName)
            buffer.print(" = new ArrayList<")
            buffer.print(jdo_element_type(attr))
            buffer.print(">(doc.")
            buffer.print(varName)
            buffer.print(");")
            buffer.println()
          }
        }
      }

      def make_update_owned_property_many {
        buffer.println("// TODO owned many")
      }

      def make_update_owned_property_one {
        buffer.println("// TODO owned one")
      }

      def make_update_query_property_many {
        buffer.println("// TODO owned many")
      }

      def make_update_query_property_one {
        buffer.println("// TODO owned one")
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

    def make_document_method {
      def make_attribute_plain(attr: GaejAttribute) {
        make_attribute_common(attr, true)
      }

      def make_attribute_shallow(attr: GaejAttribute) {
        make_attribute_common(attr, false)
      }

      def make_attribute_common(attr: GaejAttribute, isDeepCopy: Boolean) {
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
            buffer.makeIfElse(fromName + " == null") {
              buffer.print(toName)
              buffer.println(" = null;")
            }
            buffer.makeElse{
              buffer.print(toName)
              buffer.print(" = ")
              buffer.print(converter.format(fromName))
              buffer.println(";")
            }
          }

          attr.attributeType match {
            case t: GaejDateTimeType => {
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print(" = this.")
              buffer.print(varName)
              buffer.println(";")
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print("_date = Util.makeDate(this.")
              buffer.print(varName)
              buffer.print(");")
              buffer.println()
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print("_time = Util.makeTime(this.")
              buffer.print(varName)
              buffer.print(");")
              buffer.println()
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print("_now = false;")
              buffer.println()
            }
            case e: GaejEntityType => {
              def make_id {
                buffer.print("doc.")
                buffer.print(gaejContext.variableName4RefId(attr))
                buffer.print(" = this.")
                buffer.print(jdoVarName)
                buffer.println(";")
              }

              def make_get_id {
                buffer.print("doc.")
                buffer.print(gaejContext.variableName4RefId(attr))
                buffer.print(" = this.")
                buffer.print(refVarName)
                buffer.println(".getId();")
              }

              def make_deep {
                buffer.println("fill_%s();".format(attrName))
                buffer.makeIf("%s != null".format(refVarName)) {
                  buffer.println("doc.%s = %s.make_document();".format(varName, refVarName))
                }
              }

              def make_shallow {
                buffer.println("fill_%s();".format(attrName))
                buffer.makeIf("%s != null".format(refVarName)) {
                  buffer.println("doc.%s = %s.make_document_shallow();".format(varName, refVarName))
                }
              }

              if (is_owned_property(attr)) {
                buffer.println("fill_%s();".format(attrName))
                buffer.makeIf("%s != null".format(refVarName)) {
                  make_get_id
                  buffer.println("doc.%s = %s.make_document();".format(varName, refVarName))
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
                  buffer.println("fill_%s();".format(attrName))
                  buffer.makeIf("%s != null".format(refVarName)) {
                    make_get_id
                    buffer.println("doc.%s = %s.make_document();".format(varName, refVarName))
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
            case p: GaejEntityPartType => {
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print(" = this.")
              buffer.print(refVarName)
              buffer.println(".make_document();")
            }
            case p: GaejPowertypeType => {
              buffer.makeIf("this." + varName + " != null") {
                buffer.print("doc.")
                buffer.print(varName)
                buffer.print(" = this.")
                buffer.print(powerVarName)
                buffer.println(".getKey();")
              }
            }
            case v: GaejByteType => update("%s.byteValue()")
            case v: GaejIntegerType => update("new BigInteger(%s)")
            case v: GaejDecimalType => update("new BigDecimal(%s)")
            case _ => {
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print(" = this.")
              buffer.print(varName)
              buffer.println(";")
            }
          }
        }        

        def make_many {
          def update(converter: String) {
            buffer.makeIf(fromName + " != null") {
              buffer.makeFor(jdo_element_type(attr), "elem", fromName) {
                buffer.print(toName)
                buffer.print(".add(")
                buffer.print(converter.format("elem"))
                buffer.println(");")
              }
            }
          }

          attr.attributeType match {
            case t: GaejDateTimeType => { // XXX
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print(" = ")
              buffer.print(varName)
              buffer.println(";")
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print("_date = Util.makeDate(")
              buffer.print(varName)
              buffer.print(");")
              buffer.println()
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print("_time = Util.makeTime(")
              buffer.print(varName)
              buffer.print(");")
              buffer.println()
              buffer.print("doc.")
              buffer.print(varName)
              buffer.print("_now = false;")
              buffer.println()
            }
            case e: GaejEntityType => {
              def make_id {
                buffer.makeIf("this." + jdoVarName + " != null") {
                  buffer.print("doc.")
                  buffer.print(gaejContext.variableName4RefId(attr))
                  buffer.print(".addAll(this.")
                  buffer.print(jdoVarName)
                  buffer.println(");")
                }
              }

              def make_get_id {
                buffer.makeIf("this." + jdoVarName + " != null") {
                  buffer.print("doc.")
                  buffer.print(gaejContext.variableName4RefId(attr))
                  buffer.print(".addAll(this.")
                  buffer.print(jdoVarName)
                  buffer.println(");")
                }
              }

              def make_deep {
                buffer.println("fill_%s();".format(attrName))
                buffer.makeFor("%s entity: %s".format(e.entity.name, refVarName)) {
                  buffer.println("doc.%s.add(entity.make_document());".format(varName))
                }
              }

              def make_shallow {
                buffer.println("fill_%s();".format(attrName))
                buffer.makeFor("%s entity: %s".format(e.entity.name, refVarName)) {
                  buffer.println("doc.%s.add(entity.make_document_shallow());".format(varName))
                }
              }

              if (is_owned_property(attr)) {
                buffer.makeFor("%s entity: get%s()".format(e.entity.name, attrName.capitalize)) {
                  buffer.println("doc.%s.add(entity.make_document());".format(varName))
                  buffer.println("doc.%s.add(entity.getId());".format(gaejContext.variableName4RefId(attr)))
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
            case p: GaejEntityPartType => {
              buffer.print("fill_")
              buffer.print(attrName)
              buffer.println("();")
              buffer.makeIf("this." + partVarName + " != null") {
                buffer.makeFor(java_element_type(attr) + " element: " + partVarName) {
                  buffer.print("doc.")
                  buffer.print(attrName)
                  buffer.println(".add(element.make_document());")
                }
              }
            }
            case v: GaejByteType => update("%s.byteValue()")
            case v: GaejIntegerType => update("new BigInteger(%s)")
            case v: GaejDecimalType => update("new BigDecimal(%s)")
            case _ => {
              buffer.makeIf("this." + varName + " != null") {
                buffer.print("doc.")
                buffer.print(varName)
                buffer.print(".addAll(this.")
                buffer.print(varName)
                buffer.println(");")
              }
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

      def make_document_common(methodName: String, makeAttr: GaejAttribute => Unit) {
        buffer.method("public %s %s()".format(documentName, methodName)) {
          buffer.print(documentName)
          buffer.print(" doc = new ")
          buffer.print(documentName)
          buffer.println("();")
          buffer.print("doc.")
          buffer.print(idName)
          buffer.print(" = get")
          buffer.print(idName.capitalize)
          buffer.println("();")
          for (attr <- attributes if !attr.isId) {
            makeAttr(attr)
          }
          buffer.println("doc.entity_info = entity_getEntityInfo();")
          buffer.println("return doc;")
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
    }

    def make_make_persistent_method {
      buffer.println()
      buffer.println("public void make_persistent(PersistenceManager pm) {")
      buffer.indentUp
      if (is_logical_operation) {
        val varName = var_name(idAttr)
        if (notNull(modelEntity.jdo.table)) {
          buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
        } else {
          buffer.makeVar("kind", "String", "getClass().getSimpleName()")
        }
        buffer.makeVarNull("parent_key", "Key")
        buffer.makeIfElse("_parent_key != null") {
          buffer.println("parent_key = _parent_key;")
        }
        buffer.makeElseIf("key != null") {
          buffer.println("parent_key = key.getParent();")
        }
        idAttr.idPolicy match {
          case SMAutoIdPolicy => {
            buffer.makeIfElse("parent_key != null") {
              buffer.println("this.key = Util.allocateKey(kind, parent_key);")
            }
            buffer.makeElse {
              buffer.println("this.key = Util.allocateKey(kind);")
            }
            buffer.makeIf("%s == null".format(varName)) {
              buffer.println("%s = this.key.getId();".format(varName))
            }
          }
          case SMApplicationIdPolicy => {
            buffer.makeIfElse("parent_key != null") {
              buffer.println("this.key = Util.allocateAppUuidKey(kind, %s, parent_key);".format(varName))
            }
            buffer.makeElse {
              buffer.println("this.key = Util.allocateAppUuidKey(kind, %s);".format(varName))
            }
          }
        }
        buffer.println("entity_sync();")
      }
      buffer.println("int retryCount = 10; // XXX")
      buffer.println("int waitMillSec = 500; // XXX")
      buffer.makeWhile("retryCount-- > 0") {
        buffer.makeTry {
          buffer.println("pm.makePersistent(this);")
          buffer.makeReturn
        }
        buffer.makeCatchEnd("Exception e") {
          buffer.makeTry {
            buffer.println("Thread.sleep(waitMillSec);")
          }
          buffer.makeCatchNop("InterruptedException e2")
        }
      }
      buffer.println("pm.makePersistent(this);")
//      buffer.println("MEEntityModelInfo.updateEntityModel(pm, entity_model.entity.name, entity_model.entity.version, entity_model.entity.build, entity_model.name, entity_model.version, entity_model.build);");
      buffer.indentDown
      buffer.println("}")
    }

    def make_delete_persistent_method {
      buffer.println()
      buffer.println("public void delete_persistent(PersistenceManager pm) {")
      buffer.indentUp
      if (is_logical_operation) {
        buffer.println("_entity_removed = _entity_updated = new Date();")
        buffer.println("pm.makePersistent(this);")
      } else {
        buffer.println("pm.deletePersistent(this);")
      }
      buffer.indentDown
      buffer.println("}")
    }

    def make_get_entity_method {
      buffer.println()
      buffer.print("public static ")
      buffer.print(name)
      buffer.print(" get_entity(")
      buffer.print(idAttr.typeName)
      buffer.println(" id, PersistenceManager pm) {")
      buffer.indentUp
      if (is_logical_operation) {
        buffer.append("""Query query = pm.newQuery(%entityName%.class);
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
          buffer.makeVar("kind", "String", "\"" + modelEntity.jdo.table + "\"")
        } else {
          buffer.makeVar("kind", "String", "%s.class.getSimpleName()".format(name))
        }
        // XXX parent
        buffer.println("Key key = KeyFactory.createKey(kind, id);")
        buffer.makeVar("entity", name, "pm.getObjectById(" + name + ".class, key)")
      } else {
        buffer.makeVar("entity", name, "pm.getObjectById(" + name + ".class, id)")
      }
      buffer.println("entity.entity_fill(pm);");
      buffer.println("return entity;")
      buffer.indentDown
      buffer.println("}")
    }

    def make_get_entities_method {
      buffer.println()
      buffer.print("public static List<")
      buffer.print(name)
      buffer.print("> get_entities(List<")
      buffer.print(idAttr.typeName)
      buffer.println("> keys, PersistenceManager pm) {")
      buffer.indentUp
      buffer.makeVar("entities", "List<%s>".format(name), "new ArrayList<%s>()".format(name))
      buffer.makeFor(idAttr.typeName, "key", "keys") {
        buffer.makeVar("entity", name, "%s.get_entity(key, pm)".format(name))
        buffer.makeIf("entity != null") {
          buffer.println("entities.add(entity);")
        }
      }
      buffer.println("return entities;")
      buffer.indentDown
      buffer.println("}")
    }

    def make_delete_entity_method {
      buffer.method("public static void delete_entity(%s key, PersistenceManager pm)".format(idAttr.typeName)) {
        buffer.println("Transaction tx = pm.currentTransaction();")
        buffer.makeTry {
          buffer.print(name)
          buffer.println(" entity = get_entity(key, pm);")
          buffer.println("entity.delete_persistent(pm);")
          buffer.println("tx.commit();")
        }
        buffer.makeFinally {
          buffer.makeIf("tx.isActive()") {
            buffer.println("tx.rollback();")
          }
        }
      }
    }

    def make_entity_fill_method {
      buffer.method("void entity_fill(PersistenceManager pm)") { // XXX
        for (attr <- attributes) {
          attr.attributeType match {
            case e: GaejEntityType => {
              buffer.makeIf("false") {
                buffer.println("fill_" + attr.name + "(pm);")
              }
            }
            case p: GaejEntityPartType => {
              buffer.makeIf("true") {
                buffer.println("fill_" + attr.name + "(pm);")
              }
            }
            case p: GaejPowertypeType => {
              buffer.makeIf("true") {
                buffer.println("fill_" + attr.name + "(pm);")
              }
            }
            case _ => //
          }
        }
      }
    }

    def make_entity_restore_method {
      buffer.method("void entity_restore()") {
        for (attr <- attributes) {
          attr.attributeType match {
            case e: GaejEntityType => {
              buffer.println("restore_" + attr.name + "();")
            }
            case p: GaejEntityPartType => {
              buffer.println("restore_" + attr.name + "();")
            }
            case p: GaejPowertypeType => {
              buffer.println("restore_" + attr.name + "();")
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
      def add_attributes(buf: JavaTextMaker) {
        buf.indentUp
        buf.indentUp
        for (attr <- attributes) {
          val attrName = {
            if (attr.modelAttribute != null) {
              attr.modelAttribute.name
            } else if (attr.modelAssociation != null) {
              attr.modelAssociation.name
            } else {
              "?"
            }
          }
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
          buf.println("""entity.addAttribute("%s", "%s", "%s", "%s");""".
                      format(attrName, attrType, columnName, columnType))
        }
        buf.indentDown
        buf.indentDown
      }

      buffer.append(code, Map("%entityName%" -> qualifiedName))
      buffer.replace("%addAttributes%")(add_attributes)
    }

    buffer.print("@PersistenceCapable(identityType=IdentityType.APPLICATION")
    if (modelEntity.jdo.detachable) {
      buffer.print(", detachable=\"true\"")
    }
    if (notNull(modelEntity.jdo.catalog)) {
      buffer.print(", catalog=\"")
      buffer.print(modelEntity.jdo.catalog)
      buffer.print("\"")
    }
    if (notNull(modelEntity.jdo.schema)) {
      buffer.print(", schema=\"")
      buffer.print(modelEntity.jdo.schema)
      buffer.print("\"")
    }
    if (notNull(modelEntity.jdo.table)) {
      buffer.print(", table=\"")
      buffer.print(modelEntity.jdo.table)
      buffer.print("\"")
    } else if (notNull(modelEntity.sql.table)) {
      buffer.print(", table=\"")
      buffer.print(modelEntity.sql.table)
      buffer.print("\"")
    }      
    buffer.println(")")
    buffer.print("public class ")
    buffer.print(name)
    getBaseObject match {
      case Some(base) => {
        buffer.print(" extends ")
        buffer.print(base.name)
      }
      case None => {}
    }
    buffer.print(" {")
    buffer.println()
    buffer.indentUp
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
    buffer.indentDown
    make_entity_info
    buffer.println("}")
  }

/* 2009-07-20
  final def setBaseClass(aBaseClass: GaejEntityType) {
    _baseClass = aBaseClass
  }
*/
}
