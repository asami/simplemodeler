package org.simplemodeling.SimpleModeler.service
import scalaz._
import Scalaz._
import com.asamioffice.goldenport.text.UString
import com.asamioffice.goldenport.text.UPathString
import org.goldenport.z._
import org.goldenport.Z._
import org.goldenport._
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entity.content.GContent
import org.goldenport.entities.workspace.TreeWorkspaceEntity
import org.goldenport.entities.fs.FileStoreEntity
import org.goldenport.entities.csv.CsvEntity
import org.goldenport.record._
import org.smartdox.Text
import org.simplemodeling.SimpleModeler.SimpleModelerConstants._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.generators.uml.ClassDiagramGenerator
import org.simplemodeling.SimpleModeler.importer.ScalaDslImporter
import org.simplemodeling.SimpleModeler.transformers.squeryl.SimpleModel2SquerylRealmTransformer
import org.simplemodeling.SimpleModeler.transformers.java.SimpleModel2Java6RealmTransformer
import org.simplemodeling.SimpleModeler.transformers.sql.SimpleModel2SqlRealmTransformer
import org.simplemodeling.SimpleModeler.transformers.extjs.SimpleModel2ExtjsRealmTransformer

/*
 * @since   Feb. 22, 2013
 * @version Feb. 22, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  val gendir = "target/scala-2.9.1/src_managed/main"
  val gendirjava = "src_managed/main/java"
  val gendirscala = "src_managed/main/scala"
  val gendirsql = "src_managed/main/sql"
  val gendirextjs = "src_managed/main/extjs"
//  val packageNickname: Option[String]  = string_option("squeryl.packageNickname")
  val packageNickname: Option[String]  = string_option("squeryl.packageNickname") orElse "Acm".some
  val restBaseUri: Option[String]  = string_option("squeryl.restBaseUri") orElse "acm/rest/".some

  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) {
    record_trace("SquerylService#execute_Service")
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
    aResponse.addRealm(_squeryl(simpleModel))
    aResponse.addRealm(_java(simpleModel))
    aResponse.addRealm(_sql(simpleModel))
    aResponse.addRealm(_extjs(simpleModel))
  }

  private def _squeryl(simpleModel: SimpleModelEntity) = {
    val sm2sv = new SimpleModel2SquerylRealmTransformer(simpleModel, aCall.serviceContext)
    sm2sv.srcMainDir = gendirscala
    sm2sv.transform
//    val svRealm = sm2sv.transform
//    println("SquerylService#execute_Service = " + svRealm.getNode("/app/config/Base.scala").map(_.content).collect { case x: org.goldenport.entity.content.EntityContent => x.entity })
//    aResponse.addRealm(svRealm)
  }

  private def _java(simpleModel: SimpleModelEntity) = {
    val sm2java = new SimpleModel2Java6RealmTransformer(simpleModel, aCall.serviceContext)
    sm2java.srcMainDir = gendirjava
    sm2java.isMaven = false
    sm2java.isSbt = false
    sm2java.transform
//    val javaRealm = sm2java.transform
//    aResponse.addRealm(javaRealm)
  }

  private def _sql(simpleModel: SimpleModelEntity) = {
    val sm2sql = new SimpleModel2SqlRealmTransformer(simpleModel, aCall.serviceContext)
    sm2sql.srcMainDir = gendirsql
    sm2sql.transform
  }

  private def _extjs(simpleModel: SimpleModelEntity) = {
    val sm2extjs = new SimpleModel2ExtjsRealmTransformer(simpleModel, aCall.serviceContext)
    sm2extjs.srcMainDir = gendirextjs
    sm2extjs.useProject = false
    sm2extjs.usePlay = false
    packageNickname.foreach(sm2extjs.setPackageNickname)
    restBaseUri.foreach(sm2extjs.setRestBaseUri)
    sm2extjs.transform
  }
}

object SquerylService extends GServiceClass("squeryl") {
  description.title = "Produces a Squeryl application."

  def new_Service(aCall: GServiceCall): GService =
    new SquerylService(aCall, this)
}
