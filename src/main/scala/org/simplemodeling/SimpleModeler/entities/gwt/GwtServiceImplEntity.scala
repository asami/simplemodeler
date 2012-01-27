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
 * @version Apr. 25, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtServiceImplEntity(val gaejService: GaejServiceEntity, val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource

  var packageName: String = ""
  var basePackageName: String = ""
  var clientPackageName: String = ""
  var serviceInterfaceName: String = ""
  def serviceName: String = gaejService.name

  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  private val service = """package %packageName%;

import java.util.*;
import com.google.gwt.user.server.rpc.*;
import %basePackageName%.*;
import %clientPackageName%.*;
  
public class %serviceImplName% extends RemoteServiceServlet implements %serviceInterface% {
  private %serviceName% service;

  public %serviceImplName%() {
    service = new %serviceName%();
  }

%operations%
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val templater = new Templater(
      service,
      Map("%packageName%" -> packageName,
          "%serviceImplName%" -> name,
          "%basePackageName%" -> basePackageName,
          "%clientPackageName%" -> clientPackageName,
          "%serviceInterface%" -> serviceInterfaceName,
          "%serviceName%" -> serviceName
        ))
    templater.replace("%operations%")(make_operations)
    out.append(templater.toString)
    out.flush()
  }

  private def make_operations(buffer: TextBuilder) {
  }
}
