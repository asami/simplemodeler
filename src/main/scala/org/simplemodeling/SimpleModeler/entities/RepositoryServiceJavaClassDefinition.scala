package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Nov.  2, 2012
 * @version Nov. 12, 2012
 * @author  ASAMI, Tomoharu
 */
class RepositoryServiceJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false

  override protected def head_imports_Extension {
    jm_import("org.simplemodeling.SimpleModeler.runtime.*")
    jm_import("org.simplemodeling.SimpleModeler.runtime.Request.*")
    jm_import("org.simplemodeling.SimpleModeler.runtime.Response.*")
    head_imports_Inject_Inject
  }

  override protected def constructors_copy_constructor {
  }

  override protected def attribute_variables_extension {
    jm_code("""
@Inject
protected %context% context;
@Inject
protected %repository% repository;
""", Map("%context%" -> contextName, "%repository%" -> repositoryName))
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
    val qname = pobject.qualifiedName
    val classname = entity.name;
    val docname = entity.documentName
    val cursor = "Cursor<%s>".format(classname)
    val query = "Query"
    val createmethod = "create" + classname
    val createtxmethod = "createTx" + classname
    val createidmethod = "createId" + classname
    val createidtxmethod = "createIdTx" + classname
    val getmethod = "get" + classname
    val gettxmethod = "getTx" + classname
    val updatemethod = "update" + classname
    val updatetxmethod = "updateTx" + classname
    val deletemethod = "delete" + classname
    val deletetxmthod = "deleteTx" + classname
    jm_public_method("Response<Void> %s(Request<%s> data)", createmethod, docname) {
      code_request_try_ok_failure(qname, createmethod, "data") {
        jm_pln("repository.create%s(data.value);", classname)
      }
    }
/*
    jm_public_method("Response<Void> createTx%s(Request<%s> data)", classname, docname) {
      code_try_ok_failure {
        jm_pln("context.authorize(data.security);")
        jm_pln("repository.createTx%s(data.value, data.transaction);", classname)
      }
    }
*/
    jm_public_method("Response<Void> %s(%s data)", createmethod, docname) {
      code_try_ok_failure(qname, createmethod, "data") {
        jm_pln("repository.create%s(data);", classname)
      }
    }
    for (id <- entity.idAttrOption) {
      val idtype = id.objectTypeName
      jm_public_method("Response<%s> %s(Request<%s> data)", idtype, createidmethod, docname) {
        code_request_try_return_failure(qname, createidmethod, "data")("repository.createId%s(data.value)".format(classname))
      }
      jm_public_method("Response<%s> %s(%s data)", idtype, createidmethod, docname) {
        code_try_return_failure(qname, createidmethod, "data")("repository.createId%s(data)", classname)
      }
      jm_public_method("Response<%s> %s(Request<%s> id)", docname, getmethod, idtype) {
        code_request_try_return_failure(qname, getmethod, "id")("repository.get%sDocument(id.value)", classname)
      }
      jm_public_method("Response<%s> %s(%s id)", docname, getmethod, id.typeName) {
        code_try_return_failure(qname, getmethod, "id")("repository.get%sDocument(id)", classname)
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
    jm_public_method("Response<Void> %s(Request<%s> data)", updatemethod, docname) {
      code_request_try_ok_failure(qname, updatemethod, "data") {
        jm_pln("repository.update%s(data.value);", classname)
      }
    }
    jm_public_method("Response<Void> %s(%s data)", updatemethod, docname) {
      code_try_ok_failure(qname, updatemethod, "data") {
        jm_pln("repository.update%s(data);", classname)
      }
    }
    jm_public_method("Response<Void> %s(String data)", updatemethod) {
      code_try_ok_failure(qname, updatemethod, "data") {
        jm_pln("repository.update%s(data);", classname)
      }
    }
    jm_public_method("Response<Void> %s(String[] data)", updatemethod) {
      code_try_ok_failure(qname, updatemethod, "data") {
        jm_pln("repository.update%s(data);", classname)
      }
    }
    jm_public_method("Response<Void> %s(Map<String, Object> data)", updatemethod) {
      code_try_ok_failure(qname, updatemethod, "data") {
        jm_pln("repository.update%s(data);", classname)
      }
    }
    for (id <- entity.idAttrOption) {
      val idtype = id.objectTypeName
      jm_public_method("Response<Void> %s(Request<%s> id)", deletemethod, idtype) {
        code_request_try_ok_failure(qname, deletemethod, "id") {
          jm_pln("repository.delete%s(id.value);", classname)
        }
      }
      jm_public_method("Response<Void> %s(%s id)", deletemethod, id.typeName) {
        code_try_ok_failure(qname, deletemethod, "id") {
          jm_pln("repository.delete%s(id);", classname)
        }
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
