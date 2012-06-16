package org.simplemodeling.SimpleModeler.entities.extjs.play

import java.io.BufferedWriter
import com.asamioffice.goldenport.text.JavaScriptTextMaker
import org.goldenport.entity.GEntity
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.GEntityContext
import org.simplemodeling.SimpleModeler.entities._
import org.simplemodeling.SimpleModeler.entities.extjs._

/**
 * @since   Apr. 20, 2012
 * @version Jun. 16, 2012
 * @author  ASAMI, Tomoharu
 */
class PlayRouteEntity(pContext: PEntityContext) extends PObjectEntity(pContext) {
  val fileSuffix = ""
  val template = """# Routes
# This file defines all application routes (Higher priority routes first)
# ~~~~

# Home page
GET     /                           controllers.Application.index

GET     /app                        controllers.AppMain.index

GET	/app/rest/:name	            controllers.AppRest.list(name)

GET	/app/rest/:name/:id	    controllers.AppRest.get(name, id)

# Map static resources from the /public folder to the /assets URL path
GET     /assets/*file               controllers.Assets.at(path="/public", file)
"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template, Map.empty)
    out.append(text.toString)
    out.flush
  }  
}
