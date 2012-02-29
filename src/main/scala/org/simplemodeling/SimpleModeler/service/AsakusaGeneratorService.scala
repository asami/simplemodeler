package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.workspace.TreeWorkspaceEntity
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity

/*
 * @since   Mar. 19, 2011
 * @version Feb. 28, 2012
 * @author  ASAMI, Tomoharu
 */
class AsakusaGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    val baseRealm = new TreeWorkspaceEntity(entityContext)
    aResponse.addRealm(baseRealm)
  }
}

object AsakusaGeneratorService extends GServiceClass("asakusa") {
  description.title = "(preliminary)"

  def new_Service(aCall: GServiceCall): GService =
    new AsakusaGeneratorService(aCall, this)
}
