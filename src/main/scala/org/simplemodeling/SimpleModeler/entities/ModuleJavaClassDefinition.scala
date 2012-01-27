package org.simplemodeling.SimpleModeler.entities

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessUsecase
import org.simplemodeling.SimpleModeler.entity.business.SMBusinessTask
import scala.collection.mutable.ArrayBuffer

/*
 * @since   Jul. 14, 2011
 * @version Aug.  7, 2011
 * @author  ASAMI, Tomoharu
 */
class ModuleJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  customBaseName = Some("AbstractModule")

  val entries = new ArrayBuffer[PModuleEntry]

  override protected def head_imports_Extension {
    jm_import("com.google.inject.*")
  }

  override protected def constructors_null_constructor {
    jm_public_constructor("%s()", name) {
      jm_pln("this(new %s());", pContext.contextName(modelPackage))
    }
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
    jm_public_constructor("%s(%s context)", name, pContext.contextName(modelPackage)) {
      jm_assign_this("context", "context")
    }
  }

  override protected def attribute_variables_extension {
    jm_pln("private %s context;", contextName)
  }

  override protected def object_auxiliary {
    jm_override_protected_method("void configure()") {
      for (entry <- entries) {
        jm_p("bind(%s.class)", entry.specificationName)
        for (iname <- entry.implementationName) {
          jm_p(".to(%s.class)", iname)
        }
        if (entry.isSingleton) {
          jm_p(".in(Singleton.class)")
        }
        jm_pln(";")
      }
      traverse(new JavaMakerModelElementVisitor(jm_maker) {
        override protected def visit_Object(obj: SMObject) {
          jm_pln("bind(%s.class);", obj.name)
//          jm_pln("bind(Controller.class).to(Controller1.class).in(Singleton.class);") // XXX
        }
      })
    }
    jm_code("""
@Provides @Singleton
public %context% provide%context%() {
    return context;
}
""", Map("%context%" -> contextName))
  }
}
