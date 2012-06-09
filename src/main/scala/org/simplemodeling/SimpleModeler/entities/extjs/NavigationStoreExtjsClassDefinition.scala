package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Jun.  3, 2012
 * @version Jun.  5, 2012
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
        js_ps("type", "json")
        js_ps("root", "data")
      }
      js_po("writer") {
        js_ps("type", "json")
      }
    }
  }
}

/*
Ext.define('app.store.EntityTreeStore', {
    extend: 'Ext.data.TreeStore',
    root: {
        expanded: true,
	text: 'Entity',
        children: [{
            text: 'Actor',
            leaf: false,
            expanded: true,
            children: [{
                text: 'Customer',
                leaf: true
            }, { 
                text: 'Clerk',
                leaf: true
            }]
        }]
    }
});
*/
