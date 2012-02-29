package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.workspace.TreeWorkspaceEntity
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.entities.project._

/*
 * @since   Jan. 28, 2009
 *  version Apr. 26, 2011
 * @version Feb. 28, 2012
 * @author  ASAMI, Tomoharu
 */
class ProjectRealmGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val projectName = aRequest.argument(0).toString
    val projectRealm = new ProjectRealmEntity(entityContext)
    projectRealm.projectName = projectName
    projectRealm.open()
    projectRealm.buildScaffold()
    projectRealm.buildDemo()
    val baseRealm = new TreeWorkspaceEntity(entityContext)
    baseRealm.open()
    baseRealm.copyIn(projectName, projectRealm)
    aResponse.addRealm(baseRealm)
  }
}

object ProjectRealmGeneratorService extends GServiceClass("project") {
  description.title = "Creates a project initial artifacts."

  def new_Service(aCall: GServiceCall): GService =
    new ProjectRealmGeneratorService(aCall, this)
}
