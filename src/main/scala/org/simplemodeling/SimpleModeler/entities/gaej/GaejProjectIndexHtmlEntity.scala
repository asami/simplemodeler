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
 * @version Sep.  3, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejProjectIndexHtmlEntity(val services: Seq[GaejServiceEntity], val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  val config = gaejContext.entityRepositoryServiceConfig

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val html = <html>
<head>
  <title>Index</title>
<style>
<!--
.dialogPanel {
  margin: 5px;
}
h1 {background: #b3ada0}
h2 {background: #d4dcd6; border-top: 3px solid #b3ada0}
div.menu {margin-top: 1em; margin-bottom:1em; background-color: #92b5a9}
table.menu {background-color: #92b5a9}
table.menu td.action {background-color: #92b5a9}
table.menu td.selected {background-color: white}
table.datasheet form {display:inline;margin:0 0 0 0;}
table{border-collapse:collapse;border-spacing:0;}
table.datasheet {font-size:inherit;font:100%; border-collapse:separate; border:1px solid #666666; border-left:none;}
table.datasheet {margin-top:0; margin-bottom:.2em}
table.datasheet td {border:1px solid #dedede; padding:2px; font-size:100%; vertical-align:top;}
table.datasheet th {border:1px solid #dedede; padding:2px; background-color:#165e83; color:#FFFFFF; font-weight:bold; font-size:100%; text-align:center}
.datasheetHeader td {border:1px solid #dedede; padding:2px; background-color:#165e83; color:#FFFFFF; font-weight:bold; font-size:100%; text-align:center}
-->
</style>
</head>
<body>
<h1>Index</h1>

<h2>Servlet/JSP/Dojo Toolkit</h2>

{ make_servlet_list }

<h2>Google Web Toolkit</h2>

{ make_gwt_list }

</body>
</html>

  override protected def write_Content(out: BufferedWriter) {
    XML.write(out, html, "UTF-8", true, null)
    out.flush()
  }

  private def make_servlet_list = {
    <ul> {
      for (service <- services) yield {
        service match {
          case domainService: GaejDomainServiceEntity => {
            for (entity <- domainService.entities) yield {
              def url = config.controllerUrlPathname(entity) + "/index"
              def name = "%s (%s)".format(entity.term, entity.name)
              <li><a href={url}>{name}</a></li>
            }
          }
          case _ => //
        }
      }
    } </ul>
  }

  private def make_gwt_list = {
    <ul> {
      for (service <- services) yield {
        service match {
          case domainService: GaejDomainServiceEntity => {
            for (entity <- domainService.entities) yield {
              def url = config.viewUrlPathname(entity) + "/index.html"
              def name = "%s (%s)".format(entity.term, entity.name)
              <li><a href={url}>{name}</a></li>
            }
          }
          case _ => //
        }
      }
    } </ul>
  }
}
