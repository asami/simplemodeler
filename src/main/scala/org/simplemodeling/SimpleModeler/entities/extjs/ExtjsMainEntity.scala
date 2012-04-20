package org.simplemodeling.SimpleModeler.entities.extjs

import java.io._
import com.asamioffice.goldenport.text.JavaScriptTextMaker
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 20, 2012
 * @version Apr. 20, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsMainEntity(extjsContext: ExtjsEntityContext) extends ExtjsObjectEntity(extjsContext) {
  val template = """Ext.onReady(function () {
    Ext.Loader.setConfig({enabled:true});
});

Ext.application({
    autoCreateViewport: true,

    name: 'App1',

    appFolder: 'assets/app', // important using 'assets'

    controllers: [
        'Account'
    ],

    launch: function() {
//        Ext.create('App1.view.AccountViewport');
    }
});
"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template, Map.empty)
    out.append(text.toString)
    out.flush
  }  
}
