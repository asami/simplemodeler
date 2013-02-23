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
 * @since   Feb. 22, 2013
 * @version Feb. 24, 2013
 * @author  ASAMI, Tomoharu
 */
class SquerylEntityScalaClassDefinition(
  pContext: PEntityContext,     
  aspects: Seq[ScalaAspect],
  pobject: PEntityEntity
) extends SquerylScalaClassDefinitionBase(pContext, aspects, pobject) {
  override protected def class_open_body_constructor {
//    val parents = parentAttributeDefinitions.filter(is_column).map(squeryl_param)
//    val mixins = traitsAttributeDefinitions.filter(is_column).map(squeryl_var)
//    val owns = attributeDefinitions.filter(is_column).map(squeryl_var)
//    sm_param_list(parents ++ mixins ++ owns)
    val parents = parentAttributeDefinitions.filter(is_column).map(_.attr.name)
    val owns = wholeAttributeDefinitions.filter(is_column).map(_squeryl_var_or_param(parents))
    sm_param_list(owns)
  }

  private def _squeryl_var_or_param(parents: Seq[String])(a: ScalaClassAttributeDefinition): String = {
    if (parents.contains(a.attr.name)) squeryl_param(a)
    else squeryl_var(a)
  }

  override protected def class_open_body_parent_constructor {
    val parents = parentAttributeDefinitions.filter(is_column).map(_.paramName)
    sm_param_list(parents)
  }
}
