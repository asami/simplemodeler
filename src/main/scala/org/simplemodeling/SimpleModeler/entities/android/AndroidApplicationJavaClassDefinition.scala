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
 * @version Aug. 26, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidApplicationJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  customBaseName = Some("GApplication")

  override protected def head_imports_Extension {
    super.head_imports_Extension
    jm_import("org.goldenport.android.*")
    jm_import("com.google.inject.*")    
  }

  override protected def constructors_null_constructor = null
  override protected def constructors_copy_constructor = null

  override protected def object_auxiliary {
    jm_code("""
private %context% gcontext;

public %application%() {
    gcontext = new %context%();
    gcontext.setContext(this);
}

@Override
protected Module[] create_Modules() {
    return new Module[] { new %module%(gcontext) };
}

@Override
protected GFactory create_Factory(Module[] modules) {
    return new %factory%(modules);
}
""", Map("%context%" -> contextName, "%application%" -> name, "%module%" -> moduleName,
    "%factory%" -> factoryName))
  }
}
