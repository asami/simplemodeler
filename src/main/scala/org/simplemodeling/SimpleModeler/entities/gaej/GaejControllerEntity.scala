package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{UString, JavaTextMaker}
import org.simplemodeling.SimpleModeler.entities.gaej.GaejUtil._

/*
 * @since   Apr. 11, 2009
 * @version Oct. 29, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejControllerEntity(val entity: GaejEntityEntity, val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource

  override def is_Text_Output = true

  val config = gaejContext.entityRepositoryServiceConfig
  val controller_package_name = {
    if (entity.packageName == "") "controller"
    else entity.packageName + ".controller"
  }
  val view_dir = ".." + config.viewUrlPathname(entity)
  val base_name = gaejContext.entityBaseName(entity)
  val controller_path = config.controllerUrlPathname(entity)
  val capitalized_base_name = UString.capitalize(base_name)
  val home_action = "/"
  val create_action = controller_path + "/create"
  val destroy_action = controller_path + "/destroy"
  val edit_action = controller_path + "/edit"
  val index_action = controller_path + "/index"
  val new_action = controller_path + "/new"
  val search_action = controller_path + "/search"
  val show_action = controller_path + "/show"
  val update_action = controller_path + "/update"
  val service_name = config.serviceName(entity)
  val doc_name = entity.documentName
  val id_name = entity.idName
//  val id_name = {
//    entity.attributes.find(_.isId) match {
//      case Some(attr) => attr.name
//      case None => ""
//    }
//  }    
  val create_operation_name = "create" + capitalized_base_name
  val read_operation_name = "read" + capitalized_base_name
  val update_operation_name = "update" + capitalized_base_name
  val delete_operation_name = "delete" + capitalized_base_name
  val query_operation_name = "query" + capitalized_base_name

  private val entity_refs = new ArrayBuffer[(String, String)]

  def setEntityRefs(entityRefs: Seq[(String, String)]) {
    entity_refs ++= entityRefs
  }

  protected final def id_var_name(attr: GaejAttribute) = {
    gaejContext.variableName4RefId(attr)
  }

  protected final def doc_var_name(attr: GaejAttribute) = {
    gaejContext.documentVariableName(attr)
  }

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val controller = """package %controllerPackageName%;

import java.util.*;
import java.math.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import %basePackageName%.*;

@SuppressWarnings("serial")
public class %entityName%Controller extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String action = get_action(req.getPathInfo());
    if ("create".equals(action)) {
    } else if ("destroy".equals(action)) {
    } else if ("edit".equals(action)) {
      edit_action(req, resp);
    } else if ("index".equals(action)) {
      index_action(req, resp);
    } else if ("new".equals(action)) {
      new_action(req, resp);
    } else if ("search".equals(action)) {
      search_action(req, resp);
    } else if ("show".equals(action)) {
      show_action(req, resp);
    } else if ("update".equals(action)) {
    } else if ("list.json".equals(action)) {
      list_json_action(req, resp);
    } else {
      throw new IOException("unknown action = " + action);
    }
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String action = get_action(req.getPathInfo());;
    if ("create".equals(action)) {
      create_action(req, resp);
    } else if ("destroy".equals(action)) {
      destroy_action(req, resp);
    } else if ("edit".equals(action)) {
    } else if ("index".equals(action)) {
    } else if ("new".equals(action)) {
    } else if ("search".equals(action)) {
    } else if ("show".equals(action)) {
    } else if ("update".equals(action)) {
      update_action(req, resp);
    } else if ("list.json".equals(action)) {
    } else {
      throw new IOException("unknown action = " + action);
    }
  }

%create_action_method%
%destroy_action_method%
%edit_action_method%
%index_action_method%
%new_action_method%
%search_action_method%
%show_action_method%
%update_action_method%
%build_part_method%
%list_json_action_method%
%get_action_method%
%home_after_task%
%home_after_create%
%home_after_destroy%
%home_after_update%
%edit_view%
%index_view%
%new_view%
%show_view%
%get_entity_list_url_method%
%get_action_url_method%
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val templater = new JavaTextMaker(
      controller,
      Map("%entityName%" -> entity.name,
          "%basePackageName%" -> entity.packageName,
          "%controllerPackageName%" -> controller_package_name,
	  "%viewDir%" -> view_dir,
          "%homeAction%" -> home_action,
	  "%createAction%" -> create_action,
	  "%destroyAction%" -> destroy_action,
	  "%editAction%" -> edit_action,
	  "%indexAction%" -> index_action,
	  "%newAction%" -> new_action,
	  "%searchAction%" -> search_action,
	  "%showAction%" -> show_action,
	  "%updateAction%" -> update_action
	))
    templater.replace("%create_action_method%")(make_create_action_method)
    templater.replace("%destroy_action_method%")(make_destroy_action_method)
    templater.replace("%edit_action_method%")(make_edit_action_method)
    templater.replace("%index_action_method%")(make_index_action_method)
    templater.replace("%new_action_method%")(make_new_action_method)
    templater.replace("%search_action_method%")(make_search_action_method)
    templater.replace("%show_action_method%")(make_show_action_method)
    templater.replace("%update_action_method%")(make_update_action_method)
    templater.replace("%build_part_method%")(make_build_part_method)
    templater.replace("%list_json_action_method%")(make_list_json_action_method)
    templater.replace("%get_action_method%")(make_get_action_method)
    templater.replace("%home_after_task%")(make_home_after_task)
    templater.replace("%home_after_create%")(make_home_after_create)
    templater.replace("%home_after_destroy%")(make_home_after_destroy)
    templater.replace("%home_after_update%")(make_home_after_update)
    templater.replace("%edit_view%")(make_edit_view_method)
    templater.replace("%index_view%")(make_index_view_method)
    templater.replace("%new_view%")(make_new_view_method)
    templater.replace("%show_view%")(make_show_view_method)
    templater.replace("%get_entity_list_url_method%")(make_get_entity_list_url_method)
    templater.replace("%get_action_url_method%")(make_get_action_url_method)
    out.append(templater.toString)
    out.flush()
  }

  private def make_create_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void create_action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {")
    buffer.indentUp
    make_service_create(buffer)
    make_doc_create(buffer)
    make_set_request_to_doc_id_attribute(buffer)
    make_set_request_to_doc_non_id_attributes(buffer)
    buffer.print("service.")
    buffer.print(create_operation_name) // createGoods
    buffer.println("(doc);")
//    buffer.println("req.getRequestDispatcher(home_after_create()).forward(req, resp);")
    buffer.println("resp.sendRedirect(home_after_create());")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_destroy_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void destroy_action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {")
    buffer.indentUp
    make_service_create(buffer)
    buffer.print("String ")
    buffer.println("key = req.getParameter(\"key\");")
    buffer.print("service.")
    buffer.print(delete_operation_name) // deleteGoods
    buffer.println("(key);")
//    buffer.println("req.getRequestDispatcher(home_after_create()).forward(req, resp);")
    buffer.println("resp.sendRedirect(home_after_destroy());")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_edit_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void edit_action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {")
    buffer.indentUp
    make_service_create(buffer)
    buffer.println("String key = req.getParameter(\"key\");")
    make_read_doc_by_key(buffer)
    make_session_values_doc_actions(buffer)
    buffer.println("req.getRequestDispatcher(edit_view()).forward(req, resp);")
//    buffer.println("resp.sendRedirect(edit_view());");
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_index_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void index_action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {")
    buffer.indentUp
    make_service_create(buffer)
    make_query_docs(buffer)
    make_session_values_docs_actions(buffer)
    buffer.println("req.getRequestDispatcher(index_view()).forward(req, resp);")
//    buffer.println("resp.sendRedirect(index_view());")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_new_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void new_action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {")
    buffer.indentUp
    make_session_values_actions(buffer)
    buffer.println("req.getRequestDispatcher(new_view()).forward(req, resp);")
//    buffer.println("resp.sendRedirect(new_view());");
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_search_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void search_action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {")
    buffer.indentUp
//    buffer.println("req.getRequestDispatcher(home_after_task()).forward(req, resp);")
    buffer.println("resp.sendRedirect(home_after_task());")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_show_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void show_action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {")
    buffer.indentUp
    make_service_create(buffer)
    buffer.println("String key = req.getParameter(\"key\");")
    make_read_doc_by_key(buffer)
    make_session_values_doc_actions(buffer)
    buffer.println("req.getRequestDispatcher(show_view()).forward(req, resp);")
//    buffer.println("resp.sendRedirect(show_view());");
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_update_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void update_action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {")
    buffer.indentUp
    make_service_create(buffer)
    buffer.println("String key = req.getParameter(\"key\");")
    make_read_doc_by_key(buffer)
    make_set_request_to_doc_non_id_attributes(buffer)
    buffer.print("service.")
    buffer.print(update_operation_name) // updateGoods
    buffer.println("(doc);")
//    buffer.println("req.getRequestDispatcher(home_after_update()).forward(req, resp);")
    buffer.println("resp.sendRedirect(home_after_update());")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_list_json_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private void list_json_action(HttpServletRequest req, HttpServletResponse resp) throws IOException {")
    buffer.indentUp
    make_service_create(buffer)
    make_query_docs(buffer)

    buffer.println("PrintWriter out = resp.getWriter();")
    buffer.println("out.print(\"{ \\\\\"identifier\\\\\": \\\\\"name\\\\\", \\\\\"items\\\\\": [\");")
    buffer.print("for (")
    buffer.print(doc_name)
    buffer.println(" doc : docs) {")
    buffer.indentUp
    buffer.println("out.print(\"{ \\\\\"name\\\\\": \\\\\"\");")
    buffer.print("out.print(doc.")
    buffer.print(id_name)
    buffer.println(");");
    buffer.println("out.print(\"\\\\\", \\\\\"label\\\\\": \\\\\"\");");
    buffer.print("out.print(doc.")
    buffer.print(id_name)
    buffer.println(");")
    buffer.println("out.print(\"\\\\\"}\");");
    buffer.println("out.print(\",\");");
    buffer.indentDown
    buffer.println("}");
    buffer.println("out.print(\"]}\");");
    buffer.println("out.flush();");
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_service_create(buffer: JavaTextMaker) {
    buffer.print(service_name) // DSStoreDomainService
    buffer.print(" service = new ")
    buffer.print(service_name) // DSStoreDomainService
    buffer.println("();")
  }

  private def make_doc_create(buffer: JavaTextMaker) {
    buffer.print(doc_name) // DDGoods
    buffer.print(" doc = new ")
    buffer.print(doc_name) // DDGoods
    buffer.println("();")
  }

  private def make_set_request_to_doc_id_attribute(buffer: JavaTextMaker) {
    buffer.print("doc.")
    buffer.print(id_name)
    buffer.print(" = ServletUtil.get")
    buffer.print(entity.idAttr.attributeType.objectTypeName)
    buffer.print("Parameter(req, \"")
    buffer.print(id_name)
    buffer.println("\");")
  }

  private def make_set_request_to_doc_non_id_attributes(buffer: JavaTextMaker) {
    def make_one(attr: GaejAttribute) {
      val varName = gaejContext.variableName(attr)

      def make_servletutil_get(typeName: String) {
	buffer.print("doc.")
	buffer.print(varName)
	buffer.print(" = ServletUtil.get")
        buffer.print(typeName)
        buffer.print("Parameter(req, \"")
	buffer.print(varName)
	buffer.println("\");")
      }

      attr.attributeType match {
        case t: GaejDateTimeType => {
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print(" = Util.string2dateTime(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_date = Util.string2date(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_date\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_time = Util.string2time(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_time\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_now = Util.string2boolean(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_now\"));")
        }
        case t: GaejDateType => make_servletutil_get("Date")
        case t: GaejTimeType => make_servletutil_get("Time")
        case e: GaejEntityType => {
	  buffer.print("doc.")
	  buffer.print(id_var_name(attr))
	  buffer.print(" = ServletUtil.get")
          buffer.print(e.entity.idAttr.elementTypeName)
          buffer.print("Parameter(req, \"")
	  buffer.print(varName) // XXX id_var_name(attr)
	  buffer.println("\");")
        }
        case p: GaejEntityPartType => {
          buffer.print("build_")
          buffer.print(varName)
          buffer.println("(req, doc);")
        }
        case p: GaejPowertypeType => {
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print(" = ServletUtil.get")
          buffer.print("String")
          buffer.print("Parameter(req, \"")
	  buffer.print(varName)
	  buffer.println("\");")
        }
        case t => make_servletutil_get(t.objectTypeName)
      }
    }

    def make_many(attr: GaejAttribute) {
      val varName = gaejContext.variableName(attr)

      def make_servletutil_get(typeName: String) {
	  buffer.print("doc.set")
	  buffer.print(varName.capitalize)
	  buffer.print("(ServletUtil.get")
          buffer.print(typeName)
          buffer.print("ListParameter(req, \"")
	  buffer.print(varName)
	  buffer.println("\"));")
      }

      attr.attributeType match {
        case t: GaejDateTimeType => { // XXX
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print(" = Util.string2dateTime(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_date = Util.string2date(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_date\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_time = Util.string2time(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_time\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_now = Util.string2boolean(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_now\"));")
        }
        case t: GaejDateType => make_servletutil_get("Date")
        case t: GaejTimeType => make_servletutil_get("Time")
        case e: GaejEntityType => {
	  buffer.print("doc.set")
	  buffer.print(id_var_name(attr).capitalize)
	  buffer.print("(ServletUtil.get")
          buffer.print(e.entity.idAttr.elementTypeName)
          buffer.print("ListParameter(req, \"")
	  buffer.print(varName)
	  buffer.println("\"));")
        }
        case p: GaejEntityPartType => {
          buffer.print("build_")
          buffer.print(varName)
          buffer.println("(req, doc);")
        }
        case p: GaejPowertypeType => {
	  buffer.print("doc.set")
	  buffer.print(varName.capitalize)
	  buffer.print("(ServletUtil.get")
          buffer.print("String")
          buffer.print("ListParameter(req, \"")
	  buffer.print(varName)
	  buffer.println("\"));")
        }
        case t => make_servletutil_get(t.objectTypeName)
      }
    }

    def make_datatype(attr: GaejAttribute) {
      val varName = gaejContext.variableName(attr)
      val (kind, defaultValue) = attr.attributeType.getDatatypeName match {
        case Some("boolean") => ("Boolean", "false")
        case Some("byte") => ("Byte", "(byte)0")
        case Some("short") => ("Short", "(short)0")
        case Some("int") => ("Integer", "0")
        case Some("long") => ("Long", "0L")
        case Some("float") => ("Float", "0.0F")
        case Some("double") => ("Double", "0.0D")
        case None => error("???")
      }
      buffer.print("doc.")
      buffer.print(varName)
      buffer.print(" = ServletUtil.get")
      buffer.print(kind)
      buffer.print("Parameter(req, \"")
      buffer.print(varName)
      buffer.print("\", ")
      buffer.print(defaultValue)
      buffer.println(");")
    }

    for (attr <- entity.attributes if !attr.isId) {
      if (attr.isHasMany) {
        make_many(attr)
      } else if (attr.isDataType) {
        make_datatype(attr)
      } else {
        make_one(attr)
      }
    }
  }

  private def make_build_part_method(buffer: JavaTextMaker) {
    def make_part_one(wholeAttr: GaejAttribute, attr: GaejAttribute, counter: String) {
      val varName = doc_var_name(attr)
      val paramNameExpr = if (counter != null) {
        "\"" + doc_var_name(wholeAttr) + "_\" + counter + \"_" + doc_var_name(attr) + "\""
      } else {
        "\"" + doc_var_name(wholeAttr) + "_"  + doc_var_name(attr) + "\""
      }
      def make_servletutil_get(typeName: String) {
	buffer.print(typeName)
	buffer.print(" ")
        buffer.print(varName)
	buffer.print(" = ServletUtil.get")
        buffer.print(typeName)
        buffer.print("Parameter(req, ")
	buffer.print(paramNameExpr)
	buffer.println(");")
      }

      attr.attributeType match {
        case t: GaejDateTimeType => {
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print(" = Util.string2dateTime(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_date = Util.string2date(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_date\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_time = Util.string2time(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_time\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_now = Util.string2boolean(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_now\"));")
        }
        case t: GaejDateType => make_servletutil_get("Date")
        case t: GaejTimeType => make_servletutil_get("Time")
        case e: GaejEntityType => {
	  buffer.print("String ")
	  buffer.print(varName)
	  buffer.print(" = req.getParameter(")
	  buffer.print(paramNameExpr)
	  buffer.println(");")
        }
        case p: GaejEntityPartType => error("???")
        case t => make_servletutil_get(t.objectTypeName)
      }
    }

    def make_part_many(wholeAttr: GaejAttribute, attr: GaejAttribute, counter: String) {
      val varName = doc_var_name(attr)
      val paramNameExpr = if (counter != null) {
        "\"" + doc_var_name(wholeAttr) + "_\" + counter + \"_" + doc_var_name(attr) + "\""
      } else {
        "\"" + doc_var_name(wholeAttr) + "_" + doc_var_name(attr) + "\""
      }

      def make_servletutil_get(typeName: String) {
	buffer.print("List<")
        buffer.print(typeName)
        buffer.print("> ")
        buffer.print(varName)
	buffer.print(" = ServletUtil.get")
        buffer.print(typeName)
        buffer.print("ListParameter(req, ")
	buffer.print(paramNameExpr)
	buffer.println(");")
      }

      attr.attributeType match {
        case t: GaejDateTimeType => { // XXX
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print(" = Util.string2dateTime(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_date = Util.string2date(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_date\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_time = Util.string2time(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_time\"));")
	  buffer.print("doc.")
	  buffer.print(varName)
	  buffer.print("_now = Util.string2boolean(req.getParameter(\"")
	  buffer.print(varName)
	  buffer.println("_now\"));")
        }
        case t: GaejDateType => make_servletutil_get("Date")
        case t: GaejTimeType => make_servletutil_get("Time")
        case e: GaejEntityType => {
	  buffer.print("List<String> ")
	  buffer.print(varName)
	  buffer.print(" = ServletUtil.get")
          buffer.print("String")
          buffer.print("ListParameter(req, ")
	  buffer.print(paramNameExpr)
	  buffer.println(");")
        }
        case p: GaejEntityPartType => {
          buffer.print("build_")
          buffer.print(varName)
          buffer.println("(req, doc);")
        }
        case t => make_servletutil_get(t.objectTypeName)
      }
    }

    def make_body(attr: GaejAttribute, part: GaejEntityPartEntity, counter: String) {
      val wholeAttrName = gaejContext.attributeName(attr)
      for (partAttr <- part.attributes) {
        if (partAttr.isHasMany) {
          make_part_many(attr, partAttr, counter)
        } else {
          make_part_one(attr, partAttr, counter)
        }
      }
      buffer.print("if (")
      for (partAttr <- part.attributes) {
        partAttr.attributeType match {
          case t: GaejDateTimeType => {}
          case _ => {
            if (partAttr != part.attributes(0)) {
              buffer.print(" || ")
            }
            val varName = doc_var_name(partAttr)
            if (partAttr.isHasMany) {
              buffer.print(varName)
              buffer.print(".isEmpty()")
            } else {
              buffer.print(varName)
              buffer.print(" == null")
            }
          }
        }
      }
      buffer.println(") {")
      buffer.indentUp
      buffer.println("return;")
      buffer.indentDown
      buffer.println("}")
      buffer.makeVar("part", part.documentName, "new " + part.documentName+ "()")
      for (partAttr <- part.attributes) {
        partAttr.attributeType match {
          case t: GaejDateTimeType => {}
          case _ => {
            val partAttrName = doc_var_name(partAttr)
            if (partAttr.isHasMany) {
              buffer.print("part.")
              buffer.print(partAttrName)
              buffer.print(".addAll(")
              buffer.print(partAttrName)
              buffer.println(");")
            } else {
              buffer.print("part.")
              buffer.print(partAttrName)
              buffer.print(" = ")
              buffer.print(partAttrName)
              buffer.println(";")
            }
          }
        }
      }
      if (counter != null) {
        buffer.print("doc.")
        buffer.print(wholeAttrName)
        buffer.println(".add(part);")
      } else {
        buffer.print("doc.")
        buffer.print(wholeAttrName)
        buffer.println(" = part;")
      }
    }

    for (attr <- entity.attributes if !attr.isId) {
      val wholeAttrName = gaejContext.attributeName(attr)
      attr.attributeType match {
        case p: GaejEntityPartType => {
          buffer.method("private void build_" + wholeAttrName + "(HttpServletRequest req, " + doc_name + " doc)") {
            if (attr.isHasMany) {
              buffer.makeVar("counter", "int", "0")
              buffer.makeFor(";;") {
                make_body(attr, p.part, "counter")
                buffer.println("counter++;")
              }
            } else {
              make_body(attr, p.part, null)
            }
          }
        }
        case _ => {}
      }
    }
  }

  def make_read_doc_by_key(buffer: JavaTextMaker) {
    buffer.print(doc_name)
    buffer.print(" doc = service.")
    buffer.print(read_operation_name)
    buffer.println("(key);")
  }      

  def make_query_docs(buffer: JavaTextMaker) {
    buffer.println("int startIndex = ServletUtil.getIntegerParameter(req, \"start-index\", 1);")
    buffer.println("int maxResults = ServletUtil.getIntegerParameter(req, \"max-results\", 100);")
    buffer.println("Date updatedMin = ServletUtil.getDateTimeParameter(req, \"updated-min\");")
    buffer.println("Date updatedMax = ServletUtil.getDateTimeParameter(req, \"updated-max\");")
    buffer.println("Date publishedMin = ServletUtil.getDateTimeParameter(req, \"published-min\");")
    buffer.println("Date publishedMax = ServletUtil.getDateTimeParameter(req, \"published-max\");")
    buffer.println("String filter = req.getParameter(\"filter\");")
    buffer.println("String ordering = req.getParameter(\"ordering\");")
    buffer.println("String declareParameters = req.getParameter(\"declareParameters\");")
    buffer.print(service_name)
    buffer.print(".")
    buffer.print(gaejContext.queryName(entity))
    buffer.print(" query = service.")
    buffer.print(query_operation_name)
    buffer.println("();")
    buffer.println("query.setStartMax(startIndex, maxResults);")
    buffer.println("query.setUpdatedMin(updatedMin);")
    buffer.println("query.setUpdatedMax(updatedMax);")
    buffer.println("query.setPublishedMin(publishedMin);")
    buffer.println("query.setPublishedMax(publishedMax);")
    buffer.println("query.setFilter(filter);")
    buffer.println("query.setOrdering(ordering);")
    buffer.println("query.setDeclareParameters(declareParameters);")
    buffer.print("List<")
    buffer.print(doc_name)
    buffer.println("> docs = query.execute();")
  }

  def make_session_values_docs_actions(buffer: JavaTextMaker) {
//    buffer.println("HttpSession session = req.getSession();")
    buffer.println("req.setAttribute(\"docs\", docs);")
    make_action_list_in_session(buffer)
  }

  def make_session_values_doc_actions(buffer: JavaTextMaker) {
//    buffer.println("HttpSession session = req.getSession();")
    buffer.println("req.setAttribute(\"doc\", doc);")
    make_entity_list_in_session(buffer)
    make_action_list_in_session(buffer)
  }

  def make_session_values_actions(buffer: JavaTextMaker) {
//    buffer.println("HttpSession session = req.getSession();")
    make_entity_list_in_session(buffer)
    make_action_list_in_session(buffer)
  }

  def make_entity_list_in_session(buffer: JavaTextMaker) {
    buffer.println("Map<String, String>entityRefs = new HashMap<String, String>();")
    for ((qname, _) <- entity_refs) {
      buffer.print("entityRefs.put(")
      buffer.print("\"")
      buffer.print(qname)
      buffer.print("\", get_entity_list_url(\"")
      buffer.print(qname)
      buffer.print("\")")
      buffer.println(");")
    }
    buffer.println("req.setAttribute(\"entity_refs\", entityRefs);")
  }

  def make_action_list_in_session(buffer: JavaTextMaker) {
    make_set_session_attribute("action_home", "get_action_url(\"home\")", buffer)
    make_set_session_attribute("action_create", "get_action_url(\"create\")", buffer)
    make_set_session_attribute("action_destroy", "get_action_url(\"destroy\")", buffer)
    make_set_session_attribute("action_edit", "get_action_url(\"edit\")", buffer)
    make_set_session_attribute("action_index", "get_action_url(\"index\")", buffer)
    make_set_session_attribute("action_new", "get_action_url(\"new\")", buffer)
    make_set_session_attribute("action_search", "get_action_url(\"search\")", buffer)
    make_set_session_attribute("action_show", "get_action_url(\"show\")", buffer)
    make_set_session_attribute("action_update", "get_action_url(\"update\")", buffer)
  }

  def make_set_session_attribute(key: String, value: String, buffer: JavaTextMaker) {
    buffer.print("req.setAttribute(\"")
    buffer.print(key)
    buffer.print("\", ")
    buffer.print(value)
    buffer.println(");")
  }

  private def make_get_action_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String get_action(String path) {")
    buffer.indentUp
    buffer.println("if (path == null || path.equals(\"\") || path.equals(\"/\")) {")
    buffer.indentUp
    buffer.println("return \"index\";");
    buffer.indentDown
    buffer.println("} else {")
    buffer.indentUp
    buffer.println("return path.split(\"/\")[1];");
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_home_after_task(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String home_after_task() {")
    buffer.indentUp
    buffer.print("return \"")
    buffer.print(index_action)
    buffer.println("\";")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_home_after_create(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String home_after_create() {")
    buffer.indentUp
    buffer.println("return home_after_task();")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_home_after_destroy(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String home_after_destroy() {")
    buffer.indentUp
    buffer.println("return home_after_task();")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_home_after_update(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String home_after_update() {")
    buffer.indentUp
    buffer.println("return home_after_task();")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_edit_view_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String edit_view() {")
    buffer.indentUp
    buffer.print("return \"")
    buffer.print(config.viewUrlPathname(entity))
    buffer.print("/edit")
    buffer.println(".jsp\";")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_index_view_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String index_view() {")
    buffer.indentUp
    buffer.print("return \"")
    buffer.print(config.viewUrlPathname(entity))
    buffer.print("/index")
    buffer.println(".jsp\";")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_new_view_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String new_view() {")
    buffer.indentUp
    buffer.print("return \"")
    buffer.print(config.viewUrlPathname(entity))
    buffer.print("/new")
    buffer.println(".jsp\";")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_show_view_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("protected String show_view() {")
    buffer.indentUp
    buffer.print("return \"")
    buffer.print(config.viewUrlPathname(entity))
    buffer.print("/show")
    buffer.println(".jsp\";")
    buffer.indentDown
    buffer.println("}");
    buffer.indentDown
  }

  private def make_get_entity_list_url_method(buffer: JavaTextMaker) {
    buffer.indentUp
    buffer.println("private String get_entity_list_url(String qname) {")
    buffer.indentUp
    for ((qname, baseName) <- entity_refs) {
      buffer.print("if (qname.equals(")
      buffer.print("\"")
      buffer.print(qname)
      buffer.print("\"")
      buffer.println(")) {")
      buffer.indentUp
      buffer.print("return ")
      buffer.print("\"")
      buffer.print("/")
      buffer.print(baseName)
      buffer.print("/list.json")
      buffer.print("\"")
      buffer.println(";")
      buffer.indentDown
      buffer.println("}")
    }
    buffer.println("return null; // XXX")
    buffer.indentDown
    buffer.println("}")
    buffer.indentDown
  }

  // XXX divies methods
  private def make_get_action_url_method(buffer: JavaTextMaker) {
    def make_if(action: String, url: String) {
      buffer.print("if (action.equals(")
      buffer.print("\"")
      buffer.print(action)
      buffer.print("\"")
      buffer.println(")) {")
      buffer.indentUp
      buffer.print("return ")
      buffer.print("\"")
      buffer.print(url)
      buffer.print("\"")
      buffer.println(";")
      buffer.indentDown
      buffer.println("}")
    }

    buffer.indentUp
    buffer.println("private String get_action_url(String action) {")
    buffer.indentUp
    make_if("home", home_action)
    make_if("create", create_action)
    make_if("destroy", destroy_action)
    make_if("edit", edit_action)
    make_if("index", index_action)
    make_if("new", new_action)
    make_if("search", search_action)
    make_if("show", show_action)
    make_if("update", update_action)
    buffer.println("return null; // XXX")
    buffer.indentDown
    buffer.println("}")
    buffer.indentDown
  }
}
