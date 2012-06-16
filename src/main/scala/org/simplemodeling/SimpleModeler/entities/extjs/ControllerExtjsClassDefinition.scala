package org.simplemodeling.SimpleModeler.entities.extjs

import scalaz._
import Scalaz._
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 14, 2012
 * @version Jun. 16, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class ControllerExtjsClassDefinition(
  context: ExtjsEntityContext,
  aspects: Seq[ExtjsAspect],
  pobject: ExtjsObjectEntity,
  maker: ExtjsTextMaker = null
) extends ExtjsClassDefinition(context, aspects, pobject, maker) {
  baseName = "Ext.app.Controller".some

  override protected def lifecycle_variables {
    def stringnize(s: String) = "'" + s + "'"
    js_pa("views", view_QNames.map(stringnize))
    js_pa("models", model_QNames.map(stringnize))
    js_pa("stores", store_QNames.map(stringnize))
    jm_code("""init: function() {
    this.control({
        'treepanel': {
            itemclick: function(button, event) {
            console.log(button);
            console.log(event);
                var name = event.data.text;
                var tabid = "tab-" + name;
                var tables = Ext.getCmp('tables-panel');
                var tab = tables.getComponent(tabid);
                if (tab == null) {
		        var n = name.substr(0, 1).toUpperCase() + name.substr(1);
                    var klass = 'app.view.' + n + 'Grid'
                    var view = Ext.create(klass, {
                        id: tabid,
                        title: name,
                    });
                    tab = tables.add(view);
                }
                if (tab != null) {
                    tables.setActiveTab(tab);
                }
            }
        }
    });
},
""")
  }

  def view_QNames: Seq[String] = Nil
  def model_QNames: Seq[String] = Nil
  def store_QNames: Seq[String] = Nil
}
