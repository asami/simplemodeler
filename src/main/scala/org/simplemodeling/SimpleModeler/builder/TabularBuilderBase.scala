package org.simplemodeling.SimpleModeler.builder

import org.goldenport.entities.csv.CsvEntity
import org.goldenport.entity._
import org.goldenport.value.util.AnnotatedCsvTabular
import org.simplemodeling.SimpleModeler.entities.project.ProjectRealmEntity
import org.simplemodeling.SimpleModeler.entities.simplemodel._
import org.simplemodeling.SimpleModeler.entity.SimpleModelEntity
import org.simplemodeling.SimpleModeler.values.smcsv.SimpleModelCsvTabular
import org.simplemodeling.dsl.SObject
import com.asamioffice.goldenport.text.UJavaString
import org.goldenport.service.GServiceCall

/*
 * TODO NameLabel for CSV.
 * 
 * (Derived from CsvBuilder and CsvImporter.)
 * Derived from CsvBuilderBase.
 * 
 * @since   Dec. 11, 2011
 *  since   Jan. 24, 2012
 *  version Mar. 17, 2012
 * @version Oct. 19, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class TabularBuilderBase(val policy: Policy, val packageName: String) {
  val model_Builder: SimpleModelBuilder

  protected def build_model {
    build_Model
  }

  protected def build_Model: Unit

  /**
   * OutlineBuilderBase does not use the method as TableSimpleModelMakerBuilder at the moment.
   */
  protected final def build_model(tabular: AnnotatedCsvTabular) {
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
/*
      if (is_derived) {
        model_Builder.makeAttributesForDerivedObject(obj)
      } else {
        model_Builder.makeAttributesForBaseObject(obj)
      }
*/
//      model_Builder.makeNarrativeAttributes(obj)
    }
  }

  def importDslObjects: List[SObject] = { // XXX
    build_Model
    model_Builder.dslObjects
  }

  protected final def make_items(value: String) = {
    com.asamioffice.goldenport.text.CsvUtility.makeItems(value)
  }
}
