package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

/*
 * @since   Apr. 10, 2009
 *  version Nov. 12, 2009
 * @version Dec. 15, 2011
 * @author  ASAMI, Tomoharu
 */
class GaejDocumentEntity(aContext: GaejEntityContext) extends GaejObjectEntity(aContext) {
  var modelDocumentOption: Option[SMDocument] = None
  var modelEntityOption: Option[SMEntity] = None

  protected final def id_var_name(attr: GaejAttribute) = {
    gaejContext.variableName4RefId(attr)
  }

  protected final def id_attr_type_name(attr: GaejAttribute) = {
    gaejContext.attributeTypeName4RefId(attr)
  }

  override protected def write_Content(out: BufferedWriter) {
    val buffer = new JavaTextMaker

    def make_package {
      require (packageName != null)
      if (packageName != "") {
        buffer.print("package ")
        buffer.print(packageName)
        buffer.println(";")
        buffer.println()
      }
    }

    def make_imports {
      var isPrint = false

      def make_import_object(anObject: GaejObjectReferenceType) {
        if (packageName != anObject.packageName) {
          make_import(anObject.qualifiedName)
        }
      }

      def make_import_entity(anEntity: GaejEntityType) {
        if (packageName != anEntity.packageName) {
          make_import(anEntity.qualifiedName)
        }
      }

      def make_import(aName: String) {
          buffer.print("import ")
          buffer.print(aName)
          buffer.println(";")
          isPrint = true
      }

      make_import("java.util.*")
      make_import("java.math.*")
      make_import("java.io.IOException")
      make_import("com.google.appengine.api.datastore.*")
      make_import("com.google.appengine.api.users.User")
      if (_baseObject != null) {
        make_import_object(_baseObject)
      }
      val attrs = attributes.filter(_.attributeType.isEntity)
      for (attr <- attrs) {
        val entity = attr.attributeType.asInstanceOf[GaejEntityType]
        make_import_entity(entity)
      }
      if (isPrint)
        buffer.println()
    }

    make_package
    make_imports
    writeModel(buffer)
    out.append(buffer.toString)
    out.flush
  }

  final def writeModel(buffer: JavaTextMaker) {
    def make_attributes {
      for (attr <- attributes) {
        make_attribute_variable(attr)
        buffer.println()
      }
      buffer.println("public MDEntityInfo entity_info = null;")
    }

    def make_attribute_variable(attr: GaejAttribute) {
      val varName = var_name(attr)

      def make_key_long {
        buffer.print("public Long ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_unencoded_string {
        buffer.print("public String ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_key {
        buffer.print("public Key ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_as_encoded_string {
        buffer.print("public String ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_plain_id {
        attr.idPolicy match {
          case SMAutoIdPolicy => make_key_long
          case SMApplicationIdPolicy => make_key_unencoded_string
        }
      }

      def make_application_id {
        make_key_unencoded_string
      }

      if (attr.isId) {
/* 2009-11-06
        modelEntityOption.flatMap(entity => Some(entity.appEngine)) match {
          case Some(appEngine) => {
            if (appEngine.logical_operation) {
              make_application_id
            } else {
              make_plain_id
            }
          }
          case None => make_plain_id
        }
*/
        buffer.print("public ")
        buffer.print(java_type(attr))
        buffer.print(" ")
        buffer.print(varName)
        buffer.println(";")
      } else {
	attr.attributeType match {
	  case t: GaejDateTimeType => {
            buffer.print("public ")
            buffer.print(java_type(attr))
            buffer.print(" ")
            buffer.print(varName)
            buffer.println(";")
	    buffer.print("public Date ")
	    buffer.print(varName)
	    buffer.println("_date = null;")
	    buffer.print("public Date ")
	    buffer.print(varName)
	    buffer.println("_time = null;")
	    buffer.print("public boolean ")
	    buffer.print(varName)
	    buffer.println("_now = false;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_date_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_time_string = null;")
	    buffer.print("public String ")
	    buffer.print(varName)
	    buffer.println("_now_string = null;")
	  }
	  case e: GaejEntityType => {
            val idVarName = id_var_name(attr)
            val typeName = id_attr_type_name(attr)
            val docName = e.entity.documentName
            if (attr.isHasMany) {
              buffer.print("public List<%s> ".format(typeName))
              buffer.print(idVarName)
              buffer.println(" = new ArrayList<%s>();".format(typeName))
              buffer.print("public List<%s> ".format(docName))
              buffer.print(varName)
              buffer.println(" = new ArrayList<%s>();".format(docName))
            } else {
              buffer.print("public %s ".format(typeName))
              buffer.print(idVarName)
              buffer.println(";")
              buffer.print("public %s ".format(docName))
              buffer.print(varName)
              buffer.println(";")
            }
          }
	  case e: GaejEntityPartType => {
            buffer.print("public ")
            buffer.print(java_doc_type(attr))
            buffer.print(" ")
            buffer.print(varName)
            if (attr.isHasMany) {
              buffer.print(" = new ArrayList<")
              buffer.print(java_doc_element_type(attr));
              buffer.print(">()")
            }
            buffer.println(";")
          }
	  case p: GaejPowertypeType => {
            buffer.print("public ")
            buffer.print(java_doc_type(attr))
            buffer.print(" ")
            buffer.print(varName)
            if (attr.isHasMany) {
              buffer.print(" = new ArrayList<")
              buffer.print(java_doc_element_type(attr));
              buffer.print(">()")
            }
            buffer.println(";")
          }
	  case _ => {
            buffer.print("public ")
            buffer.print(java_doc_type(attr))
            buffer.print(" ")
            buffer.print(varName)
            if (attr.isHasMany) {
              buffer.print(" = new ArrayList<")
              buffer.print(java_doc_element_type(attr));
              buffer.print(">()")
            }
            buffer.println(";")
          }
	}
      }
    }

    def make_null_constructor {
      buffer.print("public ")
      buffer.print(name)
      buffer.println("() {")
      buffer.println("}")
    }

    def make_getters {
      for (attr <- attributes) {
        make_attribute_getters(attr)
      }
    }

    def make_attribute_getters(attr: GaejAttribute) {
      val varName = var_name(attr)

      def make_key_long {
        buffer.method("public String get" + attr.name.capitalize + "_asString()") {
          buffer.print("return ")
          buffer.print(varName)
          buffer.println(".toString();")
        }
      }

      def make_key_unencoded_string {
        buffer.print("public String get")
        buffer.print(attr.name.capitalize)
        buffer.println("_asString() {");
        buffer.indentUp
        buffer.print("if (")
        buffer.print(varName)
        buffer.println(" == null) {")
        buffer.indentUp
        buffer.println("return \"\";")
        buffer.indentDown
        buffer.println("} else {")
        buffer.indentUp
        buffer.print("return ");
        buffer.print(varName)
        buffer.println(";")
        buffer.indentDown
        buffer.println("}")
        buffer.indentDown
        buffer.println("}")
      }

      def make_key_key {
        buffer.print("public Key ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_key_as_encoded_string {
        buffer.print("public String ")
        buffer.print(varName)
        buffer.println(";")
      }

      def make_single {
/*
        def make_asString(varName: String, expr: String) {
          buffer.method("public String get" + varName.capitalize + "_asString()") {
            buffer.makeIfElse(varName + " == null") {
              buffer.makeReturn("\"\"")
            }
            buffer.makeElse {
              buffer.makeReturn(expr)
            }
          }
        }
*/

	attr.attributeType match {
	  case t: GaejDateTimeType => {
            make_single_object_get_asString(attr.name, "Util.dateTime2text(" + attr.name + ")", buffer)
/*
            buffer.print("public String get")
            buffer.print(attr.name.capitalize)
            buffer.println("_asString() {");
            buffer.indentUp
            buffer.print("if (")
            buffer.print(attr.name)
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return Util.dateTime2text(");
            buffer.print(attr.name)
            buffer.println(");")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")
*/
            buffer.print("public String get")
            buffer.print(attr.name.capitalize)
            buffer.println("_date_asString() {");
            buffer.indentUp
            buffer.print("if (")
            buffer.print(varName)
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return Util.date2text(");
            buffer.print(varName)
            buffer.println(");")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")

            buffer.print("public String get")
            buffer.print(attr.name.capitalize)
            buffer.println("_time_asString() {");
            buffer.indentUp
            buffer.print("if (")
            buffer.print(varName)
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return Util.time2text(");
            buffer.print(varName)
            buffer.println(");")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")
          }
          case t: GaejDateType => make_single_object_get_asString(attr.name, "Util.date2text(" + varName + ")", buffer)
          case t: GaejTimeType => make_single_object_get_asString(attr.name, "Util.time2text(" + varName + ")", buffer)
	  case e: GaejEntityType => {
            val varName = id_var_name(attr)
            val attrTypeName = id_attr_type_name(attr)
            buffer.print("public String get")
            buffer.print(varName.capitalize)
            buffer.println("_asString() {")
            buffer.indentUp
            buffer.print("if (")
            buffer.print(varName)
            buffer.println(" == null) {")
            buffer.indentUp
            buffer.println("return \"\";")
            buffer.indentDown
            buffer.println("} else {")
            buffer.indentUp
            buffer.print("return ")
            if ("String".equals(attrTypeName)) {
              buffer.print(varName)
            } else {
              buffer.print("Util.datatype2string(%s)".format(varName))
            }
            buffer.println(";")
            buffer.indentDown
            buffer.println("}")
            buffer.indentDown
            buffer.println("}")
          }
	  case p: GaejEntityPartType => {
            buffer.method("public String get" + attr.name.capitalize + "_asString()") {
              buffer.print("if (")
              buffer.print(varName)
              buffer.println(" == null) {")
              buffer.indentUp
              buffer.println("return \"\";")
              buffer.indentDown
              buffer.println("} else {")
              buffer.indentUp
              buffer.print("return ");
              buffer.print(varName)
              buffer.println(".toString();")
              buffer.indentDown
              buffer.println("}")
            }
          }
	  case _ => {
            buffer.print("public String get")
            buffer.print(attr.name.capitalize)
            buffer.println("_asString() {");
            buffer.indentUp
            if (attr.isDataType) {
              buffer.print("return Util.datatype2string(");
              buffer.print(varName)
              buffer.println(");")
            } else {
              buffer.print("if (")
              buffer.print(varName)
              buffer.println(" == null) {")
              buffer.indentUp
              buffer.println("return \"\";")
              buffer.indentDown
              buffer.println("} else {")
              buffer.indentUp
              buffer.print("return ");
              buffer.print(varName)
              buffer.println(".toString();")
              buffer.indentDown
              buffer.println("}")
            }
            buffer.indentDown
            buffer.println("}")
          }
	}
      }

      def make_multiple {
/*
        def make_asString(varName: String, typeName: String, expr: String) {
          buffer.method("public String get" + varName.capitalize + "_asString()") {
            buffer.makeIf(varName + ".isEmpty()") {
              buffer.makeReturn("\"\"")
            }
            buffer.makeVar("buf", "StringBuilder")
            buffer.makeVar("last", typeName, varName + ".get(" + varName + ".size() - 1)")
            buffer.makeFor(typeName + " elem : " + varName) {
              buffer.makeAppendExpr(expr)
              buffer.makeIf("elem != last") {
                buffer.makeAppendString(", ")
              }
            }
            buffer.makeReturn("buf.toString()")
          }
        }

        def make_asStringIndex(varName: String, typeName: String, expr: String) {
          buffer.method("public String get" + varName.capitalize + "_asString(int index)") {
            buffer.makeIfElse(varName + ".size() <= index") {
              buffer.makeReturn("\"\"")
            }
            buffer.makeElse {
              buffer.makeVar("elem", typeName, varName + ".get(index)")
              buffer.makeReturn(expr)
            }
          }
        }

        def make_asStringList(varName: String, typeName: String, expr: String) {
          buffer.method("public List<String> get" + varName.capitalize + "_asStringList()") {
            buffer.makeVar("list", "List<String>", "new ArrayList<String>()")
            buffer.makeFor(typeName + " elem : " + varName) {
              buffer.print("list.add(")
              buffer.print(expr)
              buffer.println(");")
            }
            buffer.println("return list;")
          }
        }

        def make_asString_methods(varName: String, typeName: String, expr: String) {
          make_asString(varName, typeName, expr)
          make_asStringIndex(varName, typeName, expr)
          make_asStringList(varName, typeName, expr)
        }
*/
	attr.attributeType match {
	  case t: GaejDateTimeType => make_multi_get_asString_methods(attr.name, "Date", "Util.dateTime2text(elem)", buffer)
	  case t: GaejDateType => make_multi_get_asString_methods(attr.name, "Date", "Util.date2text(elem)", buffer)
	  case t: GaejTimeType => make_multi_get_asString_methods(attr.name, "Date", "Util.time2text(elem)", buffer)
	  case e: GaejEntityType => make_multi_get_asString_methods(id_var_name(attr), id_attr_type_name(attr), "elem", buffer)
	  case p: GaejEntityPartType => make_multi_get_asString_methods(attr.name, java_doc_element_type(attr), "elem.toString()", buffer)
	  case _ => make_multi_get_asString_methods(attr.name, java_element_type(attr), "elem.toString()", buffer)
	}
      }

      if (attr.isId) {
        attr.idPolicy match {
          case SMAutoIdPolicy => make_key_long
          case SMApplicationIdPolicy => make_key_unencoded_string
        }
      } else {
        if (attr.isHasMany) {
          make_multiple
        } else {
          make_single
        }
      }
    }

    def make_setters {
      for (attr <- attributes) {
        make_attribute_setters(attr)
      }
    }

    def make_attribute_setters(attr: GaejAttribute) {
      val attrName = doc_attr_name(attr)
      val varName = doc_var_name(attr)

      def make_key_long {}
      def make_key_unencoded_string {}
      def make_single {}

      def make_multiple {
        def make_set(anAttrName: String, aVarName: String, aTypeName: String) {
          buffer.method("public void set" + anAttrName.capitalize + "(List<" + aTypeName + "> " + aVarName + ")") {
            buffer.println("this." + aVarName + ".clear();")
            buffer.println("this." + aVarName + ".addAll(" + aVarName + ");")
          }
          buffer.println()
        }

	attr.attributeType match {
	  case t: GaejDateTimeType => make_set(attrName, varName, "Date")
	  case e: GaejEntityType => make_set(attrName, id_var_name(attr), id_attr_type_name(attr)) // XXX attrName?
          //	  case e: GaejEntityType => make_set(id_var_name(attr), id_var_name(attr), id_attr_type_name(attr)) // XXX attrName?
          case p: GaejEntityPartType => make_set(attrName, varName, java_doc_element_type(attr))
	  case _ => make_set(attrName, varName, java_element_type(attr))
	}
      }

      if (attr.isId) {
        attr.idPolicy match {
          case SMAutoIdPolicy => make_key_long
          case SMApplicationIdPolicy => make_key_unencoded_string
        }
      } else {
        if (attr.isHasMany) {
          make_multiple
        } else {
          make_single
        }
      }
    }

    def make_entity_id_asString {
      if (!isId) return
      buffer.println()
      buffer.println("public void entity_id_asString(StringBuilder buf) {")
      buffer.indentUp
      buffer.print("buf.append(")
      buffer.print(make_get_string_property(idName))
      buffer.println(");")
      buffer.indentDown
      buffer.println("}")
    }

    def make_entity_title_asXmlString {
      buffer.method("public void entity_title_asXmlString(StringBuilder buf)") {
        getTitleName match {
          case Some(name) => {
            buffer.makeAppendExpr(make_get_xml_string_property(name))
          }
          case None => getNameName match {
            case Some(name) => {
              buffer.makeAppendExpr(make_get_xml_string_property(name))
              if (isId) {
                buffer.makeAppendString("(")
                buffer.makeAppendExpr(make_get_xml_string_property(idName))
                buffer.makeAppendString(")")
              }
            }
            case None => {
              if (isId) {
                buffer.makeAppendExpr(make_get_xml_string_property(idName))
              }
            }
          }
        }
      }
    }

    def make_entity_subtitle_asXmlString {
      buffer.method("public void entity_subtitle_asXmlString(StringBuilder buf)") {
        getSubTitleName match {
          case Some(name) => {
            buffer.makeAppendExpr(make_get_xml_string_property(name))
          }
          case None => {}
        }
      }
    }

    def make_entity_summary_asXmlContent {
      buffer.method("public void entity_summary_asXmlContent(StringBuilder buf)") {
        getSummaryName match {
          case Some(name) => {
            buffer.makeAppendString("<summary>")
            buffer.makeAppendExpr(make_get_xml_string_property(name))
            buffer.makeAppendString("</summary>")
          }
          case None => {}
        }
      }
    }

    def make_entity_category_asXmlContent {
      buffer.method("public void entity_category_asXmlContent(StringBuilder buf)") {
        getCategoryName match {
          case Some(name) => {
            buffer.makeAppendExpr(make_get_xml_string_property(name))
          }
          case None => {}
        }
      }
    }

    def make_entity_author_asXmlContent {
      buffer.method("public void entity_author_asXmlContent(StringBuilder buf)") {
        getAuthorName match {
          case Some(name) => {
            buffer.makeAppendString("<author>")
            buffer.makeAppendExpr(make_get_xml_string_property(name))
            buffer.makeAppendString("</author>")
          }
          case None => {
            buffer.makeFor("MDEntityInfo.MDAuthor author: entity_info.authors") {
              buffer.makeAppendString("<author>")
              buffer.makeAppendString("<name>")
              buffer.makeAppendExpr("author.name")
              buffer.makeAppendString("</name>")
              buffer.makeIf("author.email != null") {
                buffer.makeAppendString("<email>")
                buffer.makeAppendExpr("author.email")
                buffer.makeAppendString("</email>")
              }
              buffer.makeIf("author.uri != null") {
                buffer.makeAppendString("<uri>")
                buffer.makeAppendExpr("author.uri")
                buffer.makeAppendString("</uri>")
              }
              buffer.makeAppendString("</author>")
            }
          }
        }
      }
    }

    def make_entity_icon_asString {
      buffer.method("public void entity_icon_asString(StringBuilder buf)") {
        getIconName match {
          case Some(name) => {
            buffer.makeAppendExpr(make_get_xml_string_property(name))
          }
          case None => {}
        }
      }
    }

    def make_entity_logo_asString {
      buffer.method("public void entity_logo_asString(StringBuilder buf)") {
        getLogoName match {
          case Some(name) => {
            buffer.makeAppendExpr(make_get_xml_string_property(name))
          }
          case None => {}
        }
      }
    }

    def make_entity_link_asXmlContent {
      buffer.method("public void entity_link_asXmlContent(StringBuilder buf)") {
        getLinkName match {
          case Some(name) => {
            buffer.makeAppendExpr(make_get_xml_string_property(name))
          }
          case None => {}
        }
      }
    }

    def make_entity_content_asXmlString {
      buffer.method("public void entity_content_asXmlString(StringBuilder buf)") {
        getContentName match {
          case Some(name) => {
            buffer.makeAppendExpr(make_get_xml_string_property(name))
          }
          case None => {}
        }
      }
    }

    def make_entity_content_asXmlContent {
      buffer.method("public void entity_content_asXmlContent(StringBuilder buf)") {
        getContentName match {
          case Some(name) => {
            val mime = "application/xml"
            buffer.makeAppendString("""<content type="%s">""".format(mime))
            buffer.makeAppendExpr(make_get_xml_string_property(name))
            buffer.makeAppendString("</content>")
          }
          case None => {}
        }
      }
    }

    def make_entity_created {
      buffer.method("public void entity_created()") {
        buffer.makeReturn("entity_info.created")
      }
    }

    def make_entity_updated {
      buffer.method("public void entity_updated()") {
        buffer.makeReturn("entity_info.updated")
      }
    }

    def make_entity_created_asString {
      buffer.println()
      buffer.println("public void entity_created_asString(StringBuilder buf) {")
      buffer.indentUp
      buffer.print("buf.append(")
      buffer.print("entity_info.getCreatedAsString()")
      buffer.println(");")
      buffer.indentDown
      buffer.println("}")
    }

    def make_entity_updated_asString {
      buffer.println()
      buffer.println("public void entity_updated_asString(StringBuilder buf) {")
      buffer.indentUp
      buffer.print("buf.append(")
      buffer.print("entity_info.getUpdatedAsString()")
      buffer.println(");")
      buffer.indentDown
      buffer.println("}")
    }

    def make_entity_asJsonString {
      buffer.println()
      buffer.println("public void entity_asJsonString(StringBuilder buf) {")
      buffer.indentUp
      buffer.makeAppendString("{");
      for (attr <- attributes) {
        buffer.print("build_json_element(\"")
        buffer.print(attr.name)
        buffer.print("\", ")
        if (attr.isHasMany) {
          buffer.print(make_get_string_list_property(attr))
        } else {
          buffer.print(make_get_string_property(attr))
        }
        buffer.println(", buf);")
      }
      buffer.makeAppendString("}");
      buffer.indentDown
      buffer.println("}")
      //
      buffer.println()
      buffer.println("public void entity_asJsonString(Appendable buf) throws IOException {")
      buffer.indentUp
      buffer.makeAppendString("{");
      for (attr <- attributes) {
        buffer.print("build_json_element(\"")
        buffer.print(attr.name)
        buffer.print("\", ")
        if (attr.isHasMany) {
          buffer.print(make_get_string_list_property(attr))
        } else {
          buffer.print(make_get_string_property(attr))
        }
        buffer.println(", buf);")
      }
      buffer.makeAppendString("}");
      buffer.indentDown
      buffer.println("}")
    }

    def make_entity_asCsvString {
      buffer.println()
      buffer.println("public void entity_asCsvString(StringBuilder buf) {")
      buffer.indentUp
      for (attr <- attributes) {
        buffer.print("build_csv_element(")
        if (attr.isHasMany) {
          buffer.print(make_get_string_list_property(attr))
        } else {
          buffer.print(make_get_string_property(attr))
        }
        buffer.println(", buf);")
        if (attr ne attributes.last) {
          buffer.makeAppendString(",")
        }
      }
      buffer.makeAppendln();
      buffer.indentDown
      buffer.println("}")
    }

    def make_entity_rng {
      buffer.println()
      buffer.method("public static String entity_rng()") {
        val rng = <grammar xmlns="http://relaxng.org/ns/structure/1.0"
         xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
         xmlns:s="http://purl.oclc.org/dsdl/schematron"
         xmlns:sm="http://simplemodeling.org/xmlns/SimpleModeler/"
         datatypeLibrary="http://www.w3.org/2001/XMLSchema-datatypes"
         ns={xmlNamespace}>
  <start>
    <choice>
      <ref name={xmlElementName}/>
      <ref name="list_of_members"/>
    </choice>
  </start>
  <define name="list_of_members">
    <element name="list_of_members">
      <zeroOrMore>
        <ref name={xmlElementName}/>
      </zeroOrMore>
    </element>
  </define>
  <define name={xmlElementName}>
    {
    for (attr <- attributes) yield {
      def make_data_element {
        val datatype = attr.attributeType.xmlDatatypeName
    <element name={attr.name}>
      <data type={datatype}/>
    </element>
      }

      attr.multiplicity match {
	case m: GaejOne => make_data_element
	case m: GaejZeroOne => <zeroOne>{make_data_element}</zeroOne>
	case m: GaejOneMore => <oneOrMore>{make_data_element}</oneOrMore>
	case m: GaejZeroMore => <zeroOrMore>{make_data_element}</zeroOrMore>
	case m: GaejRange => <zeroOrMore>{make_data_element}</zeroOrMore>
      }
    }
  }
  </define>
</grammar>
        buffer.makeReturnTextXml(rng)
      }
    }

    def make_entity_rnc {
      buffer.println()
      buffer.method("public static String entity_rnc()") {
        buffer.makeStringBuilderVar
        buffer.makeAppendStringln("default namespace = \"" + xmlNamespace + "\"")
        buffer.makeAppendStringln("namespace a = \"http://relaxng.org/ns/compatibility/annotations/1.0\"")
        buffer.makeAppendStringln("namespace s = \"http://purl.oclc.org/dsdl/schematron\"")
        buffer.makeAppendln();
        buffer.makeAppendStringln("start = " + xmlElementName + " | list_of_members")
        buffer.makeAppendStringln("list_of_members = element list_of_members { " + xmlElementName + "* }")
        buffer.makeAppendStringln(xmlElementName + " = ")
        buffer.makeAppendIndentUp
        buffer.makeAppendStringln("element " + xmlElementName + " {")
        buffer.makeAppendIndentUp
        for (attr <- attributes) {
	  def make_data_element {
            val datatype = attr.attributeType.xmlDatatypeName
            buffer.makeAppendStringln("element " + attr.name + " { xsd: " + datatype + " }")
	  }
	  attr.multiplicity match {
	    case m: GaejOne => //
	    case m: GaejZeroOne => {
	      buffer.indentUp
	      buffer.makeAppendStringln("zeroOne {")
	      buffer.indentUp
	      make_data_element
	      buffer.indentDown
	      buffer.makeAppendStringln("}")
	      buffer.indentDown
	    }
	    case m: GaejOneMore => {
	      buffer.indentUp
	      buffer.makeAppendStringln("oneMore {")
	      buffer.indentUp
	      make_data_element
	      buffer.indentDown
	      buffer.makeAppendStringln("}")
	      buffer.indentDown
	    }
	    case m: GaejZeroMore => {
	      buffer.indentUp
	      buffer.makeAppendStringln("zeroMore {")
	      buffer.indentUp
	      make_data_element
	      buffer.indentDown
	      buffer.makeAppendStringln("}")
	      buffer.indentDown
	    }
	    case m: GaejRange => {
	      buffer.indentUp
	      buffer.makeAppendStringln("zeroMore {")
	      buffer.indentUp
	      make_data_element
	      buffer.indentDown
	      buffer.makeAppendStringln("}")
	      buffer.indentDown
	    }
	  }
        }
        buffer.makeAppendIndentDown
        buffer.makeAppendStringln("}");
        buffer.makeAppendIndentDown
        buffer.makeStringBuilderReturn
      }
    }

    def make_entity_xsd {
      buffer.println()
      buffer.method("public static String entity_xsd()") {
        val xsd = <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            targetNamespace={xmlNamespace}>
  <xsd:element name="list_of_members">
    <xsd:complexType>
      <xsd:sequence>
        <xsd:element minOccurs="0" maxOccurs="unbounded" ref={xmlElementName}/>
      </xsd:sequence>
    </xsd:complexType>
  </xsd:element>
  <xsd:element name={xmlElementName}>
    <xsd:complexType>
      <xsd:sequence>{
        for (attr <- attributes) yield {
          val datatype = "string"
        <xsd:element name={attr.name} type={"xsd:" + datatype}/>
        }
      }        
      </xsd:sequence>
    </xsd:complexType>
  </xsd:element>
</xsd:schema>
        buffer.makeReturnTextXml(xsd)
      }
    }

    def make_build_json_element {
      buffer.println
      buffer.method("private void build_json_element(String name, String value, StringBuilder buf)") {
        buffer.makeAppendString("\"")
        buffer.makeAppendExpr("name")
        buffer.makeAppendString("\":\"")
        buffer.makeAppendExpr("value")
        buffer.makeAppendString("\"")
        buffer.makeAppendString(",")
      }
      buffer.println
      buffer.method("private void build_json_element(String name, List<String> values, StringBuilder buf)") {
        buffer.makeAppendString("\"")
        buffer.makeAppendExpr("name")
        buffer.makeAppendString("\"")
        buffer.makeAppendString(":[")
        buffer.makeFor("String value: values") {
          buffer.makeAppendString("\"")
          buffer.makeAppendExpr("value")
          buffer.makeAppendString("\"")
          buffer.makeAppendString(",")
        }
        buffer.makeAppendString("],")
      }
      buffer.println
      buffer.method("private void build_json_element(String name, String[] values, StringBuilder buf)") {
        buffer.makeAppendString("\"")
        buffer.makeAppendExpr("name")
        buffer.makeAppendString("\"")
        buffer.makeAppendString(":[")
        buffer.makeFor("String value: values") {
          buffer.makeAppendString("\"")
          buffer.makeAppendExpr("value")
          buffer.makeAppendString("\"")
          buffer.makeAppendString(",")
        }
        buffer.makeAppendString("],")
      }
      //
      buffer.println
      buffer.method("private void build_json_element(String name, String value, Appendable buf) throws IOException") {
        buffer.makeAppendString("\"")
        buffer.makeAppendExpr("name")
        buffer.makeAppendString("\":\"")
        buffer.makeAppendExpr("value")
        buffer.makeAppendString("\"")
        buffer.makeAppendString(",")
      }
      buffer.println
      buffer.method("private void build_json_element(String name, List<String> values, Appendable buf) throws IOException") {
        buffer.makeAppendString("\"")
        buffer.makeAppendExpr("name")
        buffer.makeAppendString("\"")
        buffer.makeAppendString(":[")
        buffer.makeFor("String value: values") {
          buffer.makeAppendString("\"")
          buffer.makeAppendExpr("value")
          buffer.makeAppendString("\"")
          buffer.makeAppendString(",")
        }
        buffer.makeAppendString("],")
      }
      buffer.println
      buffer.method("private void build_json_element(String name, String[] values, Appendable buf) throws IOException") {
        buffer.makeAppendString("\"")
        buffer.makeAppendExpr("name")
        buffer.makeAppendString("\"")
        buffer.makeAppendString(":[")
        buffer.makeFor("String value: values") {
          buffer.makeAppendString("\"")
          buffer.makeAppendExpr("value")
          buffer.makeAppendString("\"")
          buffer.makeAppendString(",")
        }
        buffer.makeAppendString("],")
      }
    }

    def make_build_csv_element {
      buffer.println
      buffer.method("private void build_csv_element(String value, StringBuilder buf)") {
        buffer.makeAppendExpr("value")
      }
      buffer.println
      buffer.method("private void build_csv_element(List<String> values, StringBuilder buf)") {
        buffer.makeAppendString("\"")
        buffer.makeIf("values.size() > 0") {
          buffer.makeAppendExpr("values.get(0)")
          buffer.makeFor("int i = 1;i < values.size();i++") {
            buffer.makeAppendString(",")
            buffer.makeAppendExpr("values.get(i)")
          }
        }
        buffer.makeAppendString("\"")
      }
      buffer.println
      buffer.method("private void build_csv_element(String name, String[] values, StringBuilder buf)") {
        buffer.makeAppendString("\"")
        buffer.makeIf("values.length > 0") {
          buffer.makeAppendExpr("values[0]")
          buffer.makeFor("int i = 1;i < values.length;i++") {
            buffer.makeAppendString(",")
            buffer.makeAppendExpr("values[i]")
          }
        }
        buffer.makeAppendString("\"")
      }
    }

    buffer.print("public class ")
    buffer.print(name)
    getBaseObject match {
      case Some(base) => {
        buffer.print(" extends ")
        buffer.print(base.name)
        buffer.print(" {")
      }
      case None => {
        buffer.print(" implements java.io.Serializable {")
      }
    }
    buffer.println()
    buffer.indentUp
    make_attributes
    make_null_constructor
    make_getters
    make_setters
    make_entity_id_asString
    make_entity_title_asXmlString
    make_entity_subtitle_asXmlString
    make_entity_summary_asXmlContent
    make_entity_category_asXmlContent
    make_entity_author_asXmlContent
    make_entity_icon_asString
    make_entity_logo_asString
    make_entity_link_asXmlContent
    make_entity_content_asXmlString
    make_entity_content_asXmlContent
    make_entity_created_asString
    make_entity_updated_asString
    make_entity_xmlString(buffer)
    make_entity_asXmlString(buffer)
    make_entity_asJsonString
    make_entity_asCsvString
    make_entity_rng
    make_entity_rnc
    make_entity_xsd
    make_build_xml_element(buffer)
    make_build_json_element
    make_build_csv_element
    buffer.indentDown
    buffer.println("}")
    buffer.println()
  }

/*
  final def setBaseClass(aBaseClass: GaejEntityType) {
    _baseClass = aBaseClass
  }
*/
}
