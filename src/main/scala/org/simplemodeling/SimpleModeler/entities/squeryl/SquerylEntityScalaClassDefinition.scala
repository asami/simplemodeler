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
 *  version Feb. 24, 2013
 * @version Aug. 11, 2014
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

  private lazy val whole_concrete_attribute_definitions = wholeAttributeDefinitions.filter(is_column)

  override protected def attribute_methods {
    _copy_constructor
    _to_record
  }

  private def _copy_constructor {
    val params = _copy_params(whole_concrete_attribute_definitions)
    val values = _copy_values(whole_concrete_attribute_definitions)
    sm_blockps("def copy%s", name) {
      sm_list(params)
    }
    sm_blockp(" = ") {
      sm_blockps("new %s", name) {
        sm_list(values)
      }
    }
  }

  private def _copy_params(xs: Seq[ScalaClassAttributeDefinition]): Seq[String] = {
    xs.map(x => """%s: %s = this.%s""".format(x.paramName, squeryl_type(x), x.attrName))
  }

  private def _copy_values(xs: Seq[ScalaClassAttributeDefinition]): Seq[String] = {
    xs.map(x => """%s""".format(x.paramName))
  }

  private def _to_record {
    def body = {
      val attrs = _record_params(whole_concrete_attribute_definitions)
      sm_blockps("Record.data") {
        sm_list(attrs)
      }
    }

    if (pobject.getBaseObject.isDefined)
      sm_override_def("toRecord: Record")(body)
    else
      sm_def("toRecord: Record")(body)
  }

  private def _record_params(xs: Seq[ScalaClassAttributeDefinition]): Seq[String] = {
    xs.map(x => """"%s" -> %s""".format(x.attrName, x.attrName))
  }

  override protected def companions {
    sm_object(name) {
      sm_def("create(rec: Record): %s", name) {
        val attrs = _entity_params(whole_concrete_attribute_definitions)
        sm_blockps("new %s", name) {
          sm_list(attrs)
        }
      }
    }
  }

  private def _entity_params(xs: Seq[ScalaClassAttributeDefinition]): Seq[String] = {
    xs.map(x => """rec.%s("%s")""".format(_getter(x), x.attrName))
  }

  private def _getter(attr: ScalaClassAttributeDefinition): String = {
    attr.attr.multiplicity match {
      case POne => _getter_one(attr)
      case PZeroOne => _getter_zeroone(attr)
      case POneMore => _getter_onemore(attr)
      case PZeroMore => _getter_zeromore(attr)
    }
  }

  private def _getter_one(attr: ScalaClassAttributeDefinition): String = {
    "as" + squeryl_object_type(attr.attr)
  }

  private def _getter_zeroone(attr: ScalaClassAttributeDefinition): String = {
    "get" + squeryl_object_type(attr.attr)
  }

  private def _getter_onemore(attr: ScalaClassAttributeDefinition): String = {
    throw new UnsupportedOperationException("_getter_onemore")
  }

  private def _getter_zeromore(attr: ScalaClassAttributeDefinition): String = {
    throw new UnsupportedOperationException("_getter_zeremore")
  }
}
