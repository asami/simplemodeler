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
 * @version Oct. 10, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidBaseColumnsJavaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity,
  maker: JavaMaker,
  val entity: SMDomainEntity
) extends JavaClassDefinition(pContext, aspects, pobject, maker) {
  useDocument = false
  isStatic = true
  customName = Some(pContext.className(entity.term))
  customImplementNames = List("BaseColumns")
  
  override protected def head_imports_Extension {
    jm_import("android.net.Uri")
    jm_import("android.provider.BaseColumns")
  }

  override protected def attribute_variables_extension {
    val pkgname = modelPackage.qualifiedName
    val cname = entity.term
    jm_public_static_final("Uri", "CONTENT_URI", """Uri.parse("content://" + AUTHORITY + "/%s")""", entity.term) 
    jm_public_static_final_String_literal("CONTENT_TYPE", "vnd.android.cursor.dir/vnd.%s.%s", pkgname, cname)
    jm_public_static_final_String_literal("CONTENT_ITEM_TYPE", "vnd.android.cursor.item/vnd.%s.%s", pkgname, cname)

    jm_public_static_final_String_literal("_ID", "_id")
    for (a <- entity.attributes) {
      jm_public_static_final_String_literal(pContext.constantName(a.name), a.name)
    }
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }
}
