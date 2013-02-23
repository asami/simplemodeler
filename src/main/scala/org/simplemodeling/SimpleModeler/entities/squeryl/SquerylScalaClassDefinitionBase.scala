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
 * @version Feb. 24, 2013
 * @author  ASAMI, Tomoharu
 */
abstract class SquerylScalaClassDefinitionBase(
  pContext: PEntityContext,     
  aspects: Seq[ScalaAspect],
  pobject: PObjectEntity
) extends ScalaClassDefinition(pContext, aspects, pobject) {
  isImmutable = true

  override protected def head_imports_Extension {
    sm_import("java.util.Date")
    sm_import("java.sql.Timestamp")
  }

  protected def is_column(a: ScalaClassAttributeDefinition): Boolean = {
    !(a.isInject || a.isDerive || a.isMulti || a.attr.isParticipation)
  }

  protected def squeryl_abstract_def(a: ScalaClassAttributeDefinition): String = {
    "def %s: %s".format(squeryl_var_name(a), squeryl_type(a))
  }

  protected def squeryl_var_type(a: ScalaClassAttributeDefinition): String = {
    "%s: %s".format(squeryl_var_name(a), squeryl_type(a))
  }

  protected def squeryl_param(a: ScalaClassAttributeDefinition): String = {
    if (is_column(a)) {
      squeryl_var_type(a)
    } else {
      "// " + squeryl_var_type(a)
    }
  }

  protected def squeryl_var(a: ScalaClassAttributeDefinition): String = {
    if (is_column(a)) {
      class_open_body_constructor_var_keyword + " " + squeryl_var_type(a)
    } else {
      "// " + class_open_body_constructor_var_keyword + " " + squeryl_var_type(a)
    }
  }

  protected def squeryl_var_name(a: ScalaClassAttributeDefinition): String = {
    pContext.sqlColumnName(a.attr)
  }

  protected def squeryl_type(a: ScalaClassAttributeDefinition): String = {
    val attr = a.attr
    attr.multiplicity match {
      case POne => _squery_type(attr)
      case PZeroOne => "Option[" + _squery_type(attr) + "]"
      case POneMore => "Seq[" + _squery_type(attr) + "]"
      case PZeroMore => "Seq[" + _squery_type(attr) + "]"
    }
  }

  private def _squery_type(a: PAttribute): String = {
    a.attributeType match {
      case x: PIntType => "Int"
      case x: PDateTimeType => "Timestamp"
      case x: PLinkType => "String"
      case x: PObjectReferenceType => "String" // XXX Long
      case x: PPowertypeType => "Int" // XXX String
      case x: PStateMachineType => "Int" // XXX String
      case x: PEntityType => "String" // XXX Long
      case _ => a.jpaElementTypeName
    }
  }

  override def attribute_variables {
    // Nop
  }

  override def attribute_methods {
    // Nop
  }
}
