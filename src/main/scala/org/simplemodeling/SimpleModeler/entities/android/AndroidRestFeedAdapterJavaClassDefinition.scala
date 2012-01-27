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
 * @since   Aug. 13, 2011
 * @version Aug. 13, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidRestFeedAdapterJavaClassDefinition(
    pContext: PEntityContext,
    aspects: Seq[JavaAspect],
    pobject: PObjectEntity) extends JavaClassDefinition(pContext, aspects, pobject, null) {
  useDocument = false
  customBaseName = Some("FeedBeanSequenceAdapter")

  override protected def head_imports_Extension {
    jm_import("org.goldenport.android.GModel")
    jm_import("org.goldenport.android.feed.GAEntry")
    jm_import("org.goldenport.android.models.BeanSequenceRepository")
    jm_import("org.goldenport.android.widgets.FeedBeanSequenceAdapter")
    jm_import("android.content.Context")
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
    jm_public_constructor("%s(Context context, GModel<?, ?> model, BeanSequenceRepository<GAEntry> repo)", name) {
      jm_pln("super(context, model, repo);")
    }
  }
}
