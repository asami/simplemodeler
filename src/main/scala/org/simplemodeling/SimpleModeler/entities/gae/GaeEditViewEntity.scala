package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.{XML, Elem}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * @since   Mar. 10, 2009
 * @version Apr.  7, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeEditViewEntity(anEntity: GaeObjectEntity, aContext: GEntityContext) extends GaeViewEntityBase(anEntity, aContext) {
  override protected def write_Content(out: BufferedWriter) {
    val html =
<html>
<head>
<title>Edit {capitalizedTerm}</title>
{
  List(make_embedded_style, make_embedded_script)
}
</head>
<body class={body_class}>
<h1>Edit {capitalizedTerm}</h1>

<form action={getActionPath("update")} method="post">
<input type="hidden" name="key" value={getIdCode} />
<table class="datasheet">
<thead>
<tr>
  <th>項目</th><th>値</th>
</tr>
</thead>
<tbody>
<tr>
  <td>{idName}</td><td>{getIdCode}</td>
</tr>
{
  for (attr <- entity.attributes.toList if !attr.isId) yield {
    <tr>
      <td>{attr.name}</td><td>{make_input(attr)}</td>
    </tr>
  }
}
</tbody>
</table>

<input type="submit" value="Update" />
</form>

<div class="menu">
<table class="menu">
<tr><td class="action"><a href={getActionPath("home")}>Home</a></td><td class="action"><a href={getActionPath("index")}>Index</a></td><td class="action"><a href={getActionPath("new")}>New</a></td></tr>
</table>
</div>

</body>
</html>
    XML.write(out, html, entityContext.textEncoding, false, null)
  }
}

class GaeEditViewEntityClass extends GEntityClass {
  type Instance_TYPE = GaeModelsEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "html"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeModelsEntity(aDataSource, aContext))
}
