package org.simplemodeling.SimpleModeler.entities

import java.io._
import scala.collection.mutable.{ArrayBuffer, HashMap}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{UString, JavaTextMaker}
import org.simplemodeling.SimpleModeler.entities.PUtil._

// derived from GaejServletCode since Sep. 18, 2009.
/*
 * @since   Apr. 23, 2011
 * @version Jul. 24, 2011
 * @author  ASAMI, Tomoharu
 */
abstract class PServletCode(aContext: PEntityContext) extends PObjectCode(aContext) {
  abstract class EntityCoder(val entity: PEntityEntity) extends ServletCoder {
    val base_name = pContext.entityNameBase(entity)
    val doc_name = entity.documentName
    val id_name = entity.idName
    val create_operation_name = "create" + base_name
    val read_operation_name = "read" + base_name
    val update_operation_name = "update" + base_name
    val delete_operation_name = "delete" + base_name
    val query_operation_name = "query" + base_name

    protected final def make_set_request_to_doc_id_attribute() {
      cAppend("doc.")
      cAppend(id_name)
      cAppend(" = ServletUtil.get")
      cAppend(entity.idAttr.attributeType.objectTypeName)
      cAppend("Parameter(req, \"")
      cAppend(id_name)
      cAppendln("\");")
    }

    protected final def make_set_request_to_doc_non_id_attributes() {
      for (attr <- entity.attributes if !attr.isId) {
        make_set_request_to_doc_attribute(attr)
      }
    }

    protected final def make_build_part_method() {
      def make_part_one(wholeAttr: PAttribute, attr: PAttribute, counter: String) {
        val varName = doc_var_name(attr)
        val paramNameExpr = if (counter != null) {
          "\"" + doc_var_name(wholeAttr) + "_\" + counter + \"_" + doc_var_name(attr) + "\""
        } else {
          "\"" + doc_var_name(wholeAttr) + "_"  + doc_var_name(attr) + "\""
        }

        def make_servletutil_get(typeName: String) {
	  cAppend(typeName)
	  cAppend(" ")
          cAppend(varName)
	  cAppend(" = ServletUtil.get")
          cAppend(typeName)
          cAppend("Parameter(req, ")
	  cAppend(paramNameExpr)
	  cAppendln(");")
        }

        attr.attributeType match {
          case t: PDateTimeType => { // XXX
	    cAppend("doc.")
	    cAppend(attr.name)
	    cAppend(" = Util.string2dateTime(req.getParameter(\"")
	    cAppend(attr.name)
	    cAppendln("\"));")
	    cAppend("doc.")
	    cAppend(attr.name)
	    cAppend("_date = Util.string2date(req.getParameter(\"")
	    cAppend(attr.name)
	    cAppendln("_date\"));")
	    cAppend("doc.")
	    cAppend(attr.name)
	    cAppend("_time = Util.string2time(req.getParameter(\"")
	    cAppend(attr.name)
	    cAppendln("_time\"));")
	    cAppend("doc.")
	    cAppend(attr.name)
	    cAppend("_now = Util.string2boolean(req.getParameter(\"")
	    cAppend(attr.name)
	    cAppendln("_now\"));")
          }
          case t: PDateType => make_servletutil_get("Date")
          case t: PTimeType => make_servletutil_get("Time")
          case e: PEntityType => {
	    cAppend("String ")
	    cAppend(varName)
	    cAppend(" = req.getParameter(")
	    cAppend(paramNameExpr)
	    cAppendln(");")
          }
          case p: PEntityPartType => error("???")
          case p: PPowertypeType => error("???")
          case t => make_servletutil_get(t.objectTypeName)
        }
      }

      def make_part_many(wholeAttr: PAttribute, attr: PAttribute, counter: String) {
        val varName = doc_var_name(attr)
        val paramNameExpr = if (counter != null) {
          "\"" + doc_var_name(wholeAttr) + "_\" + counter + \"_" + doc_var_name(attr) + "\""
        } else {
          "\"" + doc_var_name(wholeAttr) + "_" + doc_var_name(attr) + "\""
        }

        def make_servletutil_get(typeName: String) {
	  cAppend("List<")
          cAppend(typeName)
          cAppend("> ")
          cAppend(varName)
	  cAppend(" = ServletUtil.get")
          cAppend(typeName)
          cAppend("ListParameter(req, ")
	  cAppend(paramNameExpr)
	  cAppendln(");")
        }

        attr.attributeType match {
          case t: PDateTimeType => { // XXX
	    cAppend("doc.")
	    cAppend(attr.name)
	    cAppend(" = Util.string2dateTime(req.getParameter(\"")
	    cAppend(attr.name)
	    cAppendln("\"));")
	    cAppend("doc.")
	    cAppend(attr.name)
	    cAppend("_date = Util.string2date(req.getParameter(\"")
	    cAppend(attr.name)
	    cAppendln("_date\"));")
	    cAppend("doc.")
	    cAppend(attr.name)
	    cAppend("_time = Util.string2time(req.getParameter(\"")
	    cAppend(attr.name)
	    cAppendln("_time\"));")
	    cAppend("doc.")
	    cAppend(attr.name)
	    cAppend("_now = Util.string2boolean(req.getParameter(\"")
	    cAppend(attr.name)
	    cAppendln("_now\"));")
          }
          case t: PDateType => make_servletutil_get("Date")
          case t: PTimeType => make_servletutil_get("Time")
          case e: PEntityType => {
	    cAppend("List<String> ")
	    cAppend(varName)
	    cAppend(" = ServletUtil.get")
            cAppend("String")
            cAppend("ListParameter(req, ")
	    cAppend(paramNameExpr)
	    cAppendln(");")
          }
          case p: PEntityPartType => {
            cAppend("build_")
            cAppend(attr.name)
            cAppendln("(req, doc);")
          }
          case p: PPowertypeType => {
	    cAppend("List<String> ")
	    cAppend(varName)
	    cAppend(" = ServletUtil.get")
            cAppend("String")
            cAppend("ListParameter(req, ")
	    cAppend(paramNameExpr)
	    cAppendln(");")
          }
          case t => make_servletutil_get(t.objectTypeName)
        }
      }

      def make_body(attr: PAttribute, part: PEntityPartEntity, counter: String) {
        val wholeAttrName = attr.name
        for (partAttr <- part.attributes) {
          if (partAttr.isHasMany) {
            make_part_many(attr, partAttr, counter)
          } else {
            make_part_one(attr, partAttr, counter)
          }
        }
        cAppend("if (")
        for (partAttr <- part.attributes) {
          partAttr.attributeType match {
            case t: PDateTimeType => {}
            case _ => {
              if (partAttr != part.attributes(0)) {
                cAppend(" || ")
              }
              val varName = doc_var_name(partAttr)
              if (partAttr.isHasMany) {
                cAppend(varName)
                cAppend(".isEmpty()")
              } else {
                cAppend(varName)
                cAppend(" == null")
              }
            }
          }
        }
        cAppendln(") {")
        cIndentUp
        cAppendln("return;")
        cIndentDown
        cAppendln("}")
        cVar("part", part.documentName, "new " + part.documentName+ "()")
        for (partAttr <- part.attributes) {
          partAttr.attributeType match {
            case t: PDateTimeType => {}
            case _ => {
              val partAttrName = doc_var_name(partAttr)
              if (partAttr.isHasMany) {
                cAppend("part.")
                cAppend(partAttrName)
                cAppend(".addAll(")
                cAppend(partAttrName)
                cAppendln(");")
              } else {
                cAppend("part.")
                  cAppend(partAttrName)
                cAppend(" = ")
                cAppend(partAttrName)
                cAppendln(";")
              }
            }
          }
        }
        if (counter != null) {
          cAppend("doc.")
          cAppend(wholeAttrName)
          cAppendln(".add(part);")
        } else {
          cAppend("doc.")
          cAppend(wholeAttrName)
          cAppendln(" = part;")
        }
      }
      
      for (attr <- entity.attributes if !attr.isId) {
        val wholeAttrName = attr.name
        attr.attributeType match {
          case p: PEntityPartType => {
            cMethod("private void build_" + wholeAttrName + "(HttpServletRequest req, " + doc_name + " doc)") {
              if (attr.isHasMany) {
                cVar("counter", "int", "0")
                cFor(";;") {
                  make_body(attr, p.part, "counter")
                  cAppendln("counter++;")
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

    protected final def make_get_entity_id() {
      cAppendln("String id = req.getParameter(\"id\");")
    }

    protected final def make_doc_create() {
      cAppend(doc_name)
      cAppend(" doc = new ")
      cAppend(doc_name)
      cAppendln("();")
    }

    protected def make_read_doc_by_id() {
      cAppend(doc_name)
      cAppend(" doc = service.")
      cAppend(read_operation_name)
      cAppendln("(id);")
    }      

    protected def make_query_docs() {
      cAppendln("int startIndex = ServletUtil.getIntegerParameter(req, \"start-index\", 1);")
      cAppendln("int maxResults = ServletUtil.getIntegerParameter(req, \"max-results\", 100);")
      cAppendln("Date updatedMin = ServletUtil.getDateTimeParameter(req, \"updated-min\");")
      cAppendln("Date updatedMax = ServletUtil.getDateTimeParameter(req, \"updated-max\");")
      cAppendln("Date publishedMin = ServletUtil.getDateTimeParameter(req, \"published-min\");")
      cAppendln("Date publishedMax = ServletUtil.getDateTimeParameter(req, \"published-max\");")
      cAppendln("String filter = req.getParameter(\"filter\");")
      cAppendln("String ordering = req.getParameter(\"ordering\");")
      cAppendln("String declareParameters = req.getParameter(\"declareParameters\");")
      cAppend(pContext.queryName(entity))
      cAppend(" query = repository.")
      cAppend(query_operation_name)
      cAppendln("();")
      cAppendln("query.setStartMax(startIndex, maxResults);")
      cAppendln("query.setUpdatedMin(updatedMin);")
      cAppendln("query.setUpdatedMax(updatedMax);")
      cAppendln("query.setPublishedMin(publishedMin);")
      cAppendln("query.setPublishedMax(publishedMax);")
      cAppendln("query.setFilter(filter);")
      cAppendln("query.setOrdering(ordering);")
      cAppendln("query.setDeclareParameters(declareParameters);")
      cAppend("List<")
      cAppend(doc_name)
      cAppendln("> docs = query.execute();")
    }

    protected def make_set_session_attribute(key: String, value: String) {
      cAppend("req.setAttribute(\"")
      cAppend(key)
      cAppend("\", ")
      cAppend(value)
      cAppendln(");")
    }
  }

  abstract class ServletCoder extends Coder {
    protected final def make_set_request_to_doc_attributes(attributes: Seq[PAttribute]) {
      attributes.foreach(make_set_request_to_doc_attribute)
    }

    protected final def make_set_request_to_doc_attribute(attr: PAttribute) {
      def make_one(attr: PAttribute) {
        val attrName = doc_attr_name(attr)
        val varName = doc_var_name(attr)

        def make_servletutil_get(typeName: String) {
	  cAppend("doc.")
	  cAppend(varName)
	  cAppend(" = ServletUtil.get")
          cAppend(typeName)
          cAppend("Parameter(req, \"")
	  cAppend(varName)
	  cAppendln("\");")
        }

        attr.attributeType match {
          case t: PDateTimeType => {
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend(" = Util.string2dateTime(req.getParameter(\"")
	    cAppend(varName)
	    cAppendln("\"));")
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend("_date = Util.string2date(req.getParameter(\"")
	    cAppend(varName)
	    cAppendln("_date\"));")
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend("_time = Util.string2time(req.getParameter(\"")
	    cAppend(varName)
	    cAppendln("_time\"));")
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend("_now = Util.string2boolean(req.getParameter(\"")
	    cAppend(varName)
	    cAppendln("_now\"));")
          }
          case t: PDateType => make_servletutil_get("Date")
          case t: PTimeType => make_servletutil_get("Time")
          case e: PEntityType => {
	    cAppend("doc.")
	    cAppend(id_var_name(attr))
	    cAppend(" = ServletUtil.get")
            cAppend(e.entity.idAttr.elementTypeName)
            cAppend("Parameter(req, \"")
	    cAppend(varName)
	    cAppendln("\");")
          }
          case p: PEntityPartType => {
            cAppend("build_")
            cAppend(varName)
            cAppendln("(req, doc);")
          }
          case p: PPowertypeType => {
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend(" = ServletUtil.get")
            cAppend("String")
            cAppend("Parameter(req, \"")
	    cAppend(varName)
	    cAppendln("\");")
          }
          case t => make_servletutil_get(t.objectTypeName)
        }
      }

      def make_many(attr: PAttribute) {
        val attrName = doc_attr_name(attr)
        val varName = doc_var_name(attr)

        def make_servletutil_get(typeName: String) {
	  cAppend("doc.set")
	  cAppend(attrName.capitalize)
	  cAppend("(ServletUtil.get")
          cAppend(typeName)
          cAppend("ListParameter(req, \"")
	  cAppend(varName)
	  cAppendln("\"));")
        }

        attr.attributeType match {
          case t: PDateTimeType => { // XXX
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend(" = Util.string2dateTime(req.getParameter(\"")
	    cAppend(varName)
	    cAppendln("\"));")
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend("_date = Util.string2date(req.getParameter(\"")
	    cAppend(varName)
	    cAppendln("_date\"));")
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend("_time = Util.string2time(req.getParameter(\"")
	    cAppend(varName)
	    cAppendln("_time\"));")
	    cAppend("doc.")
	    cAppend(varName)
	    cAppend("_now = Util.string2boolean(req.getParameter(\"")
	    cAppend(varName)
	    cAppendln("_now\"));")
          }
          case t: PDateType => make_servletutil_get("Date")
          case t: PTimeType => make_servletutil_get("Time")
          case e: PEntityType => {
	    cAppend("doc.set")
	    cAppend(id_var_name(attr).capitalize)
	    cAppend("(ServletUtil.get")
            cAppend(e.entity.idAttr.elementTypeName)
            cAppend("ListParameter(req, \"")
	    cAppend(varName)
	    cAppendln("\"));")
          }
          case p: PEntityPartType => {
            cAppend("build_")
            cAppend(varName)
            cAppendln("(req, doc);")
          }
          case p: PPowertypeType => {
	    cAppend("doc.set")
	    cAppend(varName)
	    cAppend("(ServletUtil.get")
            cAppend("String")
            cAppend("ListParameter(req, \"")
	    cAppend(varName)
	    cAppendln("\"));")
          }
          case t => make_servletutil_get(t.objectTypeName)
        }
      }

      def make_datatype(attr: PAttribute) {
        val attrName = doc_attr_name(attr)
        val varName = doc_var_name(attr)

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
        cAppend("doc.")
        cAppend(varName)
        cAppend(" = ServletUtil.get")
        cAppend(kind)
        cAppend("Parameter(req, \"")
        cAppend(varName)
        cAppend("\", ")
        cAppend(defaultValue)
        cAppendln(");")
      }

      if (attr.isHasMany) {
        make_many(attr)
      } else if (attr.isDataType) {
        make_datatype(attr)
      } else {
        make_one(attr)
      }
    }      
  }
}
