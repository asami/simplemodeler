package org.simplemodeling.SimpleModeler.service

import scala.collection.mutable.ArrayBuffer
import org.goldenport.Goldenport
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entities.xmind.XMindEntity
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import com.asamioffice.goldenport.text.{UString, UJavaString}
import org.simplemodeling.SimpleModeler.builder.Policy

/*
 * @since   Jan. 28, 2009
 *  version Feb. 27, 2009
 * @version Dec. 11, 2011
 * @author  ASAMI, Tomoharu
 */
class ImportService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) {
//    println("import = " + aRequest.entity) 2009-02-27
//    println("output = " + aRequest.parameter(Goldenport.Container_Output_Base))
    val projectRealm = new ProjectRealmEntity(entityContext)
    val packageName = aRequest.parameter("import.package") match {
      case Some(name) => name.toString
      case None => ""
    }
    val policy: Policy = Policy.create(entityContext, packageName)(
      aRequest.parameter("import.builder.policy"))
    projectRealm.open()
    aRequest.entity match {
      case csv: CsvEntity => _build_csv(projectRealm, packageName, csv, policy)
      case xmind: XMindEntity => _build_xmind(projectRealm, packageName, xmind, policy)
      case _ => error("jump usage error = " + aRequest.entity) // XXX
    }
    aResponse.addRealm(projectRealm)
  }

  private def _build_csv(project: ProjectRealmEntity, packageName: String, csv: CsvEntity, policy: Policy) {
    val builder = new CsvBuilder(project, packageName, csv, policy)
    builder.build
  }

  private def _build_xmind(project: ProjectRealmEntity, packageName: String, xmind: XMindEntity, policy: Policy) {
    val builder = new XMindBuilder(project, packageName, xmind, policy)
    builder.build
  }
}

object ImportService extends GServiceClass("import") {
  def new_Service(aCall: GServiceCall): GService =
    new ImportService(aCall, this)
}
