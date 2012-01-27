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
 * @since   Jul. 30, 2011
 * @version Jul. 30, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidBuilderJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity,
  maker: JavaMaker
) extends BuilderJavaClassDefinition(pContext, aspects, pobject, maker) {
  /*
   * update
   */
  override protected def update_methods_entry {
    jm_public_method("void update_entry(String entry)") {
    }
  }

  override protected def update_methods_Extension {
    jm_public_method("void update_bound(Bundle bundle)") {
    }

    jm_public_method("void update_content_value(ContentValues values)") {
    }
  }
}
