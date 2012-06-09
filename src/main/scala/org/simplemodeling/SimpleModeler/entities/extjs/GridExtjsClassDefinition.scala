package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 14, 2012
 * @version Jun.  2, 2012
 * @author  ASAMI, Tomoharu
 */
class GridExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  extjsobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ExtjsClassDefinition(context, aspects, extjsobject, maker) {
  baseName = "Ext.grid.Panel".some
  set_widget_name(ascii_name)
//  aliasName = ("widget." + lower_ascii_name).some

  override protected def attribute(attr: PAttribute): ATTR_DEF = {
    new GridExtjsClassAttributeDefinition(context, aspects, attr, this, ejmaker)
  }

  override protected def attribute_variables_Prologue {
    js_ptrue("stateful")
    js_ps("stateId", "stateGrid")
    js_ps("title", label_name)
    js_po("viewConfig") {
      js_ptrue("stripeRows")
    }
    jm_pln("columns: [")
    jm_indent_up
  }

  override protected def attribute_variables_Epilogue {
    jm_indent_down
    jm_pln("],")
    js_ps("store", extjsContext.entityStoreQualifiedName(extjsObject))
  }
}

class GridExtjsClassAttributeDefinition(
  pContext: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  attr: PAttribute,
  owner: ExtjsClassDefinition,
  maker: ExtjsTextMaker) extends ExtjsClassAttributeDefinition(pContext, aspects, attr, owner, maker) {

  override protected def variable_plain_Attribute_Instance_Variable(typename: String, varname: String) {
    js_o(attr.attributeType(grid_column))
  }
}
