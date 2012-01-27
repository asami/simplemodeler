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
class GwtDomainServiceAsyncEntity(val gaejService: GaejDomainServiceEntity, val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

public interface %serviceName% {
%operations%
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val serviceName = name
    val templater = new Templater(
      service,
      Map("%packageName%" -> packageName,
          "%serviceName%" -> serviceName))
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
        buffer.print("public void create")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(documentName)
        buffer.print(" doc, AsyncCallback<")
        buffer.print(documentName)
        buffer.println("> callback);")
      }

      def read_operation {
        buffer.println()
        buffer.print("public void read")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtContext.keyTypeName(anEntity))
        buffer.print(" key, AsyncCallback<")
	buffer.print(documentName)
	buffer.println("> callback);")
      }

      def update_operation {
        buffer.println()
        buffer.print("public void update")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(documentName)
        buffer.println(" doc, AsyncCallback<Void> callback);")
      }

      def delete_operation {
        buffer.println()
        buffer.print("public void delete")
        buffer.print(capitalizedTerm)
        buffer.print("(")
        buffer.print(gwtContext.keyTypeName(anEntity))
        buffer.println(" key, AsyncCallback<Void> callback);")
      }

      def query_operation {
        buffer.println()
        buffer.print("public void query")
        buffer.print(capitalizedTerm)
        buffer.print("(GwtQuery query, AsyncCallback<Collection<")
	buffer.print(documentName)
	buffer.println(">> callback);")
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
