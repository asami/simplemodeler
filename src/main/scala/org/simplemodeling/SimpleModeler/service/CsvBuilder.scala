package org.simplemodeling.SimpleModeler.service

import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entity._
import org.goldenport.recorder.Recordable
import org.goldenport.value.util.AnnotatedCsvTabular
import org.simplemodeling.SimpleModeler.builder.CsvBuilderBase
import org.simplemodeling.SimpleModeler.builder.Policy
import org.simplemodeling.SimpleModeler.builder.SimpleModelMakerBuilder
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.values.smcsv.SimpleModelCsvTabular

import com.asamioffice.goldenport.text.UJavaString

/*
 * @since   Feb.  1, 2009
 *  version Feb. 28, 2009
 * @version Dec. 11, 2011
 * @author  ASAMI, Tomoharu
 */
class CsvBuilder(val project: ProjectRealmEntity, packageName: String, csv: CsvEntity,
    policy: Policy) extends CsvBuilderBase(policy, packageName, csv) {

  val entityContext = project.entityContext
  val simplemodel = new SimpleModelMakerEntity(entityContext, policy)
  val model_Builder = new SimpleModelMakerBuilder(simplemodel, packageName, policy)

  def build {
    simplemodel.using {
      build_model
      simplemodel.build
      project.copyIn("src/main/scala", simplemodel)
    }
  }
}

class CsvBuilder0(val project: ProjectRealmEntity, val packageName: String, val csv: CsvEntity,
    val policy: Policy) extends Recordable {
  final def build {
    val entityContext = project.entityContext
    val simplemodel = new SimpleModelMakerEntity(entityContext, policy)
    val builder = new SimpleModelMakerBuilder(simplemodel, packageName, policy)
    csv.open()
    val tabular = new SimpleModelCsvTabular(csv) // new AnnotatedCsvTabular(csv)
    tabular.build
//    tabular.dump
    csv.close()
    val packagePathname = UJavaString.packageName2pathname(packageName)

    simplemodel.open()
    for (y <- 0 until tabular.height if (tabular.annotations.get(0, y).key != "memo")) {
      val name = tabular.get(0, y)
      val obj = tabular.annotations.get(0, y).key match {
        case "actor" => builder.createObject(ActorKind, name)
        case "resource" => builder.createObject(ResourceKind, name)
        case "event" => builder.createObject(EventKind, name)
        case "role" => builder.createObject(RoleKind, name)
        case "rule" => builder.createObject(RuleKind, name)
        case "usecase" => builder.createObject(UsecaseKind, name)
	case _ => error("???")
      }
      var is_derived = false
      for (x <- 1 until tabular.width) {
	val annotation = tabular.annotations.get(x, y)
	for (value <- tabular.getOption(x, y) if value != "") {
          if (annotation != null) {
	    annotation.key match {
	      case "name" => error("not implemented yet.")
	      case "name_ja" => obj.name_ja = value
	      case "name_en" => obj.name_en = value
	      case "term_ja" => obj.term_ja = value
	      case "term_en" => obj.term_en = value
	      case "caption" => obj.caption = value
	      case "brief" => obj.brief = value
	      case "summary" => obj.summary = value
	      case "description" => obj.description = value
	      case "tableName" => obj.tableName = value
	      case "attrs" => value.split("[;, ]+").foreach(obj.addNarrativeAttribute)
	      case "parts" => value.split("[;, ]+").foreach(obj.addNarrativePart)
	      case "base" => {
	        obj.setNarrativeBase(value)
	        is_derived = true
	      }
	      case "powers" => {
	        make_items(value).foreach(obj.addNarrativePowertype)
	      }
	      case "states" => {
	        make_items(value).foreach(obj.addNarrativeStateMachine)
	      }
	      case "roles" => value.split("[;, ]+").foreach(obj.addNarrativeRole)
  	    }
          }
	}
      }
      if (is_derived) {
	builder.makeAttributesForDerivedObject(obj)
      } else {
	builder.makeAttributesForBaseObject(obj)
      }
    }
    simplemodel.build
    project.copyIn("src/main/scala", simplemodel)
    simplemodel.close()
  }

  private def make_items(value: String) = {
    com.asamioffice.goldenport.text.CsvUtility.makeItems(value)
  }
}
