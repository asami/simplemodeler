package org.simplemodeling.SimpleModeler.entities.gaej

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
 * @since   Apr. 10, 2009
 * @version Apr. 25, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejShowViewEntity(anEntity: GaejEntityEntity, aContext: GEntityContext) extends GaejViewEntityBase(anEntity, aContext) {

  val html = 
<html>
<head>
<title>Show {capitalizedTerm}</title>
{
  List(make_embedded_style, make_embedded_script)
}
</head>
[%
    {document_type_qname} doc = ({document_type_qname})request.getAttribute("doc");
    if (doc == null) {{
        doc = new {document_type_qname}();
    }}
%]
{
  declare_variables
}
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
      <td>{attr.name}</td><td>[%= doc.{make_value_string(attr)} %]</td>
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

  override protected def write_Content(out: BufferedWriter) {
    write_html(html, out)
  }
}
