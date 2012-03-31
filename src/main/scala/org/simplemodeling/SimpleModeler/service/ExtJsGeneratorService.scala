package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.extjs.SimpleModel2ExtjsRealmTransformer

/*
 * @since   Mar. 31, 2012
 * @version Mar. 31, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val sm2Extjs = new SimpleModel2ExtjsRealmTransformer(simpleModel, aCall.serviceContext)
    val ExtjsRealm = sm2Extjs.transform
    aResponse.addRealm(ExtjsRealm)
  }
}

object ExtjsGeneratorService extends GServiceClass("extjs") {
  description.title = "Produces an Ext JS application."

  def new_Service(aCall: GServiceCall): GService =
    new ExtjsGeneratorService(aCall, this)
}
