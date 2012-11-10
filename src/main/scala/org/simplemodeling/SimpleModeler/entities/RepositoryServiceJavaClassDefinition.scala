package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Nov.  2, 2012
 * @version Nov. 11, 2012
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
    val classname = entity.name;
    val docname = entity.documentName
    val cursor = "Cursor<%s>".format(classname)
    val query = "Query"
    jm_public_method("Response<Void> create%s(Request<%s> data)", classname, docname) {
      code_try_ok_failure {
        jm_pln("repository.create%s(data.value);", classname)
      }
    }
    jm_public_method("Response<Void> createTx%s(Request<%s> data)", classname, docname) {
      code_try_ok_failure {
        jm_pln("repository.createTx%s(data.value, data.transaction);", classname)
      }
    }
    jm_public_method("Response<Void> create%s(%s data)", classname, docname) {
      code_try_ok_failure {
        jm_pln("repository.create%s(data);", classname)
      }
    }
    for (id <- entity.idAttrOption) {
      val idtype = id.objectTypeName
      jm_public_method("Response<%s> createId%s(Request<%s> data)", idtype, classname, docname) {
        code_try_return_failure("repository.createId%s(data.value)", classname)
      }
      jm_public_method("Response<%s> createId%s(%s data)", idtype, classname, docname) {
        code_try_return_failure("repository.createId%s(data)", classname)
      }
      jm_public_method("Response<%s> get%s(Request<%s> id)", docname, classname, idtype) {
        code_try_return_failure("repository.get%sDocument(id.value)", classname)
      }
      jm_public_method("Response<%s> get%s(%s id)", docname, classname, id.typeName) {
        code_try_return_failure("repository.get%sDocument(id)", classname)
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
    jm_public_method("Response<Void> update%s(%s data)", classname, docname) {
      code_try_ok_failure {
        jm_pln("repository.update%s(data);", classname)
      }
    }
    jm_public_method("Response<Void> update%s(String data)", classname) {
      code_try_ok_failure {
        jm_pln("repository.update%s(data);", classname)
      }
    }
    jm_public_method("Response<Void> update%s(String[] data)", classname) {
      code_try_ok_failure {
        jm_pln("repository.update%s(data);", classname)
      }
    }
    jm_public_method("Response<Void> update%s(Map<String, Object> data)", classname) {
      code_try_ok_failure {
        jm_pln("repository.update%s(data);", classname)
      }
    }
    for (id <- entity.idAttrOption) {
      val idtype = id.objectTypeName
      jm_public_method("Response<Void> delete%s(Request<%s> id)", classname, idtype) {
        code_try_ok_failure {
          jm_pln("repository.delete%s(id.value);", classname)
        }
      }
      jm_public_method("Response<Void> delete%s(%s id)", classname, id.typeName) {
        code_try_ok_failure {
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
