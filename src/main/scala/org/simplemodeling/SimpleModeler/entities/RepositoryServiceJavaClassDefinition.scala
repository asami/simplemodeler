package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Nov.  2, 2012
 * @version Nov.  2, 2012
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
    val cursor = "Cursor<%s>".format(classname)
    val query = "Query"
    val idtype = "long"
    jm_public_method("void create%s(%s data) throws IOException", classname, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s get%s(%s id) throws IOException", classname, classname, idtype) {
      jm_UnsupportedOperationException
    }
/*
    jm_public_method("%s query%s(%s query) throws IOException", cursor, classname, query) {
      jm_UnsupportedOperationException
    }
    jm_public_method("%s query%s(String query) throws IOException", cursor, classname) {
      jm_UnsupportedOperationException
    }
*/
    jm_public_method("void update%s(%s data) throws IOException", classname, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(String data) throws IOException", classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(String[] data) throws IOException", classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(Map<String, Object> data) throws IOException", classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void delete%s(%s id) throws IOException", classname, idtype) {
      jm_UnsupportedOperationException
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
