package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.XML
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.AppendableTextBuilder

/*
 * @since   Apr. 10, 2009
 * @version Apr. 15, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejNewViewEntity(anEntity: GaejEntityEntity, aContext: GEntityContext) extends GaejViewEntityBase(anEntity, aContext) {
  val html =
<html>
<head>
<title>New {capitalizedTerm}</title>
{ make_embedded_style }
{ make_embedded_script }
</head>
[%
    {document_type_qname} doc = new {document_type_qname}();
%]
{ declare_variables }
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

  override protected def write_Content(out: BufferedWriter) {
    write_html(html, out)
  }
}
