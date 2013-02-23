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

/**
 * @since   Feb. 23, 2013
 * @version Feb. 23, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylTraitScalaClassDefinition(
  pContext: PEntityContext,
  aspects: Seq[ScalaAspect],
  pobject: PTraitEntity
) extends SquerylScalaClassDefinitionBase(pContext, aspects, pobject) {
  scalaKind = TraitScalaKind

  override def attribute_variables {
    val owns = attributeDefinitions.filter(is_column).map(squeryl_abstract_def)
    for (a <- owns) {
      sm_pln(a)
    }
  }
}
