package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 14, 2012
 * @version Jun. 10, 2012
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
      js_o {
        js_ps("region", "west")
        js_ps("xtype", "treepanel")
        js_ps("title", "一覧")
        js_pnumber("width", 200)
        js_ptrue("collapsible")
        js_ps("store", "app.store.NavigationStore")
      }
      js_o {
        js_ps("region", "center")
        js_ps("xtype", "tabpanel")
        js_ps("title", "情報")
        js_pa("items") {
          _entities
        }
      }
    }
  }

  private def _entities {
    _entities(
      actor_entities
      ++ role_entities
      ++ event_entities
      ++ resource_entities)
  }

  private def _entities(xs: Seq[PEntityEntity]) {
    for (x <- xs) {
      js_o {
        js_ps("xtype", extjsContext.entityGridViewWidgetName(x))
        js_ps("title", x.label)
      }
    }
  }
}
