package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._

/*
 * @since   Jul. 13, 2011
 *  version Aug.  7, 2011
 *  version Dec. 13, 2011
 * @version Nov. 11, 2012
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
    val cursor = "Cursor<%s>".format(classname)
    val query = "Query"
    jm_public_method("void create%s(%s data) throws IOException", classname, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void createTx%s(%s data, ITransactionContext tx) throws IOException", classname, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void create%s(%s data) throws IOException", classname, docname) {
      jm_pln("create%s(new %s(data));".format(classname, classname))
    }
    jm_public_method("void createTx%s(%s data, ITransactionContext tx) throws IOException", classname, docname) {
      jm_pln("createTx%s(new %s(data), tx);".format(classname, classname))
    }
    for (id <- entity.idAttrOption) {
      val idtype = id.typeName
      jm_public_method("%s createId%s(%s data) throws IOException", idtype, classname, classname) {
        jm_UnsupportedOperationException
      }
      jm_public_method("%s createIdTx%s(%s data, ITransactionContext tx) throws IOException", idtype, classname, classname) {
        jm_UnsupportedOperationException
      }
      jm_public_method("%s createId%s(%s data) throws IOException", idtype, classname, docname) {
        jm_return("createId%s(new %s(data))".format(classname, classname))
      }
      jm_public_method("%s createIdTx%s(%s data, ITransactionContext tx) throws IOException", idtype, classname, docname) {
        jm_return("createId%s(new %s(data))".format(classname, classname))
      }
      jm_public_method("%s get%s(%s id) throws IOException", classname, classname, idtype) {
        jm_UnsupportedOperationException
      }
      jm_public_method("%s getTx%s(%s id, ITransactionContext tx) throws IOException", classname, classname, idtype) {
        jm_UnsupportedOperationException
      }
      jm_public_method("%s get%sDocument(%s id) throws IOException", docname, classname, idtype) {
        jm_get_return_expr_or_null(classname, "get%s(id)".format(classname))("%s.make_document()")
      }
      jm_public_method("%s getTx%sDocument(%s id, ITransactionContext tx) throws IOException", docname, classname, idtype) {
        jm_get_return_expr_or_null(classname, "get%s(id)".format(classname))("%s.make_document()")
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
    jm_public_method("void update%s(%s data) throws IOException", classname, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void updateTx%s(%s data, ITransactionContext tx) throws IOException", classname, classname) {
      jm_UnsupportedOperationException
    }
    jm_public_method("void update%s(%s data) throws IOException", classname, docname) {
      jm_pln("update%s(new %s(data));".format(classname, classname))
    }
    jm_public_method("void updateTx%s(%s data, ITransactionContext tx) throws IOException", classname, docname) {
      jm_pln("update%s(new %s(data));".format(classname, classname))
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
    jm_public_method("void updateTx%s(Map<String, Object> data, ITransactionContext tx) throws IOException", classname) {
      jm_UnsupportedOperationException
    }
    for (id <- entity.idAttrOption) {
      val idtype = id.typeName
      jm_public_method("void delete%s(%s id) throws IOException", classname, idtype) {
        jm_UnsupportedOperationException
      }
      jm_public_method("void deleteTx%s(%s id, ITransactionContext tx) throws IOException", classname, idtype) {
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
