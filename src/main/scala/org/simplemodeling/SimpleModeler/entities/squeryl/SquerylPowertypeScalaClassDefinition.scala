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
 * @version Oct. 17, 2015
 * @author  ASAMI, Tomoharu
 */
class SquerylPowertypeScalaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  pobject: PPowertypeEntity
) extends SquerylScalaClassDefinitionBase(pContext, aspects, pobject) {
  scalaKind = SealedTraitScalaKind
  customBaseName = Some("Powertype")

  case class Kind(classname: String, kindname: String, value: Int, label: String)

  private lazy val _kinds = for ((k, i) <- pobject.kinds.zipWithIndex) yield {
    val cn = k.name.capitalize + name
    val v = k.value match {
      case Right(r) => r
      case _ => i + 1
    }
    Kind(cn, k.name, v, k.label)
  }

  override def companions {
    for (k <- _kinds) {
      sm_pln
      sm_case_object(k.classname, name) {
        sm_def_string("name", k.kindname)
        sm_def_value("value", k.value)
        sm_override_def_string("label", k.label)
      }
    }
    sm_pln
    sm_object(name, "PowertypeClass") {
      sm_p("type T = ")
      sm_pln(name)
      sm_pln
      sm_val_vector_value("elements", _kinds.map(_.classname))
    }
  }
}
