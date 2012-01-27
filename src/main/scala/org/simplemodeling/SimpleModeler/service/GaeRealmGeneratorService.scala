package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.gae.SimpleModel2GaeRealmTransformer

/*
 * Mar.  8, 2009
 * Dec. 18, 2010
 */
class GaeRealmGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val sm2gae = new SimpleModel2GaeRealmTransformer(simpleModel, aCall.serviceContext)
    val gaeRealm = sm2gae.toGaeRealm
    aResponse.addRealm(gaeRealm)
  }
}

object GaeRealmGeneratorService extends GServiceClass("gae") {
  def new_Service(aCall: GServiceCall): GService =
    new GaeRealmGeneratorService(aCall, this)
}
