package org.simplemodeling.SimpleModeler.entities.rest

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker, UString, UJavaString}
import org.simplemodeling.SimpleModeler.entity._
import org.simplemodeling.SimpleModeler.entities.gaej._

/*
 * @since   May.  2, 2009
 * @version Nov.  6, 2009
 * @author  ASAMI, Tomoharu
 */
class RestEntityRepositoryServcieEntity(val gaejService: GaejDomainServiceEntity, val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""
  var basePackageName: String = ""
  def domainServiceName: String = gaejService.name
  def queryPackageName = basePackageName + "." + domainServiceName
  def factoryName = gaejContext.factoryName(basePackageName)
  def contextName = gaejContext.contextName(basePackageName)
  def eventName = gaejContext.eventName(basePackageName)
  def entities = gaejService.entities

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
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskHandle;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import %basePackageName%.*;
import %queryPackageName%.*;

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

    private void action(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        %contextName% context = factory.createContext();
        String[] path = get_path(req);
        String entity = get_entity(path);
        String id = get_id(path);
        String action = get_action(path);
        if ("_system".equals(entity)) {
%set_flush_cache_keys%
            context.systemAction(req, resp);
            return;
        } else if (action.endsWith("-async")) {
            context.asyncAction(req, resp);
            return;
        }
        switch (path.length) {
        case 0:
            action = "entities";
            break;
        case 1:
            action = "list";
            break;
        }
        if ("entities".equals(action)) {
            entities_action(req, resp, context);
        } else if ("index".equals(action) || "list".equals(action)) {
            list_action(entity, req, resp, context);
        } else if ("create".equals(action)) {
            create_action(entity, req, resp, context);
//        } else if ("create-async".equals(action)) {
//            create_async_action(entity, req, resp, context);
        } else if ("prepare-create".equals(action)) {
            prepare_create_action(entity, req, resp, context);
        } else if ("verify-create".equals(action)) {
            verify_create_action(entity, req, resp, context);
        } else if ("read".equals(action)) {
            read_action(entity, id, req, resp, context);
        } else if ("update".equals(action)) {
            update_action(entity, id, req, resp, context);
//        } else if ("update-async".equals(action)) {
//            update_async_action(entity, id, req, resp, context);
        } else if ("prepare-update".equals(action)) {
            prepare_update_action(entity, id, req, resp, context);
        } else if ("verify-update".equals(action)) {
            verify_update_action(entity, id, req, resp, context);
        } else if ("delete".equals(action)) {
            delete_action(entity, id, req, resp, context);
//        } else if ("delete-async".equals(action)) {
//            delete_async_action(entity, id, req, resp, context);
        } else {
            read_action(entity, id, req, resp, context);
//          throw new IOException("unknown action = " + action);
        }
    }

    // (service/)entity/id/action
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

    protected String get_entity(String[] path) {
        if (path == null || path.length < 1) {
            return null;
        } else {
            return path[0];
        }
    }

    protected String get_id(String[] path) {
        if (path == null || path.length < 2) {
            return null;
        } else {
            return path[1];
        }
    }

    protected String get_action(String[] path) {
        if (path == null || path.length == 0) {
            return null;
        } else {
            return path[path.length - 1];
        }
    }

    private void entities_action(HttpServletRequest req, HttpServletResponse resp, %contextName% context) {
        // XXX
    }

%list_action_method%
%create_action_method%
%create_async_action_method%
%prepare_create_action_method%
%verify_create_action_method%
%read_action_method%
%update_action_method%
%update_async_action_method%
%prepare_update_action_method%
%verify_update_action_method%
%delete_action_method%
%delete_async_action_method%
%build_methods%

/*
    @SuppressWarnings("unchecked")
    private void async_action(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
        StringBuffer path = req.getRequestURI();
        int index = path.indexOf("-async");
        path.substring(0, index);
        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions options = url(path.toString());
        String uuid = UUID.randomUUID().toString();
        options = options.taskName(uuid);
        Set<Map.Entry<String, String[]>> entries = req.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                options = options.param(key, value);
            }
        }
        options = options.param("task_queue", queue.getQueueName());
        options = options.param("task_name", uuid);
        TaskHandle task = queue.add(options);
        PrintWriter buf = resp.getWriter();
        buf.append("{");
        buf.append("  result_code = 0,");
        buf.append("  task_queue = \"");
        buf.append(task.getQueueName());
        buf.append("\",");
        buf.append("  task_name = \"");
        buf.append(task.getName());
        buf.append("\"");
        buf.append("}");
        buf.append("\n");
    }
*/
}
"""
    coder("restPackageName", packageName)
    coder("basePackageName", basePackageName)
    coder("queryPackageName", queryPackageName)
    coder("serviceName", name)
    coder("factoryName", factoryName)
    coder("contextName", contextName)
    coder("set_flush_cache_keys", make_set_flush_cache_keys)
    coder("list_action_method", make_list_action_method)
    coder("create_action_method", make_create_action_method)
    coder("create_async_action_method", make_create_async_action_method)
    coder("prepare_create_action_method", make_prepare_create_action_method)
    coder("verify_create_action_method", make_verify_create_action_method)
    coder("read_action_method", make_read_action_method)
    coder("update_action_method", make_update_action_method)
    coder("update_async_action_method", make_update_async_action_method)
    coder("prepare_update_action_method", make_prepare_update_action_method)
    coder("verify_update_action_method", make_verify_update_action_method)
    coder("delete_action_method", make_delete_action_method)
    coder("delete_async_action_method", make_delete_async_action_method)
    coder("build_methods", make_build_methods)

    private def make_set_flush_cache_keys = new RestServletCoder {
      def code() {
        for (entity <- entities) {
          cAppendln("context.setFlushCacheKey(\"%s\");".format(entity.qualifiedName))
        }
      }
    }

    private def make_list_action_method = new RestServletCoder {
      def code() {
        cMethod("private void list_action(String entity, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          make_repository_service_create
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                val docName = entity.documentName
                make_query_docs
                cVar("buf", "PrintWriter", "resp.getWriter()")
                cOutputString("{")
                cOutputString("  result_code = ")
                cOutputExpr("context.okResultCode()")
                cOutputString(",")
                cOutputString("  entities = [")
                cVar("first", "boolean", "true")
                cFor(docName, "doc", "docs") {
                  cIfElse("first") {
                    cAppendln("first = false;")
                  }
                  cElse {
                    cOutputString(",")
                  }
                  cAppendln("doc.entity_asJsonString(buf);")
                }
                cOutputString("  ]")
                cOutputString("}")
                cOutputString("\\n")
                cOutputFlush
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }
    }

    private def make_create_action_method = new RestServletCoder {
      def code() {
        cMethod("private void create_action(String entity, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cAppendln("long startTime = System.currentTimeMillis();")
          make_repository_service_create
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                val docName = entity.documentName
                make_doc_create
                make_set_request_to_doc_id_attribute
                make_set_request_to_doc_non_id_attributes
                cTry {
                  cVar("result", docName, "repository." + create_operation_name + "(doc)")
                  cAppendln("long endTime = System.currentTimeMillis();")
                  cAppendln("context.completeTask(req, doc.entity_xmlString(), result.entity_xmlString(), startTime, endTime);")
                  cVar("buf", "PrintWriter", "resp.getWriter()")
                  cOutputString("{")
                  cOutputString("  result_code = ")
                  cOutputExpr("context.okResultCode()")
                  cOutputString(",")
                  cOutputString("  entity = ")
                  cAppendln("result.entity_asJsonString(buf);")
                  cOutputString("}")
                  cOutputString("\\n")
                }
                cCatchEnd("Exception e") {
                  cAppendln("long endTime = System.currentTimeMillis();")
                  cAppendln("context.exceptionTask(req, doc.entity_xmlString(), e, startTime, endTime);")
                  cAppendln("context.resultException(resp, e);")
                }
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }
    }

    private def make_create_async_action_method = new Coder {
      def code() {
        cMethod("private void create_async_action(String entity, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cAppendln("context.asyncAction(req, resp);")
        }
      }
    }

    private def make_prepare_create_action_method = new Coder {
      def code() {
        cMethod("private void prepare_create_action(String entity, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                val docName = entity.documentName
                cVar("buf", "PrintWriter", "resp.getWriter()")
                cOutputString("{")
                cOutputString("  result_code = ")
                cOutputExpr("context.okResultCode()")
                cOutputString(",")
                cOutputString("  entity = ")
                cOutputString("{}") // XXX
                cOutputString("}")
                cOutputString("\\n")
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }
    }

    private def make_verify_create_action_method = new Coder {
      def code() {
        cMethod("private void verify_create_action(String entity, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                val docName = entity.documentName
                cVar("buf", "PrintWriter", "resp.getWriter()")
                cOutputString("{")
                cOutputString("  result_code = ")
                cOutputExpr("context.okResultCode()")
                cOutputString(",")
                cOutputString("}")
                cOutputString("\\n")
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }
    }

    private def make_read_action_method = new RestServletCoder {
      def code() {
        cMethod("private void read_action(String entity, String id, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          make_repository_service_create
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                val docName = entity.documentName
                cVar("result", docName, "repository." + read_operation_name + "(id)")
                cVar("buf", "PrintWriter", "resp.getWriter()")
                cOutputString("{")
                cOutputString("  result_code = ")
                cOutputExpr("context.okResultCode()")
                cOutputString(",")
                cOutputString("  entity = ")
                cAppendln("result.entity_asJsonString(buf);")
                cOutputString("}")
                cOutputString("\\n")
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }
    }

    private def make_update_action_method = new RestServletCoder {
      def code() {
        cMethod("private void update_action(String entity, String id, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cAppendln("long startTime = System.currentTimeMillis();")
          make_repository_service_create
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                val docName = entity.documentName
                make_doc_create
                cAppend("doc.")
                cAppend(id_name)
                entity.idAttr.typeName match { // XXX use generic type system
                  case "Long" => cAppendln(" = Long.parseLong(id);")
                  case "String" => cAppendln(" = id;")
                }
                make_set_request_to_doc_non_id_attributes
                cTry {
                  cAppendln("repository." + update_operation_name + "(doc);")
                  cAppendln("long endTime = System.currentTimeMillis();")
                  cAppendln("context.completeTask(req, doc.entity_xmlString(), startTime, endTime);")
                  cVar("buf", "PrintWriter", "resp.getWriter()")
                  cOutputString("{")
                  cOutputString("  result_code = ")
                  cOutputExpr("context.okResultCode()")
                  cOutputString(",")
                  cOutputString("}")
                  cOutputString("\\n")
                }
                cCatchEnd("Exception e") {
                  cAppendln("long endTime = System.currentTimeMillis();")
                  cAppendln("context.exceptionTask(req, doc.entity_xmlString(), e, startTime, endTime);")
                  cAppendln("context.resultException(resp, e);")
                }
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }      
    }

    private def make_update_async_action_method = new Coder {
      def code() {
        cMethod("private void update_async_action(String entity, String id, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cAppendln("context.asyncAction(req, resp);")
        }
      }
    }

    private def make_prepare_update_action_method = new Coder {
      def code() {
        cMethod("private void prepare_update_action(String entity, String id, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                val docName = entity.documentName
                cVar("buf", "PrintWriter", "resp.getWriter()")
                cOutputString("{")
                cOutputString("  result_code = ")
                cOutputExpr("context.okResultCode()")
                cOutputString(",")
                cOutputString("  entity = ")
                cOutputString("{}") // XXX
                cOutputString("}")
                cOutputString("\\n")
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }
    }

    private def make_verify_update_action_method = new Coder {
      def code() {
        cMethod("private void verify_update_action(String entity, String id, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                val docName = entity.documentName
                cVar("buf", "PrintWriter", "resp.getWriter()")
                cOutputString("{")
                cOutputString("  result_code = ")
                cOutputExpr("context.okResultCode()")
                cOutputString(",")
                cOutputString("}")
                cOutputString("\\n")
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }
    }

    private def make_delete_action_method = new RestServletCoder {
      def code() {
        cMethod("private void delete_action(String entity, String id, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cAppendln("long startTime = System.currentTimeMillis();")
          make_repository_service_create
          cMatchElse(entities) {
            entity => 
            val term = gaejContext.termName(entity)
            "\"%s\".equals(entity)".format(term)
          } {
            entity => new EntityCoder(entity) {
              def code() {
                cTry {
                  val docName = entity.documentName
                  cAppend("repository.")
                  buffer.print(delete_operation_name)
                  buffer.println("(id);")
                  cAppendln("long endTime = System.currentTimeMillis();")
                  cAppendln("context.completeTask(req, id, startTime, endTime);")
                  buffer.println("context.resultOk(resp);")
                }
                cCatchEnd("Exception e") {
                  cAppendln("long endTime = System.currentTimeMillis();")
                  cAppendln("context.exceptionTask(req, id, e, startTime, endTime);")
                  cAppendln("context.resultException(resp, e);")
                }
              }
            }.coding()
          } {
            cAppendln("throw new IllegalArgumentException(entity); // XXX")
          }
        }
      }
    }

    private def make_delete_async_action_method = new Coder {
      def code() {
        cMethod("private void delete_async_action(String entity, String id, HttpServletRequest req, HttpServletResponse resp, %s context) throws IOException, ServletException".format(contextName)) {
          cAppendln("context.asyncAction(req, resp);")
        }
      }
    }

    private def make_build_methods = new Coder {
      def code() {
        for (entity <- entities) {
          new EntityCoder(entity) {
            def code() {
              make_build_part_method
            }
          }.coding()
        }
      }
    }

    abstract class RestServletCoder extends Coder {
      protected final def make_context_create {
        cVar("context", contextName, "factory.createContext()")
      }

      protected final def make_repository_service_create {
        cVar("repository", domainServiceName, "factory.create" + domainServiceName + "()")
      }
    }
  }
}
