package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entities.gaej._
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}

/*
 * @since   Apr. 16, 2009
 * @version Apr. 18, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtEntityEditorHtmlEntity(val gaejEntity: GaejEntityEntity, val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  def script_name = {
    val moduleName = gwtContext.moduleName(gaejEntity.packageName);
    "../../" + moduleName + "/" + moduleName + ".nocache.js"
  }

  val html = <html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
<title>Entity Editor</title>
<script type="text/javascript" language="javascript" src={script_name}/>
<style>
<!--
.dialogPanel {
  margin: 5px;
}
h1 {background: #b3ada0}
div.menu {margin-top: 1em; margin-bottom:1em; background-color: #92b5a9}
table.menu {background-color: #92b5a9}
table.menu td.action {background-color: #92b5a9}
table.menu td.selected {background-color: white}
table.datasheet form {display:inline;margin:0 0 0 0;}
table{border-collapse:collapse;border-spacing:0;}
table.datasheet {font-size:inherit;font:100%; border-collapse:separate; border:1px solid #666666; border-left:none;}
table.datasheet {margin-top:0; margin-bottom:.2em}
table.datasheet td {border:1px solid #dedede; padding:2px; font-size:100%; vertical-align:top;}
table.datasheet th {border:1px solid #dedede; padding:2px; background-color:#165e83; color:#FFFFFF; font-weight:bold; font-size:100%; text-align:center}
.datasheetHeader td {border:1px solid #dedede; padding:2px; background-color:#165e83; color:#FFFFFF; font-weight:bold; font-size:100%; text-align:center}
-->
</style>
</head>
<body>
<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

<h1>Entity Editor</h1>

<div id={gwtContext.entryPointId(gaejEntity)}/>

<div class="menu">
<table class="menu">
<tr><td class="action"><a href="../../index.html">Home</a></td></tr>
</table>
</div>


</body>
</html>

  override protected def write_Content(out: BufferedWriter) {
    out.write(html.toString())
    out.flush()
  }
}
