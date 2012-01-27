package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.{Elem, Node}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}

/*
 * @since   Apr. 10, 2009
 * @version Jun.  4, 2009
 * @author  ASAMI, Tomoharu
 */
abstract class GaejViewEntityBase(val entity: GaejEntityEntity, aContext: GEntityContext) extends GEntity(aContext) {
  type DataSource_TYPE = GDataSource

  override def is_Text_Output = true

  val term = entity.term
  val capitalizedTerm = UString.capitalize(term)
  val idName = entity.idName

  private def getAsString(attrName: String) =
    "get" + attrName.capitalize + "_asString()"

  private def getAsString(attrName: String, index: Int) =
    "get" + attrName.capitalize + "_asString(" + index + ")"

  val getIdCode = {
    "<%= doc." + getAsString(idName) + " %>"
  }

  def getAttrNameCode(attr: GaejAttribute) = {
    "<%= doc." + getAsString(attr.name) + " %>"
  }

  def getAttrNameCodeDate(attr: GaejAttribute) = {
    "<%= doc." + getAsString(attr.name + "_date") + " %>"
  }

  def getAttrNameCodeTime(attr: GaejAttribute) = {
    "T<%= doc." + getAsString(attr.name + "_time") + " %>"
  }

  def getAttrNameCodeEntity(attr: GaejAttribute, entityType: GaejEntityType) = {
    "<%= doc." + getAsString(entityType.entity.idName) + " %>"
  }

//
  def getAttrNameCode(attr: GaejAttribute, index: Int) = {
    "<%= doc." + getAsString(attr.name, index) + " %>"
  }

  def getAttrNameCodeDate(attr: GaejAttribute, index: Int) = {
    "<%= doc." + getAsString(attr.name + "_date", index) + " %>"
  }

  def getAttrNameCodeTime(attr: GaejAttribute, index: Int) = {
    "T<%= doc." + getAsString(attr.name + "_time", index) + " %>"
  }

  def getAttrNameCodeEntity(attr: GaejAttribute, entityType: GaejEntityType, index: Int) = {
    "<%= doc." + getAsString(entityType.entity.idName, index) + " %>"
  }

//
  def getEntityListCode(attr: GaejAttribute) = {
    "<%= entity_refs.get(\"" + entity_type_qname(attr) + "\") %>"
  }

  def getActionPath(action: String) = {
    "<%= action_" + action + " %>"
  }

  private def entity_type_qname(attr: GaejAttribute): String = {
    attr.attributeType.asInstanceOf[GaejEntityType].qualifiedName
  }

  protected lazy val document_type_qname = {
    entity.packageName + "." + entity.documentName
  }

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  protected final def make_embedded_style_string = {
    make_embedded_style.mkString
  }

  // unify GaeViewEntityBase
  protected final def make_embedded_style: Seq[Node] =
    List(
<style>
<!--
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
-->
</style>,
<style>
@import 'http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dijit/themes/tundra/tundra.css';
@import 'http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/resources/dojo.css'
</style>
    )

  protected final def make_embedded_script: Seq[Node] = {
    List(
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/dojo.xd.js" djConfig="parseOnLoad: true"></script>,
<script type="text/javascript">
dojo.require('dojo.parser');
dojo.require('dojo.data.ItemFileReadStore');
dojo.require('dijit.form.TextBox');
dojo.require('dijit.form.DateTextBox');
dojo.require('dijit.form.TimeTextBox');
dojo.require('dijit.form.CheckBox');
dojo.require('dijit.form.ComboBox');
</script>
    )
  }

  protected final def make_input_table_records(attr: GaejAttribute): Seq[Elem] = {
    val nRecords = 5 // XXX

    def make_list_input_data_records(attr: GaejAttribute): List[Elem] = {
      for (i <- (1 until nRecords).toList) yield {
        <tr><td>{make_list_input(attr, i)}</td></tr>
      }
    }

    attr.multiplicity match {
      case GaejOne => List(<tr><td>{attr.name}</td><td>{make_input(attr)}</td></tr>)
      case GaejZeroOne => List(<tr><td>{attr.name}</td><td>{make_input(attr)}</td></tr>)
      case GaejOneMore => {
        <tr><td rowspan={nRecords.toString}>{attr.name}</td><td>{make_list_input(attr, 0)}</td></tr> :: make_list_input_data_records(attr)
        
      }
      case GaejZeroMore => {
        <tr><td rowspan={nRecords.toString}>{attr.name}</td><td>{make_list_input(attr, 0)}</td></tr> :: make_list_input_data_records(attr)
      }
      case m: GaejRange => throw new UnsupportedOperationException("?")
      case _ => throw new UnsupportedOperationException("?")
    }
  }

  private def make_list_input(attr: GaejAttribute, index: Int): Elem = {
    attr.attributeType match {
      case t: GaejStringType => {
        <input type="text" name={attr.name + "_" + index}
               value={getAttrNameCode(attr, index)}
               dojoType="dijit.form.TextBox"/>
      }
      case t: GaejDateTimeType => {
        <div>
          <input type="text" name={attr.name + "_" + index + "_date"}
                 value={getAttrNameCodeDate(attr, index)}
                 dojoType="dijit.form.DateTextBox"/>
          <input type="text" name={attr.name + "_" + index + "_time"}
                 value={getAttrNameCodeTime(attr, index)}
                 dojoType="dijit.form.TimeTextBox"/>
          Now 
          <input type="checkbox" name={attr.name + "_" + index + "_now"}
                 value="false"
                 dojoType="dijit.form.CheckBox"/>
        </div>
      }
      case e: GaejEntityType => {
        <div>
          <div dojoType="dojo.data.ItemFileReadStore"
               url={getEntityListCode(attr)}
               jsId={entity_type_qname(attr)}></div>
          <select name={attr.name + "_" + index} value={getAttrNameCodeEntity(attr, e, index)}
                  dojoType="dijit.form.ComboBox"
                  store={entity_type_qname(attr)} searchAttr="label"/>
        </div>
      }
      case _ => {
        <input type="text" name={attr.name + "_" + index}
               value={getAttrNameCode(attr, index)}
               dojoType="dijit.form.TextBox"/>
      }
    }
  }

  protected final def make_input(attr: GaejAttribute): Elem = {
    attr.attributeType match {
      case t: GaejStringType => {
        <input type="text" name={attr.name}
               value={getAttrNameCode(attr)}
               dojoType="dijit.form.TextBox"/>
      }
      case t: GaejDateTimeType => {
        <div>
          <input type="text" name={attr.name + "_date"}
                 value={getAttrNameCodeDate(attr)}
                 dojoType="dijit.form.DateTextBox"/>
          <input type="text" name={attr.name + "_time"}
                 value={getAttrNameCodeTime(attr)}
                 dojoType="dijit.form.TimeTextBox"/>
          Now 
          <input type="checkbox" name={attr.name + "_now"}
                 value="false"
                 dojoType="dijit.form.CheckBox"/>
        </div>
      }
      case e: GaejEntityType => {
        <div>
          <div dojoType="dojo.data.ItemFileReadStore"
               url={getEntityListCode(attr)}
               jsId={entity_type_qname(attr)}></div>
          <select name={attr.name} value={getAttrNameCodeEntity(attr, e)}
                  dojoType="dijit.form.ComboBox"
                  store={entity_type_qname(attr)} searchAttr="label"/>
        </div>
      }
      case _ => {
        <input type="text" name={attr.name}
               value={getAttrNameCode(attr)}
               dojoType="dijit.form.TextBox"/>
      }
    }
  }

  protected def make_input_id(idName: String): Elem = {
    <input type="text" name={idName}
           dojoType="dijit.form.TextBox"/> // XXX
  }

  protected final def make_value_string(attr: GaejAttribute) = {
    attr.attributeType match {
      case t: GaejDateTimeType => getAsString(attr.name) + ".replace('T', ' ')"
      case e: GaejEntityType => getAsString(e.entity.idName)
      case _ => getAsString(attr.name)
    }
  }

  protected def body_class = "tundra"

  protected final def declare_variables = {
"""[%
    String action_home = (String)request.getAttribute("action_home");
    String action_create = (String)request.getAttribute("action_create");
    String action_destroy = (String)request.getAttribute("action_destroy");
    String action_edit = (String)request.getAttribute("action_edit");
    String action_index = (String)request.getAttribute("action_index");
    String action_new = (String)request.getAttribute("action_new");
    String action_search = (String)request.getAttribute("action_search");
    String action_show = (String)request.getAttribute("action_show");
    String action_update = (String)request.getAttribute("action_update");
    Map<String, String> entity_refs = (Map<String, String>)request.getAttribute("entity_refs");

    if (action_home == null) {{
      action_home = "/";
    }}
    if (action_create == null) {{
      action_create = "/";
    }}
    if (action_destroy == null) {{
      action_destroy = "/";
    }}
    if (action_edit == null) {{
      action_edit = "/";
    }}
    if (action_index == null) {{
      action_index = "/";
    }}
    if (action_new == null) {{
      action_new = "/";
    }}
    if (action_search == null) {{
      action_search = "/";
    }}
    if (action_show == null) {{
      action_show = "/";
    }}
    if (action_update == null) {{
      action_update = "/";
    }}
%]
"""    
  }

  protected def write_html(html: Elem, out: BufferedWriter) = {
    val buffer = new AppendableTextBuilder(out)
    buffer.append("""<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
Calendar lastModified = Calendar.getInstance();
Calendar expires = Calendar.getInstance();
expires.set(1970, 0, 1, 0, 0, 0);
response.setDateHeader("Last-Modified", lastModified.getTime().getTime());
response.setDateHeader("Expires", expires.getTime().getTime());
response.setHeader("pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
%>
""")

    buffer.append(html.toString,
                  Map("&quot;" -> "\"",
                      "&lt;" -> "<",
                      "&gt;" -> ">",
                      "\\[\\[" -> "<",
                      "\\]\\]" -> ">",
                      "\\[%" -> "<%",
                      "%\\]" -> "%>"))
    buffer.flush()
    buffer
  }    
}
