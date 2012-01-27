package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessUsecase
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessTask
import scala.collection.mutable.ArrayBuffer

/*
 * @since   Jul. 13, 2011
 *  version Aug. 26, 2011
 * @version Dec. 13, 2011
 * @author  ASAMI, Tomoharu
 */
class FactoryJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false

  val entries = new ArrayBuffer[PModuleEntry]

  override protected def head_imports_Extension {
    jm_import("org.json.*")
    jm_import("com.google.inject.*")
  }

  override protected def attribute_variables_extension {
    jm_code("""
private static %factory% factory;
private %context% context;
private Injector injector;
""", Map("%context%" -> contextName,
         "%factory%" -> factoryName))
  }

  override protected def constructors_null_constructor {
    jm_public_constructor("%s()", name) {
      jm_pln("this(new %s());", moduleName)
    }
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
    val contextname = pContext.contextName(pobject)
    val modulename = pContext.moduleName(pobject)
    val factoryname = pContext.factoryName(pobject)
    jm_public_constructor("%s(Module... modules)", factoryname) {
      jm_assign_this("injector", "Guice.createInjector(modules)")
      jm_assign_this("context", "injector.getInstance(%s.class)", contextName)
      jm_pln("context.setFactory(this);")
      jm_pln("factory = this;")
    }
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

  override protected def package_methods_Entity(entity: SMDomainEntity) {
    _create_method(entity.name)
  }

  private def _create_method(classname: String) {
    jm_public_method("%s create%s()", classname, classname) {
      jm_return("(%s)injector.getInstance(%s.class)", classname, classname)
    }
  }

  override protected def package_methods_Entity_Part(part: SMDomainEntityPart) {
  }

  override protected def package_methods_Powertype(powertype: SMDomainPowertype) {
  }

  override protected def package_methods_Id(id: SMDomainValueId) {
  }

  override protected def package_methods_Name(name: SMDomainValueName) {
  }

  override protected def package_methods_Value(value: SMDomainValue) {
  }

  override protected def package_methods_Document(document: SMDomainDocument) {
  }

  override protected def package_methods_Rule(rule: SMDomainRule) {
  }

  override protected def package_methods_Service(service: SMDomainService) {
  }

  override protected def package_methods_Extension {
    for (entry <- entries) {
      _create_method(entry.specificationName)
    }
    jm_public_static_method("%s getFactory()", factoryName) {
      jm_return("factory")
    }
  }
}
