package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.XML
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * index, new, edit, show, create, update, destroy, search
 *
 * @since   Mar. 10, 2009
 * @version Apr.  7, 2009
 * @author  ASAMI, Tomoharu
 */
class GaeShowViewEntity(anEntity: GaeObjectEntity, aContext: GEntityContext) extends GaeViewEntityBase(anEntity, aContext) {
  override protected def write_Content(out: BufferedWriter) {
    val html = 
<html>
<head>
<title>Show {capitalizedTerm}</title>
{
  List(make_embedded_style, make_embedded_script)
}
</head>
<body>
<h1>Show {capitalizedTerm}</h1>

<table class="datasheet">
<thead>
<tr>
  <th>項目</th><th>値</th>
</tr>
</thead>
<tbody>
{
  for (attr <- entity.attributes.toList) yield {
    <tr>
      <td>{attr.name}</td><td>{{{{doc.{attr.name} }}}}</td>
    </tr>
  }
}
</tbody>
</table>

<table class="action">
<tr>
  <td><form action={getActionPath("edit")} method="get"><input type="hidden" name="key" value={getIdCode} /><input type="submit" value="Edit" /></form></td>
  <td><form action={getActionPath("destroy")} method="post"><input type="hidden" name="key" value={getIdCode} /><input type="submit" value="Delete" /></form></td>
</tr>
</table>
    
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

class GaeShowViewEntityClass extends GEntityClass {
  type Instance_TYPE = GaeModelsEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "html"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeModelsEntity(aDataSource, aContext))
}
