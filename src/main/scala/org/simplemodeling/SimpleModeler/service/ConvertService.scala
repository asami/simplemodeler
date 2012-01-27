package org.simplemodeling.SimpleModeler.service

import scala.collection.mutable.ArrayBuffer
import org.goldenport.Goldenport
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entities.xmind.XMindEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.converters.CsvXMindConverter
import com.asamioffice.goldenport.text.UPathString

/*
 * Feb.  3, 2009
 * Feb. 27, 2009
 */
class ConvertService(aCall: GServiceCall, serviceClass: GServiceClass) extends GService(aCall, serviceClass) {
  def execute_Service(aRequest: GServiceRequest, aResponse: GServiceResponse) {
//    println("convert = " + aRequest.entity) 2009-02-27
//    println("output = " + aRequest.parameter(Goldenport.Container_Output_Base))

    val projectName = UPathString.getLastComponentBody(aRequest.string)
    val format = aRequest.parameter("convert.format") match {
      case Some(f) => f.toString
      case None => "xmind"
    }
    val entity = aRequest.entity match {
      case csv: CsvEntity => {
	format match {
	  case "csv" => convert_csv_csv
	  case "xmind" => convert_csv_xmind(csv, projectName)
	  case _ => error("jump to usage.")
	}
      }
      case xmind: XMindEntity => {
	format match {
	  case "csv" => convert_xmind_csv
	  case "xmind" => convert_xmind_xmind
	  case _ => error("jump to usage.")
	}
      }
      case _ => error("jump usage error = " + aRequest.entity) // XXX
    }
    entity.name = projectName + ".xmind"
    aResponse.addEntity(entity)
  }

  private def convert_csv_csv: CsvEntity = {
    error("jump to usage.")
  }

  private def convert_csv_xmind(csv: CsvEntity, projectName: String): XMindEntity = {
    val csv2xmind = new CsvXMindConverter(csv, projectName)
    csv2xmind.toXMind
  }

  private def convert_xmind_csv: CsvEntity = {
    error("jump to usage.")
  }

  private def convert_xmind_xmind: XMindEntity = {
    error("jump to usage.")
  }
}

object ConvertService extends GServiceClass("convert") {
  def new_Service(aCall: GServiceCall): GService =
    new ConvertService(aCall, this)
}
