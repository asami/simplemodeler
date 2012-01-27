package org.simplemodeling.SimpleModeler.entities.rest

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString, UJavaString}
import org.simplemodeling.SimpleModeler.entities.gaej._

/*
 * @since   Sep. 21, 2009
 * @version Oct. 19, 2009
 * @author  ASAMI, Tomoharu
 */
class RestPlainServcieEntity(val gaejService: GaejServiceEntity, val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""
  var basePackageName: String = ""
  def serviceName: String = gaejService.name
  def factoryName = gaejContext.factoryName(basePackageName)
  def contextName = gaejContext.contextName(basePackageName)
  def eventName = gaejContext.eventName(basePackageName)

  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  override protected def write_Content(out: BufferedWriter) {
    out.append(new RestEntityRepositoryServcieCode(gaejContext).code())
    out.flush()
  }

  class RestEntityRepositoryServcieCode(context: GaejEntityContext) extends GaejServletCode(context) {
    val template = """package %restPackageName%;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskHandle;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import %basePackageName%.*;

@SuppressWarnings("serial")
public class %serviceName% extends HttpServlet {
    %factoryName% factory;

    @Override
    public void init() throws ServletException {
        factory = %factoryName%.getFactory();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        action(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        action(req, resp);
    }

    // (service/)action
    protected String[] get_path(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) {
            return new String[0];
        } else if (path.length() == 0) {
            return new String[0];
        } else if ("/".equals(path)) {
            return new String[0];
        } else if (path.startsWith("/")) {
            return path.substring(1).split("/");
        } else {
            return path.split("/");
        }
    }

    protected String get_action(String[] path) {
        if (path == null || path.length == 0) {
            return null;
        } else {
            return path[0];
        }
    }

%make_action_method%
}
"""
    coder("restPackageName", packageName)
    coder("basePackageName", basePackageName)
    coder("serviceName", name)
    coder("factoryName", factoryName)
    coder("make_action_method", make_action_method)

    private def make_action_method = new RestServletCoder {
      def make_in(in: Option[GaejDocumentType]) = in match {
        case Some(docType) => "doc"
        case None => ""
      }

      def code() {
        cMethod("private void action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
          cAppendln("long startTime = System.currentTimeMillis();")
          make_context_create
          cVar("path", "String[]", "get_path(req)")
          cVar("action", "String", "get_action(path)")
          make_service_create
          cIfElse("\"_system\".equals(action)") {
            cAppendln("context.systemAction(req, resp);")
            cReturn
          }
          cElseIf("action.endsWith(\"-async\")") {
            cAppendln("context.asyncAction(req, resp);")
            cReturn
          }
          cMatchElse(gaejService.operations) {
            operation =>
              val name = operation.name
            "\"%s\".equals(action)".format(name)
          } {
            operation =>
            operation.in match {
              case Some(docType) => {
                cVar("doc", docType.name)
                make_set_request_to_doc_attributes(docType.document.attributes)
              }
              case None => {
              }
            }
            cTry {
              operation.out match {
                case Some(out) => {
                  cVar("result", out.name, "service." + operation.name + "(" + make_in(operation.in) + ")")
                  cAppendln("long endTime = System.currentTimeMillis();")
                  cAppendln("context.completeTask(req, doc.entity_xmlString(), result.entity_xmlString(), startTime, endTime);")
                  cVar("buf", "PrintWriter", "resp.getWriter()")
                  cOutputString("{")
                  cOutputString("  result_code = ")
                  cOutputExpr("context.okResultCode()")
                  cOutputString(",")
                  cOutputString("  result = ")
                  cAppendln("result.entity_asJsonString(buf);")
                  cOutputString("}")
                  cOutputString("\\n")
                }
                case None => {
                  cAppendln("service." + operation.name + "(" + make_in(operation.in) + ")")
                  cAppendln("long endTime = System.currentTimeMillis();")
                  cAppendln("context.completeTask(req, doc.entity_xmlString(), startTime, endTime);")
                  cVar("buf", "PrintWriter", "resp.getWriter()")
                  cOutputString("{")
                  cOutputString("  result_code = ")
                  cOutputExpr("context.okResultCode()")
                  cOutputString("}")
                  cOutputString("\\n")
                }
              }
              cOutputFlush
            }
            cCatchEnd("Exception e") {
              cAppendln("long endTime = System.currentTimeMillis();")
              operation.in match {
                case Some(docType) => {
                  cAppendln("context.exceptionTask(req, doc.entity_xmlString(), e, startTime, endTime);")
                }
                case None => {
                  cAppendln("context.exceptionTask(req, e, startTime, endTime);")
                }
              }
              cAppendln("context.resultException(resp, e);")
            }
          } {
            cAppendln("throw new UnsupportedOperationException(action);")
          }
        }
      }
    }

    abstract class RestServletCoder extends ServletCoder {
      protected final def make_context_create {
        cVar("context", contextName, "factory.createContext()")
      }

      protected final def make_service_create {
        cVar("service", serviceName, "factory.create" + gaejContext.serviceBaseName(gaejService) + "()")
      }
    }
  }
}
