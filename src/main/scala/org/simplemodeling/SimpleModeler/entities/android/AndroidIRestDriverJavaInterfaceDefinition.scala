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
 * Created at Tomamu
 * 
 * @since   Sep.  1, 2011
 * @version Sep. 25, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidIRestDriverJavaInterfaceDefinition(
    pContext: PEntityContext,
    aspects: Seq[JavaAspect],
    val driverEntity: AndroidIRestDriverEntity) extends JavaInterfaceDefinition(pContext, aspects, driverEntity, null) {
  override protected def head_imports_Extension {
    jm_import("java.io.IOException")
    jm_import("java.util.*")
    jm_import("com.google.inject.Inject")
    jm_import("org.json.JSONObject")
    jm_import("org.json.JSONException")
    jm_import("android.os.Bundle")
    jm_import("org.goldenport.android.*")
    jm_import("org.goldenport.android.feed.*")
    jm_import("org.goldenport.android.net.RestDriverBase")
    jm_import("org.goldenport.android.util.JsonIOException")
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
  }

  override protected def lifecycle_methods_open_method {
    jm_interface_method("void open(String server)")
  }

  override protected def lifecycle_methods_close_method {  
    jm_interface_method("void close()")
  }

  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    val doc = pContext.entityDocumentName(entity)
    val entityname = entity.classNameBase
    val term = entity.asciiName
    val pathname = term
    jm_interface_method("%s get%s(String id) throws IOException", doc, entityname)
    jm_interface_method("%s get%s(long id) throws IOException", doc, entityname)
    jm_interface_method("GADocumentFeed<%s> query%s(int start, int count) throws IOException", doc, entityname)
    jm_interface_method("GADocumentFeed<%s> query%s(Bundle selection) throws IOException", doc, entityname)
    jm_interface_method("GADocumentFeed<%s> query%s(Bundle selection, String sortOrder) throws IOException", doc, entityname)
    jm_interface_void_method("post%s(%s doc) throws IOException", entityname, doc)
    jm_interface_void_method("put%s(%s doc) throws IOException", entityname, doc)
    jm_interface_void_method("put%s(long id, Bundle props) throws IOException", entityname)
    jm_interface_void_method("put%s(String id, Bundle props) throws IOException", entityname)
    jm_interface_void_method("delete%s(String id) throws IOException", entityname)
    jm_interface_void_method("delete%s(long id) throws IOException", entityname)
    jm_interface_void_method("delete%s(Bundle selection) throws IOException", entityname)
  }
}
