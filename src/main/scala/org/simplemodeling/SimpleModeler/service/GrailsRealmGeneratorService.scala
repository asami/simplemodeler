package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.grails.SimpleModel2GrailsRealmTransformer

/*
 * Jan. 25, 2009
 * Dec. 18, 2010
 */
class GrailsRealmGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val sm2grails = new SimpleModel2GrailsRealmTransformer(simpleModel)
    val grailsRealm = sm2grails.toGrailsRealm
    aResponse.addRealm(grailsRealm)
  }
}

object GrailsRealmGeneratorService extends GServiceClass("grails") {
  def new_Service(aCall: GServiceCall): GService =
    new GrailsRealmGeneratorService(aCall, this)
}
