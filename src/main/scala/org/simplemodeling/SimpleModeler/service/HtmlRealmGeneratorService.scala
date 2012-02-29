package org.simplemodeling.SimpleModeler.service

import org.goldenport.service._
import org.goldenport.entity._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.transformers.specdoc.SimpleModel2SpecDocTransformer
import org.goldenport.entities.specdoc.SpecDocEntity
import org.goldenport.entities.specdoc.generators.SpecDoc2SmartDocRealmGenerator
import org.goldenport.entities.smartdoc.SmartDocRealmEntity
import org.goldenport.entities.smartdoc.generators.SmartDocRealm2HtmlRealmGenerator

/*
 * Oct. 27, 2008
 * Dec. 18, 2010
 * @version  Feb. 28, 2012
 */
class HtmlRealmGeneratorService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) = {
//println("HtmlRealm : before simpleModel") 2009-03-01
    val simpleModel = aRequest.entity.asInstanceOf[SimpleModelEntity]
//println("HtmlRealm : after simpleModel")
    val sm2SpecDoc = new SimpleModel2SpecDocTransformer(simpleModel)
//println("HtmlRealm : after specdoc")
    aRequest.parameterBoolean("html.diagram") match {
      case Some(value) => {
	record_trace("html.diagram = " + value)
	sm2SpecDoc.drawDiagram = value
      }
      case None => //
    }
//    sm2SpecDoc.drawDiagram = false
    val specDoc = sm2SpecDoc.transform
    val sd2SDoc = new SpecDoc2SmartDocRealmGenerator(specDoc)
    val sdoc = sd2SDoc.transform
    val sdoc2HtmlRealm = new SmartDocRealm2HtmlRealmGenerator(sdoc)
    val htmlRealm = sdoc2HtmlRealm.toHtmlRealm
    aResponse.addRealm(htmlRealm)
//    aResponse += htmlRealm
  }
}

object HtmlRealmGeneratorService extends GServiceClass("html") {
  description.title = "(preliminary)"

  def new_Service(aCall: GServiceCall): GService =
    new HtmlRealmGeneratorService(aCall, this)
}
