package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.XML
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Apr. 18, 2009
 * @version Apr. 25, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejAppEngineWebXmlEntity(val services: Seq[GaejServiceEntity], val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  def application_name = gaejContext.applicationName

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val web = <appengine-web-app xmlns="http://appengine.google.com/ns/1.0">
  <application>{application_name}</application>
  <version>1</version>
  <system-properties>
    <property name="java.util.logging.config.file" value="WEB-INF/logging.properties"/>
  </system-properties>
</appengine-web-app>

//  <sessions-enabled>true</sessions-enabled>

  override protected def write_Content(out: BufferedWriter) {
    XML.write(out, web, "UTF-8", true, null)
    out.flush()
  }
}
