package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 14, 2012
 *  version Dec. 26, 2012
 * @version May.  7, 2013
 * @author  ASAMI, Tomoharu
 */
class StoreExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  extjsobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ExtjsClassDefinition(context, aspects, extjsobject, maker) {
  baseName = "Ext.data.Store".some

  override protected def attribute_variables_Prologue {
//    js_ptrue("autoLoad")
//    js_ptrue("autoSync")
    js_ps("model", context.entityModelQualifiedNickname(extjsobject))
    js_po_tail("proxy") {
      js_ps("type", "rest")
      js_ps("url", extjsContext.restUri(extjsobject))
      js_po("reader") {
        js_ps("type", "json")
        js_ps_tail("root", "data")
      }
      js_po_tail("writer") {
        js_ps_tail("type", "json")
      }
    }
  }
}
