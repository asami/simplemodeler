package org.simplemodeling.SimpleModeler.importer

import scala.collection.mutable.ArrayBuffer
import org.goldenport.importer._
import org.goldenport.service._
import org.goldenport.entity.GEntity
import org.simplemodeling.dsl.{SObject, SManifest}
import org.simplemodeling.SimpleModeler.entity._
import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entities.xmind.XMindEntity
import org.goldenport.entities.opml.OpmlEntity
import org.goldenport.entities.outline.OutlineEntityBase
import org.simplemodeling.SimpleModeler.builder.Policy
import org.simplemodeling.SimpleModeler.builder.DefaultPolicy
import com.asamioffice.goldenport.text.UPathString
import com.asamioffice.goldenport.text.UString

/*
 * @since   Oct. 31, 2008
 *  version Dec. 11, 2011
 * @version Mar. 31, 2012
 * @author  ASAMI, Tomoharu
 */
class ScalaDslImporter(aCall: GServiceCall) extends GImporter(aCall) {
  val packageNames: Seq[String] = request.parameter("source.package") match {
    case Some(name) => name.asInstanceOf[AnyRef].toString.split(":").map(_.trim)
    case None       => Nil
  }
  val packageName = (if (packageNames.isEmpty) "" else packageNames.head) match { // XXX temporary fix for cloud service
      case "" => "model"
      case n => n    
  }
  private val _builder_policy: Policy = Policy.create(entityContext, packageName)(
      request.parameter("import.builder.policy"))

  override def execute_Import() {
    val args = request.arguments.map(reconstitute_entity)
    if (!args.isEmpty) {
      request.setEntity(args(0).get)
      request.setEntities(args.map(_.get))
    }
    //println("import start") 2009-03-01
    val objects = _dsl_objects
    //println("after get_objects: " + objects)
    val ds = new SObjectDataSource(entityContext)(objects: _*)
    val sm = new SimpleModelEntity(ds, entityContext)
    for (o <- args.headOption; en <- o) {
      val s = UPathString.getLastComponentBody(en.name)
      if (UString.notNull(s)) {
        sm.name = s + ".sm"
      }
    }
    request.setEntity(sm)
    //println("import end")
  }

  private def _dsl_objects: List[SObject] = {
    _plain_dsl_objects ::: _entities_dsl_objects
  }
  
  private def _plain_dsl_objects: List[SObject] = {
    get_objects.toList
  }

  private def _entities_dsl_objects: List[SObject] = {
    if (request.isEntity) {
      val entities = List(request.entity) // request.entities.toList
      entities.flatMap {
        case csv: CsvEntity => _csv_dsl_objects(csv)
        case xmind: XMindEntity => _xmind_dsl_objects(xmind)
//        case orgmode: OrgmodeEntity => _orgmode_dsl_objects(orgmode)
        case outline: OutlineEntityBase => _outline_dsl_objects(outline)
        case _ => error("jump usage error = " + request.entity) // XXX
      }
    } else {
      Nil
    }
  }

  private def _csv_dsl_objects(csv: CsvEntity): List[SObject] = {
    val importer = new CsvImporter(_builder_policy, serviceCall, packageName, csv)
    importer.importDslObjects
  }

  private def _xmind_dsl_objects(xmind: XMindEntity): List[SObject] = {
    val importer = new XMindImporter(_builder_policy, serviceCall, packageName, xmind)
    importer.importDslObjects
  }

/*
  private def _orgmode_dsl_objects(orgmode: OrgmodeEntityBase): List[SObject] = {
    val importer = new OrgmodeImporter(_builder_policy, serviceCall, packageName, orgmode)
    importer.importDslObjects
  }
*/

  private def _outline_dsl_objects(outline: OutlineEntityBase): List[SObject] = {
    val importer = new OutlineImporter(_builder_policy, serviceCall, packageName, outline)
    importer.importDslObjects
  }

  // OLD
  private def get_objects: Seq[SObject] = {
    val objects = new ArrayBuffer[SObject]

    def collect_objects(aName: String) {
      val manifestName = get_manifest_name(aName)
      try {
        //      println("scaladsl manifest name = " + manifestName)
        record_debug("scaladsl manifest name = " + manifestName)
        val manifest = new_instance[SManifest](manifestName)
        //      println("scaladsl manifest = " + manifest)
        record_debug("scaladsl manifest = " + manifest)
        objects ++= manifest.objects
        val packageName = get_package_name(aName)
        manifest.packageNames.foreach(collect_objects_from_subpackage(_, packageName))
      } catch {
        case ex: ClassNotFoundException => {
          record_message("%sが見つかりません。パッケージ%sが誤っている可能性があります。", manifestName, aName)
        }
//        case ex: ClassNotFoundException => throw new IllegalArgumentException(
//          "%sが見つかりません。パッケージ%sが誤っている可能性があります。".format(manifestName, aName), ex)
      }
    }

    def collect_objects_from_subpackage(aChildName: String, aParentName: String) {
      collect_objects(aParentName + "." + aChildName)
    }

    packageNames.foreach(collect_objects)
    //    request.strings.foreach(collect_objects)
    objects
  }

  private def get_manifest_name(aName: String) = {
    if (aName.endsWith(".MANIFEST")) aName
    else aName + ".MANIFEST"
  }

  private def get_package_name(aName: String) = {
    if (!aName.endsWith(".MANIFEST")) aName
    else aName.substring(".MANIFEST".length)
  }
}

object ScalaDslImporter extends GImporterClass {
  type Instance_TYPE = ScalaDslImporter

  override protected def accept_Service_Call(aCall: GServiceCall): Option[Boolean] = {
    aCall.service.name match {
      case "html"    => Some(true)
      case "java"    => Some(true)
      case "grails"  => Some(true)
      case "gae"     => Some(true)
      case "gaeo"    => Some(true)
      case "gaej"    => Some(true)
      case "android" => Some(true)
      case "g3"      => Some(true)
      case "diagram" => Some(true)
      case "extjs" => Some(true)
      case "play" => Some(true)
      case _         => None
    }
  }

  override protected def new_Importer(aCall: GServiceCall): GImporter = {
    new ScalaDslImporter(aCall)
  }
}
