package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.g3.SimpleModel2G3RealmTransformer

/*
 * @since   Jun. 12, 2011
 * @version Feb. 28, 2012
 * @author  ASAMI, Tomoharu
 */
class G3GeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val sm2g3 = new SimpleModel2G3RealmTransformer(simpleModel, aCall.serviceContext)
    val g3Realm = sm2g3.transform
    aResponse.addRealm(g3Realm)
  }
}

object G3GeneratorService extends GServiceClass("g3") {
  description.title = "Produces a g3 application."

  def new_Service(aCall: GServiceCall): GService =
    new G3GeneratorService(aCall, this)
}
