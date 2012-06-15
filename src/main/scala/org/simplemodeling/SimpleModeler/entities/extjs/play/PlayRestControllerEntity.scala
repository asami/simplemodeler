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
  def list(name: String) = Action {
    println("AppRest#list: " + name)
    implicit val conn = DB.getConnection()
    sqlList(name) match {
      case Some(sql) => {
        val query = SQL(sql)
        val records = query().map(_to_json)
        val result = JsObject(List(
          "success" -> JsString("true"),
          "data" -> JsArray(records)))
        Ok(result).as("application/json")
      }
      case None => {
        NotFound(name + "/")
      }
    }
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

  def get(name: String, id: String) = Action {
    println("AppRest#get: %s, %s".format(name, id))
    implicit val conn = DB.getConnection()
    sqlGet(name, id) match {
      case Some(sql) => {
        val query = SQL(sql)
        val records = query().map(_to_json)
        val result = JsObject(List(
          "success" -> JsString("true"),
          "data" -> records.head))
        Ok(result).as("application/json")
      }
      case None => {
        NotFound(name + "/" + id)
      }
    }
  }

  protected def sqlList(name: String): Option[String] = {
    Some("SELECT * FROM %s;".format(name))
  }

  protected def sqlGet(name: String, id: String): Option[String] = {
    Some("SELECT * FROM %s WHERE id='%s';".format(name, id))
  }
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template, Map.empty)
    out.append(text.toString)
    out.flush
  }  
}
