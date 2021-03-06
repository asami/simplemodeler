package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.workspace.TreeWorkspaceEntity
import org.goldenport.GoldenportConstants
import org.simplemodeling.SimpleModeler.SimpleModelerConstants._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.generators.uml.ClassDiagramGenerator
import com.asamioffice.goldenport.text.UString
import com.asamioffice.goldenport.text.UPathString
import org.goldenport.record._
import org.smartdox.Text

/*
 * @since   Nov.  7, 2011
 *  version Dec.  6, 2011
 *  version Feb. 28, 2012
 *  version Jun. 17, 2012
 * @version Dec. 17, 2012
 * @author  ASAMI, Tomoharu
 */
class DiagramGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
    val pkgs = aRequest.parameterAsStrings("source.package")
    val pkgname = (pkgs match {
      case Nil => ""
      case _ => pkgs.head
    }) match {
      case "" => DEFAULT_PACKAGE_NAME
      case n => n
    }
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    simpleModel.open()
    val smpkg = {
      val xs = simpleModel.activePackages
      val a = xs.find(_.qualifiedName == pkgname) orElse xs.headOption
      a match {
        case Some(pkg) => pkg
        case None => {
          throw new IllegalArgumentException("モデルがありません。")
        }
      }
    }
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

object DiagramGeneratorService extends GServiceClass("diagram") with GoldenportConstants {
  description.title = "Produce diagrams (e.g. class diagram)."
  contract = Schema(
      Field("$", XBase64Binary, summary = "File name"),
      Field("source.package", XString, summary = "Package name"),
      Field(Container_Message, XString))

  def new_Service(aCall: GServiceCall): GService =
    new DiagramGeneratorService(aCall, this)
}
