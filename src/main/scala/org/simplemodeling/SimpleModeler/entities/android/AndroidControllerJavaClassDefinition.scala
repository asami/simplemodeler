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
 * @since   Aug.  7, 2011
 * @version Sep. 25, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidControllerJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends ControllerJavaClassDefinition(pContext, aspects, pobject) {
  customBaseName = Some("GController<%s, %s, %s, %s>".format(contextName, errorModelName, modelName, agentName))

  override protected def head_imports_Extension {
    super.head_imports_Extension
    jm_import("android.content.*")
    jm_import("android.widget.ListAdapter")
    jm_import("org.goldenport.android.*")
    jm_import("org.goldenport.android.feed.*")
  }

  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    jm_public_method("ListAdapter get%sRestFeedAdapter()", entity.classNameBase) {
      jm_return("gmodel.get%sRestFeedAdapter()", entity.classNameBase)
    }
  }
}
