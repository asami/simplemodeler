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
class PlayRestControllerEntity(pContext: PEntityContext) extends PObjectEntity(pContext) {
  val fileSuffix = "scala"
  val template = """package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.db._
import play.api.Play.current
import anorm._

object AppRest extends Controller {
  def list() = Action {
    implicit val conn = DB.getConnection()
    val query = SQL("Select * from account")
    val records = query().map(_to_json)
    val result = JsObject(List(
      "success" -> JsString("true"),
      "data" -> JsArray(records)))
    Ok(result).as("application/json")
  }

  private def _to_json(row: SqlRow): JsObject = {
    JsObject(row.asMap.toList.map { col =>
      val propname = _prop_name(col._1)
      col._2 match {
        case Some(v) => (propname, JsString(v.toString))
        case None => (propname, JsNull)
      }          
    })
  }

  private def _prop_name(n: String) = {
    n.split("[.]").last.toLowerCase
  }

  def get(id: String) = Action {
    val result = JsObject(List(
      "a" -> JsString("10")))
    Ok(result).as("application/json")
  }
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template, Map.empty)
    out.append(text.toString)
    out.flush
  }  
}
