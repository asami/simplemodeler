package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.java.SimpleModel2Java6RealmTransformer

/*
 * @since   Dec. 12, 2011
 * @version Feb. 28, 2012
 * @author  ASAMI, Tomoharu
 */
class JavaRealmGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel: SimpleModelEntity = aRequest.entityAs
    val sm2java = new SimpleModel2Java6RealmTransformer(simpleModel, aCall.serviceContext)
    sm2java.javaSrcDir = "/src/main/java"
    val androidRealm = sm2java.transform
    aResponse.addRealm(androidRealm)
  }
}

object JavaRealmGeneratorService extends GServiceClass("java") {
  description.title = "Produces a Java 6 application."

  def new_Service(aCall: GServiceCall): GService =
    new JavaRealmGeneratorService(aCall, this)
}
