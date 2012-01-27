package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entities.gaej._
import com.asamioffice.goldenport.text.{AppendableTextBuilder, TextBuilder, UJavaString, UString, Templater}

/*
 * @since   Apr. 23, 2009
 * @version Apr. 23, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtServiceEntity(val gaejService: GaejServiceEntity, val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""

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
          "%RemoteServiceRelativePath%" -> "service"))
    templater.replace("%operations%")(make_operations)
    out.append(templater.toString)
    out.flush()
  }

  private def make_operations(buffer: TextBuilder) {
  }
}
