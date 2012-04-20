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
class ViewportExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  extjsobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ExtjsClassDefinition(context, aspects, extjsobject, maker) {
  baseName = "Ext.container.Viewport".some

  override protected def lifecycle_variables {
    js_po("layout") {
      js_ps("type", "border")
      js_pnumber("padding", 5)
    }
    js_pa("items") {
      js_oo {
        js_ps("region", "west")
        js_ps("title", "一覧")
        js_ptrue("collapsible")
        js_pa("items") {
          js_oo {
            js_ps("xtype", "ABC")
            js_ps("store", "ABC")
          }
        }
      }
      js_oo {
        js_ps("region", "center")
        js_ps("title", "ABC")
        js_pa("items") {
          js_oo {
            js_ps("xtype", "tabpanel")
            js_pa("items") {
              js_oo {
                js_ps("xtype", "ABC")
              }
            }
          }
        }
      }
    }
  }
}
