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
 * @version Dec. 18, 2010
 * @author  ASAMI, Tomoharu
 */
class GaejIndexViewEntity(anEntity: GaejEntityEntity, aContext: GEntityContext) extends GaejViewEntityBase(anEntity, aContext) {

  val html = <html>
<head>
<title>Index {capitalizedTerm}</title>
{make_embedded_style}
</head>
[%
    List[[{document_type_qname}]] docs = (List[[{document_type_qname}]])request.getAttribute("docs");
    String action_home = (String)request.getAttribute("action_home");
    String action_new = (String)request.getAttribute("action_new");
    String action_show = (String)request.getAttribute("action_show");
    String action_edit = (String)request.getAttribute("action_edit");
    String action_destroy = (String)request.getAttribute("action_destroy");
    if (docs == null) {{
      docs = new ArrayList[[{document_type_qname}]]();
    }}
    if (action_home == null) {{
      action_home = "/";
    }}
    if (action_new == null) {{
      action_new = "/";
    }}
    if (action_show == null) {{
      action_show = "/";
    }}
    if (action_edit == null) {{
      action_edit = "/";
    }}
    if (action_destroy == null) {{
      action_destroy = "/";
    }}
%]
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
[%  if (!docs.isEmpty()) {{
      for ({document_type_qname} doc: docs) {{ %]
       <tr>
    {
      val list = for (attr <- entity.attributes.toList) yield {
        <td>[%= doc.{make_value_string(attr)} %]</td>
      }
      list :::
      List(<td><form action={getActionPath("show")} method="get"><input type="hidden" name="key" value={getIdCode} /><input type="submit" value="Show" /></form></td>,
	   <td><form action={getActionPath("edit")} method="get"><input type="hidden" name="key" value={getIdCode} /><input type="submit" value="Edit" /></form></td>,
	   <td><form action={getActionPath("destroy")} method="post"><input type="hidden" name="key" value={getIdCode} /><input type="submit" value="Delete" /></form></td>)
    }
    </tr>
[%    }}
    }} else {{ %]
  <tr><td colspan={(entity.attributes.size + 3).toString}>Empty</td></tr>
[%  }} %]
</tbody>
 </table>

<div class="menu">
<table class="menu">
<tr><td class="action"><a href="[%= action_home %]">Home</a></td><td class="selected">Index</td><td class="action"><a href="[%= action_new %]">New</a></td></tr>
</table>
</div>

</body>
</html>

  override protected def write_Content(out: BufferedWriter) {
    write_html(html, out)
  }
}
