package org.simplemodeling.SimpleModeler.entities.gae

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.{Elem, Node}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString}

/*
 * Mar. 26, 2009
 * Apr. 10, 2009
 * ASAMI, Tomoharu
 */
abstract class GaeViewEntityBase(val entity: GaeObjectEntity, aContext: GEntityContext) extends GEntity(aContext) {
  type DataSource_TYPE = GDataSource

  override def is_Text_Output = true

  val term = entity.term
  val capitalizedTerm = UString.capitalize(term)
  val idName = {
    entity.attributes.find(_.isId) match {
      case Some(attr) => attr.name
      case None => ""
    }
  }
  val getIdCode = {
    "{{doc." + idName + "}}"
  }

  def getAttrNameCode(attr: GaeAttribute) = {
    "{{doc." + attr.name + "}}"
  }

  def getAttrNameCodeDate(attr: GaeAttribute) = {
    "{{doc." + attr.name + "_date}}"
  }

  def getAttrNameCodeTime(attr: GaeAttribute) = {
    "T{{doc." + attr.name + "_time}}"
  }

  def getEntityListCode(attr: GaeAttribute) = {
    "{{entity_" + entity_type_qname(attr).replace('.', '_') + "}}"
  }

  def getActionPath(action: String) = {
    "{{action_" + action + "}}"
//    "/" + term + "/" + action
  }

  private def entity_type_qname(attr: GaeAttribute): String = {
    attr.attributeType.asInstanceOf[GaeEntityType].qualifiedName
  }

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

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

  protected final def make_input_table_records(attr: GaeAttribute): Seq[Elem] = {
    val nRecords = 5 // XXX

    def make_list_input_data_records(attr: GaeAttribute): List[Elem] = {
      for (i <- (1 until nRecords).toList) yield {
        <tr><td>{make_list_input(attr, i)}</td></tr>
      }
    }

    attr.multiplicity match {
      case GaeOne => List(<tr><td>{attr.name}</td><td>{make_input(attr)}</td></tr>)
      case GaeZeroOne => List(<tr><td>{attr.name}</td><td>{make_input(attr)}</td></tr>)
      case GaeOneMore => {
        <tr><td rowspan={nRecords.toString}>{attr.name}</td><td>{make_list_input(attr, 0)}</td></tr> :: make_list_input_data_records(attr)
        
      }
      case GaeZeroMore => {
        <tr><td rowspan={nRecords.toString}>{attr.name}</td><td>{make_list_input(attr, 0)}</td></tr> :: make_list_input_data_records(attr)
      }
      case m: GaeRange => throw new UnsupportedOperationException("?")
      case _ => throw new UnsupportedOperationException("?")
    }
  }

  private def make_list_input(attr: GaeAttribute, index: Int): Elem = {
    attr.attributeType match {
      case t: GaeStringType => {
        <input type="text" name={attr.name + "_" + index}
               value={getAttrNameCode(attr)}
               dojoType="dijit.form.TextBox"/>
      }
      case t: GaeDateTimeType => {
        <div>
          <input type="text" name={attr.name + "_" + index + "_date"}
                 value={getAttrNameCodeDate(attr)}
                 dojoType="dijit.form.DateTextBox"/>
          <input type="text" name={attr.name + "_" + index + "_time"}
                 value={getAttrNameCodeTime(attr)}
                 dojoType="dijit.form.TimeTextBox"/>
          Now 
          <input type="checkbox" name={attr.name + "_" + index + "_now"}
                 value="false"
                 dojoType="dijit.form.CheckBox"/>
        </div>
      }
      case e: GaeEntityType => {
        <div>
          <div dojoType="dojo.data.ItemFileReadStore"
               url={getEntityListCode(attr)}
               jsId={entity_type_qname(attr)}></div>
          <select name={attr.name + "_" + index} value={getAttrNameCode(attr)}
                  dojoType="dijit.form.ComboBox"
                  store={entity_type_qname(attr)} searchAttr="label"/>
        </div>
      }
      case _ => {
        <input type="text" name={attr.name + "_" + index}
               value={getAttrNameCode(attr)}
               dojoType="dijit.form.TextBox"/>
      }
    }
  }

  protected final def make_input(attr: GaeAttribute): Elem = {
    attr.attributeType match {
      case t: GaeStringType => {
        <input type="text" name={attr.name}
               value={getAttrNameCode(attr)}
               dojoType="dijit.form.TextBox"/>
      }
      case t: GaeDateTimeType => {
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
      case e: GaeEntityType => {
        <div>
          <div dojoType="dojo.data.ItemFileReadStore"
               url={getEntityListCode(attr)}
               jsId={entity_type_qname(attr)}></div>
          <select name={attr.name} value={getAttrNameCode(attr)}
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

  protected def body_class = "tundra"
}
