package org.simplemodeling.SimpleModeler.builder

import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entity._
import org.goldenport.value.util.AnnotatedCsvTabular
import org.simplemodeling.SimpleModeler.builder.SimpleModelDslBuilder
import org.simplemodeling.SimpleModeler.builder.SimpleModelMakerBuilder
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.values.smcsv.SimpleModelCsvTabular
import org.simplemodeling.dsl.SObject
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.builder.Policy
import org.goldenport.service.GServiceCall
import org.simplemodeling.SimpleModeler.builder.SimpleModelBuilder

/*
 * Derived from CsvBuilder and CsvImporter
 * 
 * @since   Dec. 11, 2011
 *  version Dec. 11, 2011
 * @version Jan. 24, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class CsvBuilderBase(val policy: Policy, val packageName: String, val csv: CsvEntity) {
  val model_Builder: SimpleModelBuilder

  protected final def build_model {
    csv.open()
    val tabular = new SimpleModelCsvTabular(csv) // new AnnotatedCsvTabular(csv)
    tabular.build
    //    tabular.dump
    csv.close()
    val packagePathname = UJavaString.packageName2pathname(packageName)
//    for (y <- 0 until tabular.height if (tabular.annotations.get(0, y).key != "memo")) {
    for (y <- 0 until tabular.height) {
      val name = tabular.get(0, y)
      val obj = tabular.annotations.get(0, y).key match {
        case "actor"    => model_Builder.createObject(ActorKind, name)
        case "resource" => model_Builder.createObject(ResourceKind, name)
        case "event"    => model_Builder.createObject(EventKind, name)
        case "role"     => model_Builder.createObject(RoleKind, name)
        case "rule"     => model_Builder.createObject(RuleKind, name)
        case "usecase"  => model_Builder.createObject(UsecaseKind, name)
        case kind       => model_Builder.createObject(kind, name)
      }
      var is_derived = false
      for (x <- 1 until tabular.width) {
        val annotation = tabular.annotations.get(x, y)
        for (value <- tabular.getOption(x, y) if value != "") {
          if (annotation != null) {
            annotation.key match {
              case "name"        => error("not implemented yet.")
              case "name_ja"     => obj.name_ja = value
              case "name_en"     => obj.name_en = value
              case "term_ja"     => obj.term_ja = value
              case "term_en"     => obj.term_en = value
              case "caption"     => obj.caption = value
              case "brief"       => obj.brief = value
              case "summary"     => obj.summary = value
              case "description" => obj.description = value
              case "tableName"   => obj.tableName = value
              case "attrs"       => value.split("[;, ]+").foreach(obj.addNarrativeAttribute)
              case "parts"       => value.split("[;, ]+").foreach(obj.addNarrativePart)
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
        model_Builder.makeAttributesForDerivedObject(obj)
      } else {
        model_Builder.makeAttributesForBaseObject(obj)
      }
//      model_Builder.makeNarrativeAttributes(obj)
    }
  }

  def importDslObjects: List[SObject] = { // XXX
    build_model
    model_Builder.dslObjects
  }

  private def make_items(value: String) = {
    com.asamioffice.goldenport.text.CsvUtility.makeItems(value)
  }
}
