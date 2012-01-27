package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.xml.XML
import scala.xml.dtd.{DocType, PublicID}
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}
import org.simplemodeling.SimpleModeler.entities.gaej._

/*
 * @since   Apr. 18, 2009
 * @version Sep. 23, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtGwtXmlEntity(val gaejServices: Seq[GaejServiceEntity], val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  def packageName = gaejServices(0).packageName

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val gwt = <module rename-to={gwtContext.moduleName(packageName)}>
  <inherits name='com.google.gwt.user.User'/>
  <inherits name='com.google.gwt.user.theme.standard.Standard'/>
  { entry_points }
</module>

  private def entry_points = {
    gaejServices.flatMap {
      _ match {
        case domainService: GaejDomainServiceEntity => {
          for (entity <- domainService.entities) yield {
            <entry-point class={gwtContext.editorClassName(entity)}/>
          }
        }
        case _ => {
//        <entry-point class={gwtContext.operatorClassName(gaejService)}/>
          Nil
        }
      }
    }
  }

  override protected def write_Content(out: BufferedWriter) {
    XML.write(out, gwt, "UTF-8", true, null)
//              DocType("module",
//                      PublicID("-//Google Inc.//DTD Google Web Toolkit 1.6.4//EN",
//                               "http://google-web-toolkit.googlecode.com/svn/tags/1.6.4/distro-source/core/src/gwt-module.dtd")))
    out.flush()
  }
}
