package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entity.domain._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Jun.  3, 2012
 * @version Jun. 10, 2012
 * @author  ASAMI, Tomoharu
 */
class NavigationStoreExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  extjsobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ExtjsClassDefinition(context, aspects, extjsobject, maker) {
  baseName = "Ext.data.TreeStore".some

  override protected def attribute_variables_Prologue {
    js_po("root") {
      js_pboolean("expanded", true)
      js_ps("text", "Entity")
      js_pa("children") {
        _actors
        _roles
        _events
        _resources
      }
      js_po("writer") {
        js_ps("type", "json")
      }
    }
  }

  private def _actors {
    js_o {
      js_ps("text", "Actor")
      js_pboolean("leaf", false)
      js_pboolean("expanded", false)
      js_pa("children") {
        _defs(actor_entities)
      }
    }
  }

  private def _roles {
    js_o {
      js_ps("text", "Role")
      js_pboolean("leaf", false)
      js_pboolean("expanded", false)
      js_pa("children") {
        _defs(role_entities)
      }
    }
  }

  private def _events {
    js_o {
      js_ps("text", "Event")
      js_pboolean("leaf", false)
      js_pboolean("expanded", false)
      js_pa("children") {
        _defs(event_entities)
      }
    }
  }

  private def _resources {
    js_o {
      js_ps("text", "Resource")
      js_pboolean("leaf", false)
    js_pboolean("expanded", false)
      js_pa("children") {
        _defs(resource_entities)
      }
    }
  }

  private def _defs(xs: Seq[PEntityEntity]) {
    if (xs.nonEmpty) {
      js_o {
        for (x <- xs) {
          js_ps("text", x.label)
          js_pboolean("leaf", true)
        }
      }
    }
  }
}
