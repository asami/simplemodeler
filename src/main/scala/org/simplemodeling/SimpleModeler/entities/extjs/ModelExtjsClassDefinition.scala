package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 14, 2012
 * @version Apr. 20, 2012
 * @author  ASAMI, Tomoharu
 */
class ModelExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  extjsobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ExtjsClassDefinition(context, aspects, extjsobject, maker) {
  baseName = "Ext.data.Model".some

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new ModelExtjsClassAttributeDefinition(context, aspects, attr, this, ejmaker)
  }

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

class ModelExtjsClassAttributeDefinition(
  pContext: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  attr: PAttribute,
  owner: ExtjsClassDefinition,
  maker: ExtjsTextMaker) extends ExtjsClassAttributeDefinition(pContext, aspects, attr, owner, maker) {

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    val tname = attr.attributeType(model_typename)
    jm_pln("{name: '%s', type: '%s'},", data_key, tname)
  }
}
