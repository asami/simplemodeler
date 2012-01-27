package org.simplemodeling.SimpleModeler.entities

import scala.collection.mutable.ArrayBuffer
import org.simplemodeling.SimpleModeler.entity._
import com.asamioffice.goldenport.text.UString.notNull
import org.simplemodeling.dsl._
import java.text.SimpleDateFormat
import java.util.TimeZone

/*
 * Google App Engine Java Class Attribute Definition
 * 
 * @since   Jun. 21, 2011
 * @version Aug.  6, 2011
 * @author  ASAMI, Tomoharu
 */
class GaejEntityClassAttributeDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  attr: PAttribute,
  owner: GaejEntityClassDefinition,
  jmaker: JavaMaker) extends JavaClassAttributeDefinition(pContext, aspects, attr, owner, jmaker) {

  val refVarName: String = null
  val persistentVarName: String = null
  val cacheTimestampVarName = null;
  val cacheVarName = "";
  val partVarName = "";
  val powerVarName = "";
  def is_owned_property = false

  protected def attribute_variables_plain_variable_Private_Instance_Variable(attr: PAttribute, typename: String, varname: String) {
    jm_private_instance_variable(attr, typename, varname);
  }

  protected def attribute_variables_plain_variable_Private_Transient_Instance_Variable(attr: PAttribute, typename: String, varname: String) {
    jm_private_transient_instance_variable(attr, typename, varname);
  }

  protected def attribute_variables_aggregate_BackReference(source: SMObject, assoc: SMAssociation) {
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

  override protected def method_bean_single_entity(e: PEntityType) {
    def single_fill {
      pm_delegate_method("fill_" + attrName)
      jm_protected_final_void_method("fill_" + attrName + "(PersistenceManager pm)") {
        jm_if_return(refVarName + " != null")
        if (attr.isCache) {
          jm_if("entity_context.isAssociationCache(this, %s) && %s != null".format(cacheTimestampVarName, cacheVarName)) {
            jm_try {
              jm_assign_new(refVarName, e.entity.name)
              jm_pln("%s.init_document(%s, pm);".format(refVarName, cacheVarName))
              jm_return
            }
            jm_catch_end("Exception e") {
              jm_assign_null(cacheTimestampVarName)
              jm_assign_null(cacheVarName)
            }
          }
        }
        if (is_owned_property) {
          jm_assign(refVarName, persistentVarName)
          jm_assign_true(entity_ref_is_loaded_var_name())
        } else if (is_query_property) {
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
""", Map("%entityName%" -> e.entity.name,
    "%refVarName%" -> refVarName,
    "%backRefVarName%" -> back_reference_var_name()))
        } else {
          jm_if(persistentVarName + " != null") {
            jm_assign(refVarName, "%s.get_entity(%s, pm)", e.entity.name, pContext.variableName4RefId(attr))
          }
        }
      }
    }

    def single_is_update_cache {
      if (is_owned_property) {
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
      } else if (is_query_property) {
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

    def single_restore {
      if (is_owned_property || is_query_property) {
        jm_protected_final_void_method("restore_%s()", attrName) {
          jm_code("""        if (is_update_cache_%varName%()) {
            %varName%_cache = %varName%_association.document();
            %varName%_cache_timestamp = new Date();
      } else {
          %varName%_cache_timestamp = null;
          %varName%_cache = null;
      }
""", Map("%varName%" -> varName,
    "%docName%" -> persistent_element_type))
        }
      } else {
        jm_protected_final_void_method("restore_%s()", attrName) {
          jm_code("""        if (is_update_cache_%varName%()) {
            %varName%_id = %varName%_association.getId();
            %varName%_cache = %varName%_association.document();
            %varName%_cache_timestamp = new Date();
      } else {
          %varName%_cache_timestamp = null;
          %varName%_cache = null;
      }
""", Map("%varName%" -> varName,
    "%docName%" -> persistent_element_type))
        }
      }
    }

    def single_get {
      jm_public_method("%s get%s()", java_type, attrName.capitalize) {
        jm_pln("fill_%s();", attrName);
        jm_return(refVarName);
      }
    }

    def single_set {
      if (is_settable()) {
        jm_public_void_method("set%s(%s %s)", attrName.capitalize, java_type, paramName) {
          if (is_owned_property) {
            jm_pln("// TODO owned (set part built from doc)")
          } else if (is_query_property) {
            jm_pln("// TODO query (set part built from doc)")
          } else {
            jm_assign_this(refVarName, paramName);
            jm_assign_this(persistentVarName, "%s.get%s()", paramName, e.entity.idName.capitalize) 
          }
        }
      }
    }
      
    single_fill
    single_is_update_cache
    single_restore
    single_get
    single_set
  }

  override protected def method_bean_single_part(p: PEntityPartType) {
    pm_delegate_method("fill_" + attrName)
    jm_protected_final_void_method("fill_%s(PersistenceManager pm)", attrName) { 
      jm_if_else("%s != null && %s == null", persistentVarName, partVarName) {
        jm_assign_new(partVarName, java_element_type, "%s.toString(), pm", persistentVarName)
      }
      jm_else_if_else("%s == null && %s == null", persistentVarName, partVarName) {
      }
      jm_else {
      }
    }
    jm_protected_final_void_method("restore_%s()", attrName) {
      jm_if(partVarName + " != null") {
        jm_assign_new(persistentVarName, "Text", "%s.toString()", partVarName)
      }
    }
    jm_public_get_method(java_type, attrName, partVarName)
    if (attr.attributeType == PBooleanType) {
      jm_public_is_method(attrName, varName)
    }
    if (is_settable()) {
      jm_public_set_method(attrName, java_type, paramName, partVarName)
    }
  }

  override protected def method_bean_single_powertype(e: PPowertypeType) {
    pm_delegate_method("fill_" + attrName)
    jm_protected_final_void_method("fill_%s(PersistenceManager pm)", attrName) {
      jm_if("%s != null", powerVarName) {
        jm_return
      }
      jm_if("entity_context.isAssociationCache(this, %s) && %s != null", cacheTimestampVarName, entity_ref_label_var_name) {
        jm_assign(powerVarName, "%s.get(%s, %s, %s)", java_element_type, persistentVarName, entity_ref_label_var_name, entity_ref_updated_var_name)
        jm_return
      }
      jm_if("%s != null", persistentVarName) {
        jm_assign(powerVarName, "%s.get(%s, pm)", java_element_type, persistentVarName)
      }
    }
    jm_protected_final_void_method("restore_%s()", attrName) {
      jm_if("%s != null", powerVarName) {
        jm_assign(persistentVarName, "%s.getKey()", powerVarName)
        jm_assign(entity_ref_label_var_name, "%s.getLabel()", powerVarName)
        jm_assign(entity_ref_updated_var_name, "%s.getUpdated()", powerVarName) 
      }
    }
    jm_public_get_method(java_type, attrName, powerVarName); 
    if (attr.attributeType == PBooleanType) {
      jm_public_is_method(attrName, varName)
    }
    if (is_settable()) {
      jm_public_set_method(attrName, java_type, paramName, powerVarName);
    }
  }

  override protected def method_bean_multi_entity(entityType: PEntityType) {
    def multi_try_reuse_cache {
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

    def multi_fill {
      pm_delegate_method("fill_" + attrName)
      jm_protected_final_void_method("fill_%s(PersistenceManager pm)", attrName) {
        multi_try_reuse_cache
        if (is_owned_property) {
          jm_pln("// owned")
          jm_assign_new_ArrayList(refVarName, persistent_element_type, persistentVarName)
          jm_assign_true(entity_ref_is_loaded_var_name)
        } else if (is_query_property) {
          jm_pln("// query")
          jm_code("""%entityName%Query query = query%capitalizePropertyName%(pm);
%entityName%Chunks chunks = query.execute();
%refVarName% = new ArrayList<%entityName%>(chunks.next());
""", Map("%entityName%" -> attr.elementTypeName,
    "%capitalizePropertyName%" -> attrName.capitalize,
    "%refVarName%" -> refVarName))
        } else {
          jm_if_not_null(persistentVarName) {
            jm_assign(refVarName, "%s.get_entities(%s, pm);", pContext.variableName4RefId(attr)) 
          }
        }
      }
    }
                
    def multi_is_update_cache {
      if (is_owned_property) {
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
    "%isLoadedVarName%" -> entity_ref_is_loaded_var_name))
        }
      } else if (is_query_property) {
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
    "%isLoadedVarName%" -> entity_ref_is_loaded_var_name))
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

    def multi_restore {
      if (is_owned_property || is_query_property) {
        jm_protected_final_void_method("restore_" + attrName + "()") {
          jm_code("""        // multi owned or query
        if (is_update_cache_%varName%()) {
            %varName%_cache = new ArrayList<%docName%>();
            for (%entityName% entity: %varName%_association) {
                %varName%_cache.add(entity.document());
            }
            %varName%_cache_timestamp = new Date();
        } else {
            %varName%_cache_timestamp = null;
            %varName%_cache = null;
        }
""", Map("%varName%" -> varName,
    "%entityName%" -> java_element_type,
    "%docName%" -> java_doc_element_type))
        }
      } else {
        jm_protected_final_void_method("restore_" + attrName + "()") {
          jm_code("""        // multi plain
        if (is_update_cache_%varName%()) {
            %varName%_cache = new ArrayList<%docName%>();
            for (%entityName% entity: %varName%_association) {
                %varName%_id.add(entity.getId());
                %varName%_cache.add(entity.document());
            }
            %varName%_cache_timestamp = new Date();
        } else {
            %varName%_cache_timestamp = null;
            %varName%_cache = null;
        }
""", Map("%varName%" -> varName,
    "%entityName%" -> java_element_type,
    "%docName%" -> java_doc_element_type))
        }
      }
    }

    def multi_get {
      jm_public_method("%s get%s()", java_type, attrName.capitalize) {
        jm_pln("fill_%s();".format(attrName))
        jm_if_else(refVarName + " != null") {
          jm_pln("return Collections.unmodifiableList(%s);", refVarName)
        }
        jm_else {
          jm_pln("return Collections.emptyList();")
        }
      }
    }

    def multi_query {
      if (is_query_property) {
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
    "%backRefIdVarName%" -> back_reference_var_name))
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
    "%backRefIdVarName%" -> back_reference_var_name))
        }
      }
    }

    def multi_set {
      if (is_settable()) {
        jm_public_void_method("set%s(%s %s)", attrName.capitalize, java_type, paramName) {
          jm_assign_this(refVarName, "new ArrayList<%s>(%s)", attr.elementTypeName, paramName)    
          if (is_owned_property) {
            jm_pln("// TODO crates owned multi entites.")
          } else if (is_query_property) {
            jm_pln("// TODO crates query multi entites.")
          } else {
            jm_for(persistent_element_type + " element: this." + refVarName) {
              jm_pln("this.%s..add(element.get());", persistentVarName, entityType.entity.idName.capitalize) 
            } 
          }
        }
      }
    }

    multi_fill
    multi_is_update_cache
    multi_restore
    multi_get
    multi_query
    multi_set
  }

  override protected def method_bean_multi_part(p: PEntityPartType) {
    pm_delegate_method("fill_" + attrName)
    jm_protected_final_void_method("fill_" + attrName + "(PersistenceManager pm)") {
      jm_if_else(persistentVarName + " != null && " + partVarName + " == null") {
        jm_pln("List<org.w3c.dom.Element> elements = Util.getElements(%s.toString());", partVarName);
        jm_for("org.w3c.dom.Element element: elements") {
          jm_pln("%s.add(new %s(element, pm));", partVarName, java_element_type);
          jm_else_if_else(persistentVarName + " == null && " + partVarName + " == null") {
          }
          jm_else {
          }
        }
        jm_protected_final_void_method("restore_" + attrName + "()") {
          jm_if(partVarName + " != null") {
            jm_var_new_StringBuilder
            jm_append_String("<list>")
            jm_for(java_element_type + " element: " + partVarName) {
              jm_pln("element.entity_asXmlString(buf);")
            }
            jm_append_String("</list>")
            jm_assign(persistentVarName, "new Text(buf.toString());"); 
          }
        }
        jm_public_method("public " + java_type + " get" + attrName.capitalize + " ()") {
          jm_pln("fill_%s();", attrName)
          jm_if_else(partVarName + " != null") {
            jm_pln("return Collections.unmodifiableList(%s);", partVarName)
          }
          jm_else {
            jm_pln("return Collections.emptyList();")
          }
        }
        if (is_settable) {
          jm_public_void_method("set" + attrName.capitalize + "(" + java_type + " " + paramName + ")") {
            jm_assign_this(partVarName, "new ArrayList<%s>(%s)", java_element_type, paramName)  
          }
          jm_public_void_method("public void add" + attrName.capitalize + "(" + java_element_type + " " + paramName + ")") {
            jm_if_null("this." + partVarName) {
              jm_assign_this(partVarName, "new ArrayList<%s>()", java_element_type) 
            }
            jm_pln("this.%s.add(%s);", partVarName, paramName) 
          }
        }
      }
    }
  }

  override protected def method_bean_multi_powertype(e: PPowertypeType) {
    pm_delegate_method("fill_" + attrName)
    jm_protected_final_void_method("fill_" + attrName + "(PersistenceManager pm)") {
      jm_if_else(persistentVarName + " != null && " + partVarName + " == null") {
        jm_pln("List<org.w3c.dom.Element> elements = Util.getElements(%s.toString());", partVarName)
        jm_for("org.w3c.dom.Element element: elements") {
          jm_pln("%s.add(new %s(element, pm));", partVarName, java_element_type)
        }
      }
      jm_else_if_else(persistentVarName + " == null && " + partVarName + " == null") {
      }
      jm_else {
      }
    }
    jm_protected_final_void_method("restore_" + attrName + "()") {
      jm_if(partVarName + " != null") {
        jm_var_new_StringBuilder
        jm_append_String("<list>")
        jm_for(java_element_type + " element: " + partVarName) {
          jm_pln("element.entity_asXmlString(buf);")
        }
        jm_append_String("</list>")
        jm_assign(persistentVarName, "new Text(buf.toString());") 
      }
    }
    jm_public_get_list_method_prologue(java_type, attrName, partVarName) {
      jm_pln("fill_%s()", attrName)
    }
    if (is_settable()) {
      jm_public_set_list_method(attrName, java_element_type, paramName, partVarName)
      jm_public_add_list_element_method(attrName, java_element_type, paramName, partVarName)
    }
  }

  def pm_delegate_method(name: String) {
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
}
