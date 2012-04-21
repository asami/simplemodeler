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
 * @version Apr. 21, 2012
 * @author  ASAMI, Tomoharu
 */
class PlayMainControllerEntity(pContext: PEntityContext) extends PObjectEntity(pContext) {
  val fileSuffix = "scala"
  val template = """package controllers

import play.api._
import play.api.mvc._

object AppMain extends Controller {
   def index = Action {
    Ok(views.html.app())
  }
 }
"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template, Map.empty)
    out.append(text.toString)
    out.flush
  }  
}
