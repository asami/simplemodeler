package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 15, 2012
 * @version Apr. 15, 2012
 * @author  ASAMI, Tomoharu
 */
class ModelControllerExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  extjsobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ControllerExtjsClassDefinition(context, aspects, extjsobject, maker) {
  override protected def attribute_variables_Prologue {
    jm_pln("fields: [")
    jm_indent_up
  }

  override protected def attribute_variables_Epilogue {
    jm_indent_down
    jm_pln("],")
    jm_pln("validations: [")
    jm_indent_up
    for (a <- attributeDefinitions) {
      a.extjsValidation()
    }
    jm_indent_down
    jm_pln("],")
  }
}
