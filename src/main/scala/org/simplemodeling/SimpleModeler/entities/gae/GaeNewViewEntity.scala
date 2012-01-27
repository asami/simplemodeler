package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.XML
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * @since   Mar. 10, 2009
 * @version Apr.  8, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeNewViewEntity(anEntity: GaeObjectEntity, aContext: GEntityContext) extends GaeViewEntityBase(anEntity, aContext) {
  override protected def write_Content(out: BufferedWriter) {
    val html =
<html>
<head>
<title>New {capitalizedTerm}</title>
{
  List(make_embedded_style, make_embedded_script)
}
</head>
<body class={body_class}>
<h1>New {capitalizedTerm}</h1>

<form action={getActionPath("create")} method="post">
<table class="datasheet">
<thead>
<tr>
  <th>項目</th><th>値</th>
</tr>
</thead>
<tbody>
  <tr>
    <td>{idName}</td><td>{make_input_id(idName)}</td>
  </tr>
{
  for (attr <- entity.attributes.toList if !attr.isId) yield {
    make_input_table_records(attr)
  }
}
</tbody>
</table>
<table class="action">
<tr>
  <td><input type="submit" value="Create" /></td>
</tr>
</table>
</form>

<div class="menu">
<table class="menu">
<tr><td class="action"><a href={getActionPath("home")}>Home</a></td><td class="action"><a href={getActionPath("index")}>Index</a></td><td class="selected">New</td></tr>
</table>
</div>

</body>
</html>
    XML.write(out, html, entityContext.textEncoding, false, null)
  }
}

class GaeCreateViewEntityClass extends GEntityClass {
  type Instance_TYPE = GaeModelsEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "html"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeModelsEntity(aDataSource, aContext))
}
