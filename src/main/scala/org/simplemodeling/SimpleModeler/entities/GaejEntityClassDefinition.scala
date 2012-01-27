package org.simplemodeling.SimpleModeler.entities

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.goldenport.util.Options

/*
 * @since   Jun.  6, 2011
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */    
class GaejEntityClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity) extends JavaClassDefinition(pContext, aspects, pobject) {
  override protected def head_imports_Epilogue {
    jm_import("javax.jdo.PersistenceManager")
    jm_import("javax.jdo.Query")
    jm_import("javax.jdo.Transaction")
    jm_import("javax.jdo.annotations.*")
    jm_import("com.google.appengine.api.datastore.*")
    jm_import("com.google.appengine.api.datastore.Key")
    jm_import("com.google.appengine.api.users.User")
  }

  protected def jdo_detachable: Boolean = {
    modelEntityOption match {
      case Some(me) => me.jdo.detachable
      case None => false
    }
  }

  protected def jdo_catalog: Option[String] = {
    modelEntityOption match {
      case Some(me) => Options.lift(me.jdo.catalog)
      case None => None
    }
  }

  protected def jdo_schema: Option[String] = {
    modelEntityOption match {
      case Some(me) => Options.lift(me.jdo.schema)
      case None => None
    }
  }

  protected def jdo_table: Option[String] = {
    modelEntityOption match {
      case Some(me) => Options.lift(me.jdo.table)
      case None => None
    }
  }

  protected def sql_table: Option[String] = {
    modelEntityOption match {
      case Some(me) => Options.lift(me.sql.table)
      case None => None
    }
  }

  protected def appengine_use_key: Boolean = {
    modelEntityOption match {
      case Some(me) => me.appEngine.use_key
      case None => false
    }
  }

  override protected def class_open_Annotation {
    jm_p("@PersistenceCapable(identityType=IdentityType.APPLICATION")
    if (jdo_detachable) {
      jm_p(", detachable=\"true\"")
    }
    for (catalog <- jdo_catalog) {
      jm_p(", catalog=\"")
      jm_p(catalog)
      jm_p("\"")
    }
    for (schema <- jdo_schema) {
      jm_p(", schema=\"")
      jm_p(schema)
      jm_p("\"")
    }
    if (jdo_table.isDefined) {
      jm_p(", table=\"")
      jm_p(jdo_table.get)
      jm_p("\"")
    } else if (sql_table.isDefined) {
      jm_p(", table=\"")
      jm_p(sql_table.get)
      jm_p("\"")
    }
    jm_pln(")")
  }

  override protected def attribute_variables_extension {
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

  override protected def lifecycle_methods_init_method {
    jm_public_method("void init_document(%s doc, PersistenceManager pm)", documentName) {
      for (attr <- attributes) {
// XXX
//        update_attribute(attr)
      }
      jm_pln("entity_fill(pm);");
    }
  }

  override protected def persistent_methods_make_persistent_method {
    jm_public_method("void make_persistent(PersistenceManager pm)") {
      if (is_logical_operation) {
        val varName = var_name(idAttr)
        if (jdo_table.isDefined) {
          jm_var_String("kind", "\"" + jdo_table.get + "\"")
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

  override protected def persistent_methods_delete_persistent_method {
    jm_public_method("void delete_persistent(PersistenceManager pm)") {
      if (is_logical_operation) {
        jm_pln("_entity_removed = _entity_updated = new Date();")
        jm_pln("pm.makePersistent(this);")
      } else {
        jm_pln("pm.deletePersistent(this);")
      }
    }
  }

  override protected def persistent_methods_get_entity_method {
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
      } else if (appengine_use_key) {
        if (jdo_table.isDefined) {
          jm_var_String("kind", "\"" + jdo_table.get + "\"")
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

  override protected def persistent_methods_get_entities_method {
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

  override protected def persistent_methods_delete_entity_method {
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

  override protected def persistent_methods_entity_fill_method {
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

  override protected def persistent_methods_entity_restore_method {
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

  override protected def object_auxiliary {
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
            } else if (attr.modelPowertype != null) {
              attr.modelPowertype.dslPowertypeRelationship.powertype.qualifiedName
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
}
