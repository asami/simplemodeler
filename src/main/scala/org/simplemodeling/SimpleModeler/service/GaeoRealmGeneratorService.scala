package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.gaeo.SimpleModel2GaeoRealmTransformer

/*
 * @since   Mar. 11, 2009
 *  version Dec. 18, 2010
 * @version Feb. 28, 2012
 * @author  ASAMI, Tomoharu
 */
class GaeoRealmGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val sm2gaeo = new SimpleModel2GaeoRealmTransformer(simpleModel, aCall.serviceContext)
    val gaeRealm = sm2gaeo.toGaeoRealm
    aResponse.addRealm(gaeRealm)
  }
}

object GaeoRealmGeneratorService extends GServiceClass("gaeo") {
  description.title = "(obsolate)"

  def new_Service(aCall: GServiceCall): GService =
    new GaeoRealmGeneratorService(aCall, this)
}
