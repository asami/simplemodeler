package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.workspace.TreeWorkspaceEntity
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.generators.uml.ClassDiagramGenerator
import com.asamioffice.goldenport.text.UString
import com.asamioffice.goldenport.text.UPathString

/*
 * @since   Nov.  7, 2011
 *  version Dec.  6, 2011
 * @version Jan. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class DiagramGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val pkgs = aRequest.parameterAsStrings("source.package")
    val pkgname = (pkgs match {
      case Nil => ""
      case _ => pkgs.head
    }) match { // XXX temporary fix for cloud service
      case "" => "model"
      case n => n
    }
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    simpleModel.open()
    val smpkg = simpleModel.activePackages.filter(_.qualifiedName == pkgname).head
    val baseRealm = new TreeWorkspaceEntity(entityContext)
    baseRealm.open()
    val generator = new ClassDiagramGenerator(simpleModel)
    val binary = generator.makeClassDiagramPng(smpkg, "detail")
    val filename = if (UString.isNull(simpleModel.name)) "overview.png"
    else UPathString.getLastComponentBody(simpleModel.name) + ".png"
    baseRealm.setContent(filename, binary)
    aResponse.addRealm(baseRealm)
  }
}

object DiagramGeneratorService extends GServiceClass("diagram") {
  def new_Service(aCall: GServiceCall): GService =
    new DiagramGeneratorService(aCall, this)
}
