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
 * @since   Jul. 19, 2011
 * @version Oct. 10, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidG3RestDriverJavaClassDefinition(
    pContext: PEntityContext,
    aspects: Seq[JavaAspect],
    val driverEntity: AndroidG3RestDriverEntity) extends JavaClassDefinition(pContext, aspects, driverEntity, null) {
  useDocument = false
  customBaseName = Some("RestDriverBase")
  customImplementNames = driverEntity.interfaceName.toList

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

  override protected def attribute_variables_extension {
    jm_private_instance_variable_single(contextName, "context");
    jm_private_instance_variable_single(factoryName, "factory");
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
    jm_inject
    jm_public_constructor("%s(%s context)", name, contextName) {
      jm_pln("super(context.getModuleName());")
      jm_assign_this("context", "context")
      jm_assign_this("factory", "context.getFactory()")
    }
  }

  override protected def package_methods_platform_Entity(entity: PEntityEntity) {
    val doc = pContext.entityDocumentName(entity)
    val entityname = entity.classNameBase
    val term = entity.asciiName
    val pathname = term
//    val pathname = term match { // XXX
//      case "DEEBuy" => "buy"
//      case "DEACustomer" => "customer"
//      case "DERGoods" => "goods"
//    }
    jm_public_method("%s get%s(String id) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_get_json(url);
return factory.create%document%(json);
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_method("%s get%s(long id) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_get_json(url);
return factory.create%document%(json);
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_method("GADocumentFeed<%s> query%s(int start, int count) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""Bundle params = new Bundle();
params.putInt("start", start);
params.putInt("count", count);
String url = make_rest_url_json("%path%", params);
JSONObject json = invoke_get_json(url);
return factory.createDocumentFeed%document%(get_array(json, get_content_key()));
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_method("GADocumentFeed<%s> query%s(Bundle selection) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""String url = make_rest_url_json("%path%", selection);
JSONObject json = invoke_get_json(url);
return factory.createDocumentFeed%document%(get_array(json, get_content_key()));
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_method("GADocumentFeed<%s> query%s(Bundle selection, String sortOrder) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""Bundle params = new Bundle(selection);
params.putString("order_by", sortOrder);
String url = make_rest_url_json("%path%", params);
JSONObject json = invoke_get_json(url);
return factory.createDocumentFeed%document%(get_array(json, get_content_key()));
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_void_method("post%s(%s doc) throws IOException", entityname, doc) {
      jm_code("""String url = make_rest_url_json("%path%");
JSONObject json = invoke_post_json(url, doc.toBundle());
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("put%s(%s doc) throws IOException", entityname, doc) {
      val attr = entity.idAttr
      val idname = attr.attributeType match {
        case v: PValueType => attr.name + ".value" 
        case _ => attr.name
      }
      jm_code("""String url = make_rest_url_json("%path%", doc.%id%);
JSONObject json = invoke_put_json(url, doc.toBundle());
""", Map("%path%" -> pathname,
         "%id%" -> idname))
    }
    jm_public_void_method("put%s(long id, Bundle props) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_put_json(url, props);
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("put%s(String id, Bundle props) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_put_json(url, props);
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("delete%s(String id) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_delete_json(url);
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("delete%s(long id) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_delete_json(url);
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("delete%s(Bundle selection) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", selection);
JSONObject json = invoke_delete_json(url);
""", Map("%path%" -> pathname))
    }
  }
/*
  override protected def package_methods_Entity(entity: SMDomainEntity) {
    if (!is_id(entity)) return;
    val doc = pContext.entityDocumentName(entity)
    val entityname = entity.term.capitalize
    val term = entity.term_en // XXX rest name
    val pathname = term
    val idname = get_id_name(entity);
    jm_public_method("%s get%s(String id) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_get_json(url);
return factory.create%document%(json);
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_method("%s get%s(long id) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_get_json(url);
return factory.create%document%(json);
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_method("GADocumentFeed<%s> query%s(Bundle selection) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""String url = make_rest_url_json("%path%", selection);
JSONObject json = invoke_get_json(url);
return factory.createDocumentFeed%document%(get_array(json, get_content_key()));
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_method("GADocumentFeed<%s> query%s(Bundle selection, String sortOrder) throws IOException", doc, entityname) {
      jm_try {
        jm_code("""Bundle params = new Bundle(selection);
params.putString("order_by", sortOrder);
String url = make_rest_url_json("%path%", params);
JSONObject json = invoke_get_json(url);
return factory.createDocumentFeed%document%(get_array(json, get_content_key()));
""", Map("%path%" -> pathname, "%document%" -> doc))
      }
      jm_catch_end("JSONException e") {
        jm_pln("throw new JsonIOException(e);")
      }
    }
    jm_public_void_method("post%s(%s doc) throws IOException", entityname, doc) {
      jm_code("""String url = make_rest_url_json("%path%");
JSONObject json = invoke_post_json(url, doc.toBundle());
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("put%s(%s doc) throws IOException", entityname, doc) {
//      xxx
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_put_json(url, doc.toBundle());
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("put%s(long id, Bundle props) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_put_json(url, props);
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("put%s(String id, Bundle props) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_put_json(url, props);
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("delete%s(String id) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_delete_json(url);
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("delete%s(long id) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", id);
JSONObject json = invoke_delete_json(url);
""", Map("%path%" -> pathname))
    }
    jm_public_void_method("delete%s(Bundle selection) throws IOException", entityname) {
      jm_code("""String url = make_rest_url_json("%path%", selection);
JSONObject json = invoke_delete_json(url);
""", Map("%path%" -> pathname))
    }
  }

  protected final def is_id(entity: SMDomainEntity) = {
    true
  }

  protected final def get_id_name(entity: SMDomainEntity) = {
    "OK"
  }
  */
}
