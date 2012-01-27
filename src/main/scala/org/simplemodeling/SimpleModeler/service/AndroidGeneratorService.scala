package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.android.SimpleModel2AndroidRealmTransformer

/*
 * @since   Apr. 13, 2011
 * @version Apr. 25, 2011
 * @author  ASAMI, Tomoharu
 */
class AndroidGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val sm2android = new SimpleModel2AndroidRealmTransformer(simpleModel, aCall.serviceContext)
    val androidRealm = sm2android.transform
    aResponse.addRealm(androidRealm)
  }
}

object AndroidGeneratorService extends GServiceClass("android") {
  def new_Service(aCall: GServiceCall): GService =
    new AndroidGeneratorService(aCall, this)
}
