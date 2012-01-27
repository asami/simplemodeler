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
 *  version Aug. 26, 2011
 * @version Dec. 13, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidFactoryJavaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[JavaAspect],
  pobject: PObjectEntity
) extends FactoryJavaClassDefinition(pContext, aspects, pobject) {
  customBaseName = Some("GFactory")

  override protected def head_imports_Extension {
    super.head_imports_Extension
    jm_import("android.content.*")
    jm_import("org.goldenport.android.*")
    jm_import("org.goldenport.android.feed.*")
  }

  override protected def attribute_variables_extension {
    jm_code("""
private static %factory% factory;
private %context% context;
""", Map("%context%" -> contextName,
         "%factory%" -> factoryName))
  }

  override protected def constructors_null_constructor = null
  override protected def constructors_copy_constructor = null

  override protected def constructors_plain_constructor {
    val contextname = pContext.contextName(pobject)
    val modulename = pContext.moduleName(pobject)
    val factoryname = pContext.factoryName(pobject)
    jm_public_constructor("%s(Module... modules)", factoryname) {
      jm_pln("super(modules);")
      jm_assign_this("context", "injector.getInstance(%s.class)", contextName)
      jm_pln("context.setFactory(this);")
      jm_pln("factory = this;")
    }
  }

  override protected def package_methods_Entity(entity: SMDomainEntity) {
    super.package_methods_Entity(entity)
    val doc = pContext.entityDocumentName(entity)
    val builder = doc + ".Builder"
    val feed = "GADocumentFeed<%s>".format(doc)
    val feedbuilder = "GADocumentFeedBuilder<%s>".format(doc)
    jm_public_method("%s create%s(JSONObject json) throws JSONException", doc, doc) {
      jm_var(builder, "builder", "(%s)injector.getInstance(%s.class)", builder, builder)
      jm_return("builder.with_json(json).build()")
    }
    jm_public_method("%s createDocumentFeed%s(JSONObject json) throws JSONException", feed, doc) {
      jm_return("new %s().withDocuments(create%s(json)).build()", feedbuilder, doc)
    }
    jm_public_method("%s createDocumentFeed%s(JSONArray json)throws JSONException", feed, doc) {
      jm_var_List_new_ArrayList("list", doc)
      jm_var("int", "count", "json.length()")
      jm_for("int i = 0;i < count;i++") {
        jm_pln("list.add(create%s(json.getJSONObject(i)));", doc)
      }
      jm_return("new %s().withDocuments(list).build()", feedbuilder)
    }
  }
}

