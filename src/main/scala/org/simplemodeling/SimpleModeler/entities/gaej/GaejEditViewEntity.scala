package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.{XML, Elem}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * @since   Apr. 10, 2009
 * @version Jun.  4, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejEditViewEntity(anEntity: GaejEntityEntity, aContext: GEntityContext) extends GaejViewEntityBase(anEntity, aContext) {

  val html =
<html>
<head>
<title>Edit {capitalizedTerm}</title>
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
    make_input_table_records(attr)
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

  override protected def write_Content(out: BufferedWriter) {
    write_html(html, out)
  }
}
