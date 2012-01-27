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
 * @version Sep.  1, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidModuleJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends ModuleJavaClassDefinition(pContext, aspects, pobject) {
  customBaseName = Some("GModule")
  val driverIf = pContext.restDriverInterface(pobject.modelPackage.get)
  val defaultDriver = pContext.restDriverDefault(pobject.modelPackage.get)

  override protected def head_imports_Extension {
    super.head_imports_Extension
    jm_import("android.content.*")
    jm_import("org.goldenport.android.*")
  }

  override protected def constructors_null_constructor = null
  override protected def constructors_copy_constructor = null

  override protected def constructors_plain_constructor {
    jm_public_constructor("%s(GContext context)", name) {
      jm_pln("super(context);")
    }
  }

  override protected def attribute_variables_extension = null

  override protected def object_auxiliary { // XXX configure
    jm_code("""
@Override
protected void configure() {
    configure_context();
    bind(%appcontext%.class).toInstance((%appcontext%)gcontext);
    bind(GErrorModel.class).to(%errormodel%.class).in(Singleton.class);
    bind(GModel.class).to(%model%.class).in(Singleton.class);
    bind(GAgent.class).to(%agent%.class).in(Singleton.class);
    bind(GController.class).to(%controller%.class);
    bind(%driverif%.class).to(%defaultdriver%.class);
}
""", Map("%errormodel%" -> errorModelName, "%model%" -> modelName,
    "%agent%" -> agentName, "%controller%" -> controllerName,
    "%appcontext%" -> contextName,
    "%driverif%" -> driverIf, "%defaultdriver%" -> defaultDriver))
/*
    jm_code("""
public DemoModule(Context context, DemoContext gcontext) {
    super(context, gcontext);
}

@Override
protected Class<? extends GContext> context_Class() {
    return null;
}

@Override
protected Class<? extends GErrorModel<?>> errormodel_Class() {
    return null;
}

@Override
protected Class<? extends GModel<?, ?>> model_Class() {
    return null;
}

@Override
protected Class<? extends GAgent<?, ?, ?>> agent_Class() {
    return null;
}

@Override
protected Class<? extends GController<?, ?, ?, ?>> controller_Class() {
    return null;
}""")
*/
  }
}
