package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.gaej.SimpleModel2GaeJavaRealmTransformer

/*
 * @since   Apr.  9, 2009
 * @version Dec. 18, 2010
 * @author  ASAMI, Tomoharu
 */
class GaeJavaRealmGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val sm2gaej = new SimpleModel2GaeJavaRealmTransformer(simpleModel, aCall.serviceContext)
    val gaeRealm = sm2gaej.toGaeJavaRealm
    aResponse.addRealm(gaeRealm)
  }
}

object GaeJavaRealmGeneratorService extends GServiceClass("gaej") {
  def new_Service(aCall: GServiceCall): GService =
    new GaeJavaRealmGeneratorService(aCall, this)
}
