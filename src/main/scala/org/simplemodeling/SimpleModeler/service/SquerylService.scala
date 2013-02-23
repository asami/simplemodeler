package org.simplemodeling.SimpleModeler.service

import scalaz._, Scalaz._
import com.asamioffice.goldenport.text.UString
import com.asamioffice.goldenport.text.UPathString
import org.goldenport.z._
import org.goldenport.Z._
import org.goldenport._
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entity.content.GContent
import org.goldenport.entities.workspace.TreeWorkspaceEntity
import org.goldenport.entities.fs.FileStoreEntity
import org.goldenport.entities.csv.CsvEntity
import org.goldenport.record._
import org.smartdox.Text
import org.simplemodeling.SimpleModeler.SimpleModelerConstants._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.generators.uml.ClassDiagramGenerator
import org.simplemodeling.SimpleModeler.importer.ScalaDslImporter
import org.simplemodeling.SimpleModeler.transformers.squeryl.SimpleModel2SquerylRealmTransformer
import org.simplemodeling.SimpleModeler.transformers.java.SimpleModel2Java6RealmTransformer
import org.simplemodeling.SimpleModeler.transformers.sql.SimpleModel2SqlRealmTransformer
import org.simplemodeling.SimpleModeler.transformers.extjs.SimpleModel2ExtjsRealmTransformer

/*
 * @since   Feb. 22, 2013
 * @version Feb. 23, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  val gendirscala = "src_managed/main/scala"

  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) {
    record_trace("SquerylService#execute_Service")
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    aResponse.addRealm(_squeryl(simpleModel))
  }

  private def _squeryl(simpleModel: SimpleModelEntity) = {
    val sm2sq = new SimpleModel2SquerylRealmTransformer(simpleModel, aCall.serviceContext)
    sm2sq.srcMainDir = gendirscala
    sm2sq.transform
  }
}

object SquerylService extends GServiceClass("squeryl") {
  description.title = "Produces a Squeryl application."

  def new_Service(aCall: GServiceCall): GService =
    new SquerylService(aCall, this)
}
