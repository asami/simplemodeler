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
 * @since   Jul. 18, 2011
 * @version Aug.  7, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidContentContractJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends JavaClassDefinition(pContext, aspects, pobject) {
  useDocument = false

  override protected def head_imports_Extension {
    jm_import("android.net.Uri")
    jm_import("android.provider.BaseColumns")
  }

  override protected def attribute_variables_extension {
    jm_public_static_final_String_literal("AUTHORITY", modelPackage.qualifiedName)
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def object_auxiliary {
    traverse(new JavaMakerModelElementVisitor(jm_maker) {
      override protected def visit_Entity(entity: SMDomainEntity) {
        val klass = new AndroidBaseColumnsJavaClassDefinition(pContext, Nil, pobject, jm_maker, entity)
        klass.build
      }
    });
  }
}
