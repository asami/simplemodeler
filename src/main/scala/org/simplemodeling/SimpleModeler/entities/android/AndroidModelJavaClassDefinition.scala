package org.simplemodeling.SimpleModeler.entities.android

import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.dsl.datatype.XInt
import org.simplemodeling.dsl.SDatatype
import org.simplemodeling.dsl.SDatatypeFunction
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * @since   Jul. 21, 2011
 * @version Sep. 25, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidModelJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends ModelJavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false
  customBaseName = Some("GModel<%s, %s>".format(contextName, errorModelName))
  val driver = "I" + pContext.className(modelPackage, "RestDriver")
  customAttributes += new PAttribute("context", new PObjectReferenceType("Context", "android.os"), true, true)
  customAttributes += new PAttribute("driver", new PObjectReferenceType(driver, packageName), true, true)

  private def _cache_name(entity: PEntityEntity) = entity.termNameBase + "RestFeedRepository";
  private def _cache_var_name(entity: PEntityEntity) = entity.termName + "_repository"

  override protected def head_imports_Extension {
    super.head_imports_Extension
    jm_import("android.content.*")
    jm_import("android.widget.ListAdapter")
    jm_import("org.goldenport.android.*")
    jm_import("org.goldenport.android.feed.*")
  }
/*
  override protected def attribute_variables_extension {
    jm_pln("@Inject")
    jm_private_instance_variable_single(contextName, "context")
    jm_pln("@Inject")
    jm_private_instance_variable_single(factoryName, "factory")
    jm_pln("@Inject")
    jm_private_instance_variable_single(repositoryname, "repository")
    jm_pln("@Inject")
    jm_private_instance_variable_single(driver, "driver")
  }
*/

  override protected def package_variables_EntityEntity(entity: PEntityEntity) {
    jm_private_instance_variable_single(_cache_name(entity), _cache_var_name(entity))
  }

  override protected def lifecycle_methods {
    jm_override_public_method("void open()") {
      jm_pln("super.open();")
      jm_pln("gFactory = gContext.getFactory();")
      jm_pln("driver.open(get_pref_server_url());")
      for (entity <- pobject.packageEntities) {
        jm_assign_new(_cache_var_name(entity), _cache_name(entity), "driver")
      }
    }
    jm_code("""
@Override
public void close() {
    super.close();
    driver.close();
}
""")
  }
  
  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    val rep = _cache_var_name(entity)
    jm_public_method("ListAdapter get%sRestFeedAdapter()", entity.termNameBase) {
      jm_return("new %sRestFeedAdapter(context, this, %s)", entity.termNameBase, rep); 
    }
  }
}
