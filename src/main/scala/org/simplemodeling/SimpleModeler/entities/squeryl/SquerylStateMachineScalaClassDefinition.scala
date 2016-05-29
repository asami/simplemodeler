package org.simplemodeling.SimpleModeler.entities.squeryl

import scalaz._, Scalaz._
import scala.xml.Elem
import org.apache.commons.lang3.StringUtils
import org.simplemodeling.SimpleModeler.builder.{NaturalLabel, VisibilityLabel}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entity.business._
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.simplemodel.SMMAction
import org.simplemodeling.dsl.datatype.XInt
import org.simplemodeling.dsl.SDatatype
import org.simplemodeling.dsl.SDatatypeFunction
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * @since   Oct. 17, 2015
 * @version May.  7, 2016
 * @author  ASAMI, Tomoharu
 */
class SquerylStateMachineScalaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  pobject: PStateMachineEntity
) extends SquerylScalaClassDefinitionBase(pContext, aspects, pobject) {
  scalaKind = SealedTraitScalaKind
  customBaseName = Some("Powertype") // XXX StateMachine in Record

  case class State(classname: String, statename: String, value: Int, label: String)

  private lazy val _states = for ((k, i) <- pobject.states.zipWithIndex) yield {
    val cn = k.name.capitalize + name
    val v = k.value match {
      case \/-(r) => r
      case _ => i + 1
    }
    State(cn, k.name, v, k.label)
  }

  override def companions {
    for (k <- _states) {
      sm_pln
      sm_case_object(k.classname, name) {
        sm_def_string("name", k.statename)
        sm_def_value("value", k.value)
        sm_override_def_string("label", k.label)
      }
    }
    sm_pln
    sm_object(name, "PowertypeClass") {
      sm_p("type T = ")
      sm_pln(name)
      sm_pln
      sm_val_vector_value("elements", _states.map(_.classname))
    }
  }
}
