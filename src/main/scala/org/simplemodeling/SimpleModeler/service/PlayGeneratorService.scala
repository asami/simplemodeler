package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.play.SimpleModel2PlayRealmTransformer

/*
 * @since   Mar. 31, 2012
 * @version Mar. 31, 2012
 * @author  ASAMI, Tomoharu
 */
class PlayGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val sm2play = new SimpleModel2PlayRealmTransformer(simpleModel, aCall.serviceContext)
    val playRealm = sm2play.transform
    aResponse.addRealm(playRealm)
  }
}

object PlayGeneratorService extends GServiceClass("play") {
  description.title = "Produces an play application."

  def new_Service(aCall: GServiceCall): GService =
    new PlayGeneratorService(aCall, this)
}
