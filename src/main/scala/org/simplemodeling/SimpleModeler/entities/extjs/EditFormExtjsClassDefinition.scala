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
class EditFormExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  pobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ExtjsClassDefinition(context, aspects, pobject, maker) {
  baseName = "Ext.form.Panel".some

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new EditFormExtjsClassAttributeDefinition(context, aspects, attr, this, ejmaker)
  }

  override protected def attribute_variables_Prologue {
    js_ps("title", "XYZ")
    jm_pln("items: [")
    jm_indent_up
  }

  override protected def attribute_variables_Epilogue {
    jm_indent_down
    jm_pln("],")
  }
}

class EditFormExtjsClassAttributeDefinition(
  pContext: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  attr: PAttribute,
  owner: ExtjsClassDefinition,
  maker: ExtjsTextMaker) extends ExtjsClassAttributeDefinition(pContext, aspects, attr, owner, maker) {

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    val xtypename = attr.attributeType(form_item)
    js_o(attr.attributeType(form_item) :+ 
        'anchor -> "100%")
  }
}
