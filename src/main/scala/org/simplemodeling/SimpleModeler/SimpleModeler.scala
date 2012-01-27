package org.simplemodeling.SimpleModeler

import org.goldenport._
import org.goldenport.application.GApplicationDescriptor
import org.goldenport.service._
import org.goldenport.entity._
import org.goldenport.entities.csv._
import org.goldenport.entities.xmind._
import org.simplemodeling.SimpleModeler._
import org.simplemodeling.SimpleModeler.service._
import org.simplemodeling.SimpleModeler.importer._
import content.BinaryContent
import org.goldenport.exporter.FirstLeafResultExporterClass
import org.goldenport.entities.opml.OpmlEntity
import org.goldenport.entities.yaml.YamlEntity
import org.goldenport.entities.orgmode.OrgmodeEntity
import org.goldenport.entities.excel.ExcelTableEntity
import org.goldenport.exporter.FirstLeafOrZipResultExporterClass

/*
 * @since   Apr. 13, 2009
 *  version Dec. 13, 2011
 * @version Jan. 27, 2012
 * @auther  ASAMI, Tomoharu
 */
class SimpleModeler(args: Array[String]) {
  val goldenport = new Goldenport(args, SimpleModelerDescriptor)

  def useFirstLeafExporter() {
    goldenport.addExporterClass(FirstLeafResultExporterClass)
  }

  final def executeShellCommand(args: Array[String]) {
    goldenport.open()
    goldenport.executeShellCommand(args)
    goldenport.close()
  }

  def generateDiagram(name: String, data: Array[Byte]): Option[BinaryContent] = {
    goldenport.open()
    try {
      val ds = goldenport.createDataSource(name, data)
      val result = goldenport.execute("diagram", List(ds), Map("source.package" -> "sm"))
      result collect {
        case b: BinaryContent => b
      }
    } finally {
      goldenport.close()
    }
  }
}

object SimpleModeler {
  def main(args: Array[String]) {
    val simpleModeler = new SimpleModeler(args)
    simpleModeler.executeShellCommand(args)
  }
}

class AppMain extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    val args = config.arguments
    val smartdox = new SimpleModeler(args)
    smartdox.executeShellCommand(args)
    new xsbti.Exit {
      val code = 0
    }
  }    
}

object Main {
  def main(args: Array[String]) {
    val simpleModeler = new SimpleModeler(args)
    simpleModeler.executeShellCommand(args)
  }
}

// TODO: append custom service feature
class SimpleModelerDescriptor extends GApplicationDescriptor {
  name = "SimpleModeler"
  version = "0.3.1" // SNAPSHOT
  version_build = "20120127"
  copyright_years = "2008-2012"
  copyright_owner = "ASAMI, Tomoharu"
  command_name = "sm"
  //
  classpath("target/classes")
  importers(ScalaDslImporter)
  entities(CsvEntity, XMindEntity, OpmlEntity, ExcelTableEntity, 
      OrgmodeEntity, YamlEntity)
  services(ProjectRealmGeneratorService,
	   ImportService,
	   ConvertService,
	   HtmlRealmGeneratorService,
	   JavaRealmGeneratorService,
	   GrailsRealmGeneratorService,
	   GaeRealmGeneratorService,
	   GaeoRealmGeneratorService,
	   GaeJavaRealmGeneratorService,
	   AndroidGeneratorService,
	   G3GeneratorService,
	   AsakusaGeneratorService,
	   DiagramGeneratorService)
}

object SimpleModelerDescriptor extends SimpleModelerDescriptor
