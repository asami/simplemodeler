package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Jul. 13, 2011
 *  version Aug.  7, 2011
 *  version Dec. 13, 2011
 * @version Nov. 19, 2012
 * @author  ASAMI, Tomoharu
 */
class RepositoryJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false

  override protected def head_imports_Extension {
    jm_import("java.io.IOException")
    jm_import("org.simplemodeling.SimpleModeler.runtime.*")
    head_imports_Inject_Inject
  }

  override protected def constructors_copy_constructor {
  }

  override protected def attribute_variables_extension {
    jm_code("""
@Inject
protected %context% context;
""", Map("%context%" -> contextName))
  }

  override protected def package_methods_Actor(actor: SMDomainActor) {
    package_methods_Entity(actor)
  }

  override protected def package_methods_Resource(resource: SMDomainResource) {
    package_methods_Entity(resource)
  }

  override protected def package_methods_Event(event: SMDomainEvent) {
    package_methods_Entity(event)
  }

  override protected def package_methods_Role(role: SMDomainRole) {
    package_methods_Entity(role)
  }

  override protected def package_methods_Summary(summary: SMDomainSummary) {
    package_methods_Entity(summary)
  }

  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    val classname = entity.name;
    val docname = entity.documentName
    val mcreate = code_method_name_create(entity)
    val mcreateid = code_method_name_create_id(entity)
    val mcreatetx = code_method_name_create_tx(entity)
    val mcreateidtx = code_method_name_create_id_tx(entity)
    val mget = code_method_name_get(entity)
    val mgetdoc = code_method_name_get_doc(entity)
    val mgettx = code_method_name_get_tx(entity)
    val mgetdoctx = code_method_name_get_doc_tx(entity)
    val mupdate = code_method_name_update(entity)
    val mupdatetx = code_method_name_update_tx(entity)
    val mdelete = code_method_name_delete(entity)
    val mdeletetx = code_method_name_delete_tx(entity)
    val cursor = "Cursor<%s>".format(classname)
    val query = "Query"
    jm_public_method("void %s(%s data) throws IOException", mcreate, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void %s(%s data, ITransactionContext tx) throws IOException", mcreatetx, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void %s(%s data) throws IOException", mcreate, docname) {
      jm_pln("%s(new %s(data));".format(mcreate, classname))
    }
    jm_public_method("void %s(%s data, ITransactionContext tx) throws IOException", mcreatetx, docname) {
      jm_pln("%s(new %s(data), tx);".format(mcreatetx, classname))
    }
    for (id <- entity.idAttrOption) {
      val idtype = id.typeName
      jm_public_method("%s %s(%s data) throws IOException", idtype, mcreateid, classname) {
        jm_UnsupportedOperationException
      }
      jm_public_method("%s %s(%s data, ITransactionContext tx) throws IOException", idtype, mcreateidtx, classname) {
        jm_UnsupportedOperationException
      }
      jm_public_method("%s %s(%s data) throws IOException", idtype, mcreateid, docname) {
        jm_return("%s(new %s(data))".format(mcreateid, classname))
      }
      jm_public_method("%s %s(%s data, ITransactionContext tx) throws IOException", idtype, mcreateidtx, docname) {
        jm_return("%s(new %s(data), tx)".format(mcreateidtx, classname))
      }
      jm_public_method("%s %s(%s id) throws IOException", classname, mget, idtype) {
        jm_UnsupportedOperationException
      }
      jm_public_method("%s %s(%s id, ITransactionContext tx) throws IOException", classname, mgettx, idtype) {
        jm_UnsupportedOperationException
      }
      jm_public_method("%s %s(%s id) throws IOException", docname, mgetdoc, idtype) {
        jm_get_return_expr_or_null(classname, "%s(id)".format(mget))("%s.make_document()")
      }
      jm_public_method("%s %s(%s id, ITransactionContext tx) throws IOException", docname, mgetdoctx, idtype) {
        jm_get_return_expr_or_null(classname, "%s(id, tx)".format(mgettx))("%s.make_document()")
      }
    }
/*
    jm_public_method("%s query%s(%s query) throws IOException", cursor, classname, query) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s query%s(String query) throws IOException", cursor, classname) {
      jm_UnsupportedOperationException
    }
*/
    jm_public_method("void %s(%s data) throws IOException", mupdate, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void %s(%s data, ITransactionContext tx) throws IOException", mupdatetx, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void %s(%s data) throws IOException", mupdate, docname) {
      jm_pln("%s(new %s(data));".format(mupdate, classname))
    }
    jm_public_method("void %s(%s data, ITransactionContext tx) throws IOException", mupdatetx, docname) {
      jm_pln("%s(new %s(data), tx);".format(mupdatetx, classname))
    }
    jm_public_method("void %s(String data) throws IOException", mupdate) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void %s(String[] data) throws IOException", mupdate) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void %s(Map<String, Object> data) throws IOException", mupdate) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void %s(Map<String, Object> data, ITransactionContext tx) throws IOException", mupdatetx) {
      jm_UnsupportedOperationException
    }
    for (id <- entity.idAttrOption) {
      val idtype = id.typeName
      jm_public_method("void %s(%s id) throws IOException", mdelete, idtype) {
        jm_UnsupportedOperationException
      }
      jm_public_method("void %s(%s id, ITransactionContext tx) throws IOException", mdeletetx, idtype) {
        jm_UnsupportedOperationException
      }
    }
  }

  // XXX platform object
  override protected def package_methods_Entity_Part(part: SMDomainEntityPart) {
  }

  // XXX platform object
  override protected def package_methods_Powertype(powertype: SMDomainPowertype) {
  }

  // XXX platform object
  override protected def package_methods_Id(id: SMDomainValueId) {
  }

  // XXX platform object
  override protected def package_methods_Name(name: SMDomainValueName) {
  }

  // XXX platform object
  override protected def package_methods_Value(value: SMDomainValue) {
  }

  // XXX platform object
  override protected def package_methods_Document(document: SMDomainDocument) {
  }

  // XXX platform object
  override protected def package_methods_Rule(rule: SMDomainRule) {
  }

  // XXX platform object
  override protected def package_methods_Service(service: SMDomainService) {
  }
}
