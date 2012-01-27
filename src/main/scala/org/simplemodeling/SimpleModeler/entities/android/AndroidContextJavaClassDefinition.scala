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
 * @version Aug.  7, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidContextJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends ContextJavaClassDefinition(pContext, aspects, pobject) {
  customBaseName = Some("GContext")

  override protected def head_imports_Extension {
    super.head_imports_Extension
    jm_import("android.content.*")
    jm_import("org.goldenport.android.GContext")
    jm_import("org.goldenport.android.feed.*")
  }
}
