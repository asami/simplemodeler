package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Nov.  2, 2012
 * @version Nov.  3, 2012
 * @author  ASAMI, Tomoharu
 */
class RepositoryServiceJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false

  override protected def head_imports_Extension {
    jm_import("java.io.IOException")
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
    val idtype = "long"
    jm_public_method("void create%s(%s data) throws IOException", classname, docname) {
      jm_pln("repository.create%s(data);", classname)
    }
    jm_public_method("%s get%s(%s id) throws IOException", docname, classname, idtype) {
      jm_return("repository.get%sDocument(id)", classname)
    }
/*
    jm_public_method("%s query%s(%s query) throws IOException", cursor, classname, query) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s query%s(String query) throws IOException", cursor, classname) {
      jm_UnsupportedOperationException
    }
*/
    jm_public_method("void update%s(%s data) throws IOException", classname, docname) {
      jm_pln("repository.update%s(data);", classname)
    }
    jm_public_method("void update%s(String data) throws IOException", classname) {
      jm_pln("repository.update%s(data);", classname)
    }
    jm_public_method("void update%s(String[] data) throws IOException", classname) {
      jm_pln("repository.update%s(data);", classname)
    }
    jm_public_method("void update%s(Map<String, Object> data) throws IOException", classname) {
      jm_pln("repository.update%s(data);", classname)
    }
    jm_public_method("void delete%s(%s id) throws IOException", classname, idtype) {
      jm_pln("repository.delete%s(id);", classname)
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