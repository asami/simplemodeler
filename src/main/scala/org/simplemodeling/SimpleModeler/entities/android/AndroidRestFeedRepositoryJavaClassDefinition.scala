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
class AndroidRestFeedRepositoryJavaClassDefinition(
    pContext: PEntityContext,
    aspects: Seq[JavaAspect],
    repEntity: AndroidRestFeedRepositoryEntity) extends JavaClassDefinition(pContext, aspects, repEntity, null) {
  useDocument = false
  customBaseName = Some("FeedRepository")
  val entityDocumentName: String = repEntity.entityDocumentName.get
  val driverIfName: String = repEntity.driverIfName.get
  val defaultDriverName = repEntity.defaultDriverName.get

  override protected def head_imports_Extension {
    jm_import("java.io.IOException")
    jm_import("java.util.List")
    jm_import("org.goldenport.android.feed.GADocumentFeed")
    jm_import("org.goldenport.android.feed.GAEntry")
    jm_import("org.goldenport.android.models.FeedRepository")
  }

  override protected def attribute_variables_extension {
    jm_private_instance_variable_single(driverIfName, "driver");
  }

  override protected def constructors_null_constructor {
  }

  override protected def constructors_copy_constructor {
  }

  override protected def constructors_plain_constructor {
    jm_public_constructor("%s(%s driver)", name, driverIfName) {
      jm_assign_this("driver", "driver")
    }
  }

  override protected def object_auxiliary {
    jm_code("""@Override
protected List<GAEntry> load_Data(int start, int count) throws IOException {
    GADocumentFeed<%document%> feed = driver.query%term%(start, count);
    return feed.entries;
}
""", Map("%document%" -> entityDocumentName, "%term%" -> pobject.classNameBase))
  }
}
