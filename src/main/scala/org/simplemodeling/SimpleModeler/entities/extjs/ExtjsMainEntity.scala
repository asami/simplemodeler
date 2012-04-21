package org.simplemodeling.SimpleModeler.entities.extjs

import java.io._
import com.asamioffice.goldenport.text.JavaScriptTextMaker
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._

/**
 * @since   Apr. 20, 2012
 * @version Apr. 21, 2012
 * @author  ASAMI, Tomoharu
 */
class ExtjsMainEntity(extjsContext: ExtjsEntityContext) extends ExtjsObjectEntity(extjsContext) {
  val template = """Ext.onReady(function () {
    Ext.Loader.setConfig({enabled:true});
});

Ext.application({
    autoCreateViewport: true,

    name: '%name%',

    appFolder: '%appFolder%',

    models: [
    ],

    controllers: [
        '%controller%'
    ],

    launch: function() {
//        Ext.create('%viewport%');
    }
});
"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template,
                                       Map("%name%" -> "app",
                                           "%appFolder%" -> "assets/app",
                                           "%controller%" -> "AppController",
                                           "%viewport%" -> "app.view.AppView"))
    out.append(text.toString)
    out.flush
  }  
}
