package org.simplemodeling.SimpleModeler.transformers

import _root_.java.io.File
import scala.collection.mutable.{ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.requirement._
import org.simplemodeling.SimpleModeler.entities._
import org.goldenport.value._
import org.goldenport.service.GServiceContext
import org.goldenport.entity._
import org.goldenport.entity.content.{GContent, EntityContent}
import org.goldenport.entities.fs.FileStoreEntity
import org.simplemodeling.dsl._
import org.simplemodeling.dsl.domain._
import org.simplemodeling.dsl.util.UDsl
import com.asamioffice.goldenport.text.{UJavaString, UString}
import com.asamioffice.goldenport.io.UIO
import com.asamioffice.goldenport.util.MultiValueMap
import org.goldenport.recorder.Recordable

/*
 * @since   Nov. 19, 2012
 * @version Nov. 19, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SimpleModel2ScalaRealmTransformerBase(
  simpleModel: SimpleModelEntity, serviceContext: GServiceContext
) extends SimpleModel2ProgramRealmTransformerBase(simpleModel, serviceContext) {
  srcMainDir = "/src"
  usePackageObject = false
  val defaultFileSuffix = "scala"

  override protected def make_Builder() = {
    new ScalaBuilder 
  }

  override protected def make_Resolver() = {
    Some(new ScalaResolve)
  }

  override protected def make_Phases(): List[TransformerPhase] = {
    List(new ScalaMakeCrud)
  }

  class ScalaBuilder extends BuilderBase {
  }

  class ScalaResolve extends ResolvePhase {
  }

  class ScalaMakeCrud extends CrudMakePhase {
  }
}
