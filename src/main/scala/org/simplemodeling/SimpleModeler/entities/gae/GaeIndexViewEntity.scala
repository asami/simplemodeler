package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml._
import scala.xml.XML._
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * @since   Mar. 10, 2009
 * @version Dec. 18, 2010
 * @author  ASAMI, Tomoharu
 */
class GaeIndexViewEntity(anEntity: GaeObjectEntity, aContext: GEntityContext) extends GaeViewEntityBase(anEntity, aContext) {
  override protected def write_Content(out: BufferedWriter) {
    val html =
<html>
<head>
<title>Index {capitalizedTerm}</title>
{make_embedded_style}
</head>
<body>
<h1>Index {capitalizedTerm}</h1>

<table class="datasheet">
<thead>
<tr>
{
  val list = for (attr <- entity.attributes.toList) yield {
    <th>{attr.name}</th>
  }
  list ::: List(<th colspan="3"></th>)
}
</tr>
</thead>
<tbody>
{{% if docs %}}
  {{% for doc in docs %}}
    <tr>
    {
      val list: List[Elem] = for (attr <- entity.attributes.toList) yield {
        <td>{{{{doc.{attr.name} }}}}</td>
      }
      list :::
      List(<td><form action={getActionPath("show")} method="get"><input type="hidden" name="key" value={getIdCode} /><input type="submit" value="Show" /></form></td>,
	   <td><form action={getActionPath("edit")} method="get"><input type="hidden" name="key" value={getIdCode} /><input type="submit" value="Edit" /></form></td>,
	   <td><form action={getActionPath("destroy")} method="post"><input type="hidden" name="key" value={getIdCode} /><input type="submit" value="Delete" /></form></td>)
    }
    </tr>
  {{% endfor %}}
{{% else %}}
  <tr><td colspan={(entity.attributes.size + 3).toString}>Empty</td></tr>
{{% endif %}}
</tbody>
</table>

<div class="menu">
<table class="menu">
<tr><td class="action"><a href={getActionPath("home")}>Home</a></td><td class="selected">Index</td><td class="action"><a href={getActionPath("new")}>New</a></td></tr>
</table>
</div>

</body>
</html>
    XML.write(out, html, entityContext.textEncoding, false, null)
  }
}

class GaeIndexViewEntityClass extends GEntityClass {
  type Instance_TYPE = GaeModelsEntity

  override def accept_Suffix(suffix: String): Boolean = suffix == "html"

  override def reconstitute_DataSource(aDataSource: GDataSource, aContext: GEntityContext): Option[Instance_TYPE] = Some(new GaeModelsEntity(aDataSource, aContext))
}
