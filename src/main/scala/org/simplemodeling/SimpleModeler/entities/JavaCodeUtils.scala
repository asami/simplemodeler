package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.apache.commons.lang3.StringUtils.isNotBlank
import com.asamioffice.goldenport.text.UJavaString
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov.  8, 2012
 * @version Mar.  7, 2013
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
    if (attr.isDerivedOnTheFly) {
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

  /*
   * Service
   */
  protected final def code_try_ok_failure[T](qname: String, operation: String, param: String)(f: => T) {
    jm_try {
      jm_pln("context.log_enter(\"%s\", \"%s\", %s);", qname, operation, param)
      jm_pln("context.authorizeAll(%s);", _rsc(qname, operation))
      f
      jm_return("context.ok(\"%s\", \"%s\")", qname, operation)
    }
    jm_catch_end("Exception e") {
      jm_pln("context.log_exception(\"%s\", \"%s\", e);", qname, operation)
      jm_return("Response.failure(e)")
    }
  }

  private def _rsc(qname: String, operation: String): String = {
    '"' + qname + "#" + operation + '"'
  }

  protected final def code_request_try_ok_failure[T](qname: String, operation: String, param: String)(f: => T) {
    val rsc = qname + "#" + operation
    jm_try {
      jm_pln("context.log_enter(\"%s\", \"%s\", %s);", qname, operation, param)
      jm_pln("context.authorize(%s, %s.security);", _rsc(qname, operation), param)
      f
      jm_return("context.ok(\"%s\", \"%s\")", qname, operation)
    }
    jm_catch_end("Exception e") {
      jm_pln("context.log_exception(\"%s\", \"%s\", e);", qname, operation)
      jm_return("Response.failure(e)")
    }
  }

  protected final def code_try_return_failure(qname: String, operation: String, param: String)(s: String, args: Any*) {
    val rsc = qname + "#" + operation
    jm_try {
      jm_pln("context.log_enter(\"%s\", \"%s\", %s);", qname, operation, param)
      jm_pln("context.authorizeAll(%s);", _rsc(qname, operation))
      jm_return("context.ok(\"%s\", \"%s\", %s)", qname, operation, s.format(args: _*))
    }
    jm_catch_end("Exception e") {
      jm_pln("context.log_exception(\"%s\", \"%s\", e);", qname, operation)
      jm_return("Response.failure(e)")
    }
  }

  protected final def code_request_try_return_failure(qname: String, operation: String, param: String)(s: String, args: Any*) {
    val rsc = qname + "#" + operation
    jm_try {
      jm_pln("context.log_enter(\"%s\", \"%s\", %s);", qname, operation, param)
      jm_pln("context.authorize(%s, %s.security);", _rsc(qname, operation), param)
      jm_return("context.ok(\"%s\", \"%s\", %s)", qname, operation, s.format(args: _*))
    }
    jm_catch_end("Exception e") {
      jm_pln("context.log_exception(\"%s\", \"%s\", e);", qname, operation)
      jm_return("Response.failure(e)")
    }
  }

  protected final def code_try_block_return_failure[T](qname: String, operation: String, param: String)(f: => T)(s: String, args: Any*) {
    val rsc = qname + "#" + operation
    jm_try {
      jm_pln("context.log_enter(\"%s\", \"%s\", %s);", qname, operation, param)
      jm_pln("context.authorizeAll(%s);", _rsc(qname, operation))
      f
      jm_return("context.ok(\"%s\", \"%s\", %s)", qname, operation, s.format(args: _*))
    }
    jm_catch_end("Exception e") {
      jm_pln("context.log_exception(\"%s\", \"%s\", e);", qname, operation)
      jm_return("Response.failure(e)")
    }
  }

  protected final def code_request_try_block_return_failure[T](qname: String, operation: String, param: String)(f: => T)(s: String, args: Any*) {
    val rsc = qname + "#" + operation
    jm_try {
      jm_pln("context.log_enter(\"%s\", \"%s\", %s);", qname, operation, param)
      jm_pln("context.authorize(%s, %s.security);", _rsc(qname, operation), param)
      f
      jm_return("context.ok(\"%s\", \"%s\", %s)", qname, operation, s.format(args: _*))
    }
    jm_catch_end("Exception e") {
      jm_pln("context.log_exception(\"%s\", \"%s\", e);", qname, operation)
      jm_return("Response.failure(e)")
    }
  }

  // Repository
  protected final def code_method_name_create(entity: PObjectEntity): String = {
    val classname = entity.name
    "create" + entity.name
  }

  protected final def code_method_name_create_tx(entity: PObjectEntity): String = {
    val classname = entity.name
    "create" + entity.name + "Tx"
  }

  protected final def code_method_name_create_id(entity: PObjectEntity): String = {
    val classname = entity.name
    "create" + entity.name + "Id"
  }

  protected final def code_method_name_create_id_tx(entity: PObjectEntity): String = {
    val classname = entity.name
    "create" + entity.name + "IdTx"
  }

  protected final def code_method_name_get(entity: PObjectEntity): String = {
    val classname = entity.name
    "get" + entity.name
  }

  protected final def code_method_name_get_tx(entity: PObjectEntity): String = {
    val classname = entity.name
    "get" + entity.name + "Tx"
  }

  protected final def code_method_name_get_doc(entity: PObjectEntity): String = {
    val classname = entity.name
    "get" + entity.name + "Document"
  }

  protected final def code_method_name_get_doc_tx(entity: PObjectEntity): String = {
    val classname = entity.name
    "get" + entity.name + "DocumentTx"
  }

  protected final def code_method_name_update(entity: PObjectEntity): String = {
    val classname = entity.name
    "update" + entity.name
  }

  protected final def code_method_name_update_tx(entity: PObjectEntity): String = {
    val classname = entity.name
    "update" + entity.name + "Tx"
  }

  protected final def code_method_name_delete(entity: PObjectEntity): String = {
    val classname = entity.name
    "delete" + entity.name
  }

  protected final def code_method_name_delete_tx(entity: PObjectEntity): String = {
    val classname = entity.name
    "delete" + entity.name + "Tx"
  }

  protected final def code_method_name_issue(entity: PObjectEntity): String = {
    val classname = entity.name
    "issue" + entity.name
  }

  protected final def code_method_name_issue_tx(entity: PObjectEntity): String = {
    val classname = entity.name
    "issue" + entity.name + "Tx"
  }

  protected final def code_method_name_issue_id(entity: PObjectEntity): String = {
    val classname = entity.name
    "issue" + entity.name + "Id"
  }

  protected final def code_method_name_issue_id_tx(entity: PObjectEntity): String = {
    val classname = entity.name
    "issue" + entity.name + "IdTx"
  }
}
