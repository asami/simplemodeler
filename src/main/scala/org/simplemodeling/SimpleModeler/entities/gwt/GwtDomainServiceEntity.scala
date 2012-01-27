package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entities.gaej._
import com.asamioffice.goldenport.text.{AppendableTextBuilder, TextBuilder, UJavaString, UString, Templater}

/*
 * @since   Apr. 16, 2009
 * @version Nov. 13, 2009
 * @author  ASAMI, Tomoharu
 */
// GwtEntityRepositoryServiceEntity
class GwtDomainServiceEntity(val gaejService: GaejDomainServiceEntity, val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""
  def entities = gaejService.entities

  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  private val service = """package %packageName%;

import java.util.*;
import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("%RemoteServiceRelativePath%")
public interface %serviceName% extends RemoteService {
%operations%
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val serviceName = name
    val templater = new Templater(
      service,
      Map("%packageName%" -> packageName,
          "%serviceName%" -> serviceName,
          "%RemoteServiceRelativePath%" -> "entity"))
    templater.replace("%operations%")(make_operations)
    out.append(templater.toString)
    out.flush()
  }

  private def make_operations(buffer: TextBuilder) {
    def make_object_crud_operation(anEntity: GaejEntityEntity) {
      val entityName = anEntity.name
      val capitalizedTerm = gwtContext.gaejContext.entityBaseName(anEntity)
      val idName = anEntity.idName
      val documentName = gwtContext.documentName(anEntity)

      def create_operation {
        buffer.println()
        buffer.print("public ")
        buffer.print(documentName)
        buffer.print(" create")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(documentName)
        buffer.println(" doc);")
      }

      def read_operation {
        buffer.println()
        buffer.print("public ")
        buffer.print(documentName)
        buffer.print(" read")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtContext.keyTypeName(anEntity))
        buffer.println(" key);")
      }

      def update_operation {
        buffer.println()
        buffer.print("public void update")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(documentName)
        buffer.println(" doc);")
      }

      def delete_operation {
        buffer.println()
        buffer.print("public void delete")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtContext.keyTypeName(anEntity))
        buffer.println(" key);")
      }

      def query_operation {
        buffer.println()
        buffer.print("public ")
	buffer.print("Collection<")
        buffer.print(documentName)
	buffer.print(">");
        buffer.print(" query")
        buffer.print(capitalizedTerm)
        buffer.println("(GwtQuery query);")
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

      buffer.indentUp
      create_operation
      read_operation
      update_operation
      delete_operation
      query_operation
      buffer.indentDown
    }

    entities.foreach(make_object_crud_operation)
  }
}
