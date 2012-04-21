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
class PlayMainViewEntity(pContext: PEntityContext) extends PObjectEntity(pContext) {
  val fileSuffix = "scala"  
  val template = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>App</title>
    <link rel="stylesheet" type="text/css" href="@routes.Assets.at("extjs/resources/css/ext-all.css")" />
    <script type="text/javascript" src="@routes.Assets.at("stylesheets/main.css")"></script>
    <script type="text/javascript" src="@routes.Assets.at("stylesheets/app.css")"></script>
    <script type="text/javascript" src="@routes.Assets.at("extjs/bootstrap.js")"></script>
    <script type="text/javascript" src="@routes.Assets.at("app.js")"></script>
  </head>
  <body></body>
"""

  override protected def write_Content(out: BufferedWriter) {
    val text = new JavaScriptTextMaker(template, Map.empty)
    out.append(text.toString)
    out.flush
  }  
}
