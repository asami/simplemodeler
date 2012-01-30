package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.workspace.TreeWorkspaceEntity
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.generators.uml.ClassDiagramGenerator
import com.asamioffice.goldenport.text.UString
import com.asamioffice.goldenport.text.UPathString
import org.goldenport.entities.fs.FileStoreEntity

/*
 * @since   Jan. 29, 2012
 * @version Jan. 30, 2012
 * @author  ASAMI, Tomoharu
 */
class BuildService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val pkgs = aRequest.parameterAsStrings("source.package")
    val pkgname = (pkgs match {
      case Nil => ""
      case _ => pkgs.head
    }) match { // XXX temporary fix for cloud service
      case "" => "model"
      case n => n
    }
    val src = aRequest.entity.asInstanceOf[FileStoreEntity]
    src.open()
    val dest = new TreeWorkspaceEntity(entityContext)
    dest.open()
    val home = src.getNode("/src/main/simplemodel")
    home.foreach(_build(_, dest))
    aResponse.addRealm(dest)
  }

  private def _build(home: GContainerEntityNode, dest: GTreeContainerEntity) {
    
  }
}

object BuildService extends GServiceClass("build") {
  def new_Service(aCall: GServiceCall): GService =
    new BuildService(aCall, this)
}
