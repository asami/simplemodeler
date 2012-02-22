package org.simplemodeling.SimpleModeler.service

import scala.collection.mutable.ArrayBuffer
import com.asamioffice.goldenport.text.UPathString
import org.goldenport.Goldenport
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entities.xmind.XMindEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.converters.CsvXMindConverter
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Feb.  3, 2009
 *  version Feb. 27, 2009
 * @version Feb. 21, 2012
 * @author  ASAMI, Tomoharu
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
    val packageNames: Seq[String] = aRequest.parameter("source.package") match {
      case Some(name) => name.asInstanceOf[AnyRef].toString.split(":").map(_.trim)
      case None       => Nil
    }
    val packageName = (if (packageNames.isEmpty) "" else packageNames.head) match { // XXX temporary fix for cloud service
      case "" => "model"
      case n => n    
    }
    val policy: Policy = Policy.create(entityContext, packageName)(
      aRequest.parameter("import.builder.policy"))

    val entity = aRequest.entity match {
      case csv: CsvEntity => {
        format match {
          case "csv" => convert_csv_csv
          case "xmind" => convert_csv_xmind(policy, packageName, csv, projectName)
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

  private def convert_csv_xmind(policy: Policy, packageName: String, csv: CsvEntity, projectName: String): XMindEntity = {
    val csv2xmind = new CsvXMindConverter(policy, packageName, csv, projectName)
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
