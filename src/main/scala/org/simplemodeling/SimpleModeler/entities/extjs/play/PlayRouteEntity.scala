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
 * @version Apr. 20, 2012
 * @author  ASAMI, Tomoharu
 */
class PlayRouteEntity(pContext: PEntityContext) extends PObjectEntity(pContext) {
  val fileSuffix = ""
  val template = """# Routes
# This file defines all application routes (Higher priority routes first)
# ~~~~

# Home page
GET     /                           controllers.Application.index

GET     /account                    controllers.Account.index

GET     /app1                       controllers.App1.index

GET	/account/rest/list	    controllers.AccountRest.listAccounts

GET	/account/rest/setup	    controllers.AccountRest.setupAccounts

GET	/account/rest/:id	    controllers.AccountRest.getAccount(id: String)

# Map static resources from the /public folder to the /assets URL path
GET     /assets/*file               controllers.Assets.at(path="/public", file)
"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template, Map.empty)
    out.append(text.toString)
    out.flush
  }  
}
