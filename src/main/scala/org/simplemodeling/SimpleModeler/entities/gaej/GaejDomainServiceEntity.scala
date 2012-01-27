package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, TextBuilder, UJavaString, UString, JavaTextMaker}

/*
 * @since   Apr. 12, 2009
 * @version Oct. 18, 2009
 * @author  ASAMI, Tomoharu
 */
// EntityRepositoryService
class GaejDomainServiceEntity(aContext: GaejEntityContext) extends GaejServiceEntity(aContext) {
  override def is_Text_Output = true

  val entities = new ArrayBuffer[GaejEntityEntity]
  val config = gaejContext.entityRepositoryServiceConfig

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  private val service = """package %packageName%;

import java.io.Serializable;
import java.util.*;
import javax.jdo.*;

public class %serviceName% {
    %contextName% context;

    void setContext(%contextName% context) {
        this.context = context;
    }

    public %contextName% get%contextName%() { // XXX
        if (context == null) {
            %factoryName% factory = %factoryName%.getFactory();
            context = factory.createContext();
        }
        return context;
    }

%operations%
%queries%

    static class Flusher implements Runnable, Serializable {
        private final MEEntityModelInfo info;

        public Flusher(MEEntityModelInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            PersistenceManager pm = Util.getPersistenceManager();
            try {
                MEEntityModelInfo.updateEntityModel(pm, info);
            } finally {
                pm.close();
            }
        }
    }
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val serviceName = gaejContext.entityRepositoryServiceName(packageName)
    val templater = new JavaTextMaker(
      service,
      Map("%packageName%" -> packageName,
          "%serviceName%" -> serviceName,
          "%factoryName%" -> gaejContext.factoryName(packageName),
          "%contextName%" -> gaejContext.contextName(packageName)))
    templater.replace("%operations%")(make_operations)
    templater.replace("%queries%")(make_queries)
    out.append(templater.toString)
    out.flush()
  }

  private def make_operations(buffer: JavaTextMaker) {
    def make_object_crud_operation(anEntity: GaejEntityEntity) {
      val entityName = anEntity.name
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val termName = gaejContext.termName(anEntity)
      val queryName = gaejContext.queryName(anEntity)
      val idName = anEntity.idName
      val idTypeName = anEntity.idAttr.objectTypeName
      val documentName = anEntity.documentName

      def create_operation {
        buffer.println()
        buffer.print("public ")
        buffer.print(documentName)
        buffer.print(" create")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(documentName)
        buffer.println(" doc) {")
        buffer.indentUp
        make_open_persistenceManager
        buffer.print(entityName)
        buffer.print(" entity = new ")
        buffer.print(entityName)
        buffer.println("();")
        buffer.println("entity.init_document(doc, pm);")
        buffer.println("entity.entity_create();")
        buffer.println("entity.make_persistent(pm);")
        buffer.println("create_%s_info(entity);".format(termName))
        buffer.println("return entity.make_document();")
        make_close_persistenceManager
        buffer.indentDown
        buffer.println("}")
      }

      def read_operation {
        buffer.println()
        buffer.print("public ")
        buffer.print(documentName)
        buffer.print(" read")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(idTypeName)
        buffer.println(" key) {")
        buffer.indentUp
        make_open_persistenceManager
        get_entity_block
        buffer.println("return entity.make_document();")
        make_close_persistenceManager
        buffer.indentDown
        buffer.println("}")
        if (idTypeName != "String") {
          buffer.println("public " + documentName + " read" + capitalizedTerm + "(String key) {")
          buffer.indentUp
          buffer.println("return read" + capitalizedTerm + "(Util.string2" + idTypeName + "(key));")
          buffer.indentDown
          buffer.println("}")
        }
      }

      def update_operation {
        buffer.println()
        buffer.print("public void update")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(documentName)
        buffer.println(" doc) {")
        buffer.indentUp
        make_open_persistenceManager
        buffer.print(idTypeName)
        buffer.print(" key = doc.")
        buffer.print(idName)
        buffer.println(";")
        get_entity_block
        buffer.println("entity.update_document(doc, pm);")
        buffer.println("entity.entity_update();")
        buffer.println("entity.make_persistent(pm);")
        buffer.println("update_%s_info(entity);".format(termName))
        make_close_persistenceManager
        buffer.indentDown
        buffer.println("}")
      }

      def delete_operation {
        buffer.println()
        buffer.print("public void delete")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(idTypeName)
        buffer.println(" key) {")
        buffer.indentUp
        make_open_persistenceManager
        delete_entity_block
        make_close_persistenceManager
        buffer.indentDown
        buffer.println("}")
        if (idTypeName != "String") {
          buffer.println("public void delete" + capitalizedTerm + "(String key) {")
          buffer.indentUp
          buffer.println("delete" + capitalizedTerm + "(Util.string2" + idTypeName + "(key));")
          buffer.indentDown
          buffer.println("}")
        }
      }

      def remove_operation {
        buffer.println()
        buffer.print("public void remove")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(idTypeName)
        buffer.println(" key) {")
        buffer.indentUp
        make_open_persistenceManager
        get_entity_block
        buffer.println("entity.entity_remove();")
        buffer.println("entity.make_persistent(pm);")
        buffer.println("remove_%s_info(entity);".format(termName))
        make_close_persistenceManager
        buffer.indentDown
        buffer.println("}")
        if (idTypeName != "String") {
          buffer.println("public void remove" + capitalizedTerm + "(String key) {")
          buffer.indentUp
          buffer.println("remove" + capitalizedTerm + "(Util.string2" + idTypeName + "(key));")
          buffer.indentDown
          buffer.println("}")
        }
      }

      def query_operation {
        buffer.println()
        buffer.print("public ")
        buffer.print(queryName)
        buffer.print(" query")
        buffer.print(capitalizedTerm)
        buffer.println("() {")
        buffer.indentUp
        buffer.print("return new ")
        buffer.print(queryName)
        buffer.println("();")
        buffer.indentDown
        buffer.println("}")
      }

      def make_open_persistenceManager {
        buffer.println("PersistenceManager pm = Util.getPersistenceManager();")
        buffer.indentUp
        buffer.println("try {")
      }

      def make_close_persistenceManager {
        buffer.indentDown
        buffer.println("} finally {")
        buffer.indentUp
        buffer.println("pm.close();")
        buffer.indentDown
        buffer.println("}")
      }

      def get_entity_block {
        buffer.print(entityName)
        buffer.print(" entity = ")
        buffer.print(entityName)
        buffer.println(".get_entity(key, pm);")
      }

      def delete_entity_block {
        buffer.print(entityName)
        buffer.println(".delete_entity(key, pm);")
      }

      def make_lock_word = {
        buffer.makeVar("word", "String", "%s.entity_model.entity.name + \"_lock\"".format(entityName))
      }

      def make_get_context {
          buffer.println("get%s();".format(gaejContext.contextName(packageName)))
      }

      def count_operation {
        buffer.method("long count" + capitalizedTerm + "()") {
          make_lock_word
          make_get_context
          buffer.makeIf("!context.lockRead(word)") {
            buffer.println("throw new IllegalStateException();")
          }
          buffer.makeTry {
            buffer.makeVar("info", "MEEntityModelInfo", "get_%s_info()".format(termName));
            buffer.makeReturn("info.getCount()");
          }
          buffer.makeFinally {
            buffer.println("context.unlockRead(word);")
          }
        }
      }

      def model_info_operations {
        def make_info_action(body: => Unit) = {
          make_get_context
          make_lock_word
          buffer.makeIf("!context.lockWrite(word)") {
            buffer.println("throw new IllegalStateException();")
          }
          buffer.makeTry {
            buffer.makeVar("info", "MEEntityModelInfo", "get_%s_info()".format(termName));
            body
            buffer.println("put_%s_info(info);".format(termName))
          }
          buffer.makeFinally {
            buffer.println("context.unlockWrite(word);")
          }
        }

        buffer.method("private void create_%s_info(%s entity)".format(termName, entityName)) {
          make_info_action {
            buffer.println("info.setUpdated();");
            buffer.println("info.countUp();");
          }            
        }
        buffer.method("private void update_%s_info(%s entity)".format(termName, entityName)) {
          make_info_action {
            buffer.println("info.setUpdated();");
          }
        }
        buffer.method("private void delete_%s_info(%s entity)".format(termName, entityName)) {
          make_info_action {
            buffer.println("info.setUpdated();");
            buffer.println("info.countDown();");
          }
        }
        buffer.method("private void remove_%s_info(%s entity)".format(termName, entityName)) {
          make_info_action {
            buffer.println("info.setUpdated();");
            buffer.println("info.countDown();");
          }
        }
        buffer.method("private MEEntityModelInfo get_%s_info()".format(termName)) {
          make_get_context
          buffer.makeVar("name", "String", "%s.entity_model.entity.name".format(entityName))
          buffer.makeVar("version", "String", "%s.entity_model.entity.version".format(entityName))
          buffer.makeVar("build", "String", "%s.entity_model.entity.build".format(entityName))
          buffer.makeVar("info", "MEEntityModelInfo", "(MEEntityModelInfo)context.getCache(name)");
          buffer.makeIf("info == null") {
            buffer.println("info = MEEntityModelInfo.getEntityModelInfo(name, version, build);")
          }
          buffer.makeReturn("info");
        }
        buffer.method("private void put_%s_info(MEEntityModelInfo info)".format(termName)) {
          make_get_context
          buffer.makeVar("name", "String", "%s.entity_model.entity.name".format(entityName))
          buffer.println("context.putCache(name, info, new Flusher(info));");
        }
      }

 /*
  public long countCustomer() {
	  getStoreContext();
	  String name = DEACustomer.entity_model.entity.name;
	  if (!context.lockRead(name)) {
		  throw new IllegalStateException();
	  }
	  try {
		  MEEntityModelInfo info = get_customer_info();
		  return info.getCount();
	  } finally {
		  context.unlockRead(name);
	  }
  }

  private void create_customer_info(DEACustomer entity) {
	  getStoreContext();
	  String name = DEACustomer.entity_model.entity.name;
	  if (!context.lockRead(name)) {
		  throw new IllegalStateException();
	  }
	  try {
		  MEEntityModelInfo info = get_customer_info();
		  info.setUpdated();
	  } finally {
		  context.unlockRead(name);
	  }
  }

  private void update_customer_info(DEACustomer entity) {
	  getStoreContext();
	  String name = DEACustomer.entity_model.entity.name;
	  if (!context.lockRead(name)) {
		  throw new IllegalStateException();
	  }
	  try {
		  MEEntityModelInfo info = get_customer_info();
		  info.setUpdated();
	  } finally {
		  context.unlockRead(name);
	  }
  }

  private void delete_customer_info(String key) {
	  getStoreContext();
	  String name = DEACustomer.entity_model.entity.name;
	  if (!context.lockRead(name)) {
		  throw new IllegalStateException();
	  }
	  try {
		  MEEntityModelInfo info = get_customer_info();
		  info.setUpdated();
	  } finally {
		  context.unlockRead(name);
	  }
  }

  private void remove_customer_info(String key) {
	  getStoreContext();
	  String name = DEACustomer.entity_model.entity.name;
	  if (!context.lockRead(name)) {
		  throw new IllegalStateException();
	  }
	  try {
		  MEEntityModelInfo info = get_customer_info();
		  info.setUpdated();
	  } finally {
		  context.unlockRead(name);
	  }
  }

  private MEEntityModelInfo get_customer_info() {
	  getStoreContext();
	  String name = DEACustomer.entity_model.entity.name;
	  String version = DEACustomer.entity_model.entity.version;
	  String build = DEACustomer.entity_model.entity.build;
	  MEEntityModelInfo info = (MEEntityModelInfo)context.getCache(name);
	  if (info == null) {
		  info = new MEEntityModelInfo(name, version, build);
		  context.putCache(name, info, new Flusher(info));
	  }
	  return info;
  }
*/


      buffer.indentUp
      create_operation
      read_operation
      update_operation
      delete_operation
      remove_operation
      query_operation
      count_operation
      model_info_operations
      buffer.indentDown
    }

    entities.foreach(make_object_crud_operation)
  }

  private def make_queries(buffer: JavaTextMaker) {
    val query = """
  public class %queryName% {
    private String filter = null;
    private String ordering = null;
    private String declareParameters = null;
    private int fromIncl = -1;
    private int toExcl = -1;
    private Date updatedMin = null;
    private Date updatedMax = null;
    private Date publishedMin = null;
    private Date publishedMax = null;

    public void setFilter(String filter) {
      this.filter = filter;
    }

    public void setOrdering(String ordering) {
      this.ordering = ordering;
    }

    public void setDeclareParameters(String declareParameters) {
      this.declareParameters = declareParameters;
    }

    public void setRange(int fromIncl, int toExcl) {
      this.fromIncl = fromIncl;
      this.toExcl = toExcl;
    }

    public void setUpdatedMin(Date updatedMin) {
      this.updatedMin = updatedMin;
    }

    public void setUpdatedMax(Date updatedMax) {
      this.updatedMax = updatedMax;
    }

    public void setPublishedMin(Date publishedMin) {
      this.publishedMin = publishedMin;
    }

    public void setPublishedMax(Date publishedMax) {
      this.publishedMax = publishedMax;
    }

    public void setStartMax(int startIndex, int maxResults) {
      fromIncl = startIndex - 1;
      toExcl = fromIncl + maxResults;
    }

    public List<%documentName%> execute() {
      PersistenceManager pm = Util.getPersistenceManager();
      try {
        ArrayList<%documentName%> docs = new ArrayList<%documentName%>();
        Query query = pm.newQuery(%entityName%.class);
        if (filter != null) {
          query.setFilter(filter);
        }
        if (filter != null) {
          query.setFilter(filter);
        }
        if (ordering != null) {
          query.setOrdering(ordering);
        }
        if (declareParameters != null) {
          query.declareParameters(declareParameters);
        }
        if (fromIncl != -1) {
          if (toExcl != -1) {
            query.setRange(fromIncl, toExcl);
          } else {
            query.setRange(fromIncl, Long.MAX_VALUE);
          }
        }
        Collection<%entityName%> entities = (Collection<%entityName%>)query.execute();
        for (%entityName% entity: entities) {
          docs.add(entity.make_document());
        }
        return docs;
      } finally {
        pm.close();
      }
    }
  }
"""
    for (entity <- entities) {
      val queryName = gaejContext.queryName(entity)
      buffer.append(query, Map("%queryName%" -> queryName,
                               "%entityName%" -> entity.name,
                               "%documentName%" -> entity.documentName
                             ))
    }
  }
}
