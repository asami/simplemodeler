package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Nov. 10, 2012
 * @version Nov. 11, 2012
 * @author  ASAMI, Tomoharu
 */
class EventServiceJavaClassDefinition(
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
""", Map("%context%" -> contextName, "%repository%" -> entityServiceName))
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
    if (!entity.isEvent) return
    val classname = entity.name;
    val docname = entity.documentName
    val cursor = "Cursor<%s>".format(classname)
    val query = "Query"
    val idtype = entity.idAttr.typeName
    jm_public_method("Response<Void> issue%s(Request<%s> data)", classname, docname) {
      code_try_ok_failure {
        jm_pln("repository.create%s(data.value);", classname)
      }
    }
    jm_public_method("Response<Void> issue%s(%s data)", classname, docname) {
      code_try_ok_failure {
        jm_pln("repository.create%s(data);", classname)
      }
    }
    jm_public_method("Response<%s> issueId%s(Request<%s> data)", idtype, classname, docname) {
      code_try_ok_failure {
        jm_pln("repository.createId%s(data.value);", classname)
      }
    }
    jm_public_method("Response<%s> issueId%s(%s data)", idtype, classname, docname) {
      code_try_ok_failure {
        jm_pln("repository.createId%s(data);", classname)
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
