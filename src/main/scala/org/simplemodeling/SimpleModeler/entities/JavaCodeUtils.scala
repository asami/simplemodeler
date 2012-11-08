package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.apache.commons.lang3.StringUtils.isNotBlank
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov.  8, 2012
 * @version Nov.  9, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaCodeUtils {
  self: JavaMakerHolder =>

  val pContext: PEntityContext
  val aspects: Seq[GenericAspect]

  /*
   * Utility methods
   */
  protected final def code_var_name(attr: GenericClassAttributeDefinition): String = {
    code_var_name(attr.attr, attr.varName)
  }

  protected final def code_var_name(attr: PAttribute, varName: String): String = {
    aspects.flatMap(_.objectVarName(attr, varName)).headOption getOrElse varName
  }

  protected final def code_get_method_name(attr: GenericClassAttributeDefinition): String = {
    "get" + code_var_name(attr).capitalize
  }

  // get value internally
  protected final def code_get_value(attr: GenericClassAttributeDefinition): String = {
    if (attr.isDerive) {
      code_get_method_name(attr) + "()"
    } else {
      attr.varName
    }
  }

  protected final def code_single_document_type(e: PEntityType) = {
    require (isNotBlank(e.entity.documentName), "documentName in PEntityType must have value.")
    e.entity.documentName
  }

  protected final def code_multi_document_type(e: PEntityType) = {
    require (isNotBlank(e.entity.documentName), "documentName in PEntityType must have value.")
    "List<" + e.entity.documentName + ">"
  }

  final protected def code_get_string_element(attr: PAttribute): String = {
    if (attr.isHasMany) {
      "build_xml_element(\"" + attr.name + "\", " + code_get_string_list_property(attr) + ", buf)"
    } else {
      "build_xml_element(\"" + attr.name + "\", " + code_get_string_property(attr) + ", buf)"
    }
  }

  final protected def code_get_string_property(name: String) = {
    "get" + name.capitalize + "_asString()"
  }

  final protected def code_get_xml_string_property(name: String) = {
    "Util.escapeXmlText(" + code_get_string_property(name) + ")"
  }

  final protected def code_get_string_property(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => {
        code_get_string_property(pContext.variableName4RefId(attr))
      }
      case _ => code_get_string_property(attr.name)
    }
  }

  final protected def code_get_string_list_property(name: String) = {
    "get" + name.capitalize + "_asStringList()"
  }

  final protected def code_get_string_list_property(attr: PAttribute): String = {
    attr.attributeType match {
      case e: PEntityType => {
        code_get_string_list_property(pContext.variableName4RefId(attr))
      }
      case _ => code_get_string_list_property(attr.name)
    }
  }

  final protected def code_single_datatype_get_asString(attrName: String, expr: String) {
    code_single_datatype_get_asString(attrName, attrName, expr)
  }

  final protected def code_single_datatype_get_asString(attrName: String, varName: String, expr: String) {
    jm_method("public String get" + attrName.capitalize + "_asString()") {
      jm_return(expr)
    }
  }

  final protected def code_single_object_get_asString(attrName: String, expr: String) {
    code_single_object_get_asString(attrName, attrName, expr)
  }

  final protected def code_single_object_get_asString(attrName: String, varName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString()") {
      jm_if_else_null(varName) {
        jm_return("\"\"")
      }
      jm_else {
        jm_return(expr)
      }
    }
  }

  final protected def code_multi_get_asString(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString()") {
      jm_if(varName + ".isEmpty()") {
        jm_return("\"\"")
      }
      jm_var_new_StringBuilder
      jm_var(typeName, "last", varName + ".get(" + varName + ".size() - 1)")
      jm_for(typeName + " elem: " + varName) {
        jm_append_expr(expr)
        jm_if("elem != last") {
          jm_append_String(", ")
        }
      }
      jm_return_StringBuilder
    }
  }

  final protected def code_multi_get_asStringIndex(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("String get" + attrName.capitalize + "_asString(int index)") {
      jm_if_else(varName + ".size() <= index") {
        jm_return("\"\"")
      }
      jm_else {
        jm_var(typeName, "elem", varName + ".get(index)")
        if ("String".equals(typeName)) {
          jm_return(expr)
        } else {
          jm_return("Util.datatype2string(%s)".format(expr))
        }
      }
    }
  }

  final protected def code_multi_get_asStringList(attrName: String, varName: String, typeName: String, expr: String) {
    jm_public_method("List<String> get" + attrName.capitalize + "_asStringList()") {
      jm_var_List_new_ArrayList("String", "list")
      jm_for(typeName + " elem: " + varName) {
        jm_p("list.add(")
        if ("String".equals(typeName)) {
          jm_p(expr)
        } else {
          jm_p("Util.datatype2string(%s)".format(expr))
        }
        jm_pln(");")
      }
      jm_pln("return list;")
    }
  }

  final protected def code_multi_get_asString_methods(attrName: String, typeName: String, expr: String) {
    code_multi_get_asString_methods(attrName, attrName, typeName, expr)
  }

  final protected def code_multi_get_asString_methods(attrName: String, varName: String, typeName: String, expr: String) {
    code_multi_get_asString(attrName, varName, typeName, expr)
    code_multi_get_asStringIndex(attrName, varName, typeName, expr)
    code_multi_get_asStringList(attrName, varName, typeName, expr)
  }

  final protected def code_build_xml_element() {
    jm_private_method("void build_xml_element(String name, String value, StringBuilder buf)") {
      jm_append_String("<")
      jm_append_expr("name")
      jm_append_String(">")
      jm_append_expr("Util.escapeXmlText(value)")
      jm_append_String("</")
      jm_append_expr("name")
      jm_append_String(">")
    }
    jm_private_method("void build_xml_element(String name, List<String> values, StringBuilder buf)") {
      jm_for("String value: values") {
        jm_pln("build_xml_element(name, value, buf);")
      }
    }
    jm_private_method("void build_xml_element(String name, String[] values, StringBuilder buf)") {
      jm_for("String value: values") {
        jm_pln("build_xml_element(name, value, buf);")
      }
    }
  }
}
