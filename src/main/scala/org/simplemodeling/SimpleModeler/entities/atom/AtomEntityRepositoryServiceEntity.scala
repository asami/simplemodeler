package org.simplemodeling.SimpleModeler.entities.atom

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, JavaTextMaker}
import org.simplemodeling.SimpleModeler.entities.gaej._

/*
 * @since   May.  2, 2009
 * @version Oct. 21, 2009
 * @author  ASAMI, Tomoharu
 */
class AtomEntityRepositoryServcieEntity(val gaejService: GaejDomainServiceEntity, val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""
  var basePackageName: String = ""
  def domainServiceName: String = gaejService.name
  def queryPackageName = basePackageName + "." + domainServiceName
  def entities = gaejService.entities

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %atomPackageName%;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import %basePackageName%.*;
import %queryPackageName%.*;

@SuppressWarnings("serial")
public class %serviceName% extends HttpServlet {
  private Date published_dateTime = null;
  private final DateFormat dateTime_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  @Override
  public void init(ServletConfig config) throws ServletException {
	super.init(config);
    TimeZone utc = TimeZone.getTimeZone("UTC");
    dateTime_format.setTimeZone(utc);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    published_dateTime = new Date();
    String[] path = get_path(req);
    if (is_collection_xml(path)) {
      get_collection_xml(path, req, resp);
    } else if (is_member_xml(path)) {
      get_member_xml(path, req, resp);
    } else if (is_collection_rng(path)) {
      get_collection_rng(path, req, resp);
    } else if (is_collection_rnc(path)) {
      get_collection_rnc(path, req, resp);
    } else if (is_collection_xsd(path)) {
      get_collection_xsd(path, req, resp);
    } else if (is_collection_xslt(path)) {
      get_collection_xslt(path, req, resp);
    } else if (is_member_xslt(path)) {
      get_member_xslt(path, req, resp);
    } else if (is_collection_json(path)) {
      get_collection_json(path, req, resp);
    } else if (is_member_json(path)) {
      get_member_json(path, req, resp);
    } else if (is_collection_csv(path)) {
      get_collection_csv(path, req, resp);
    } else if (is_member_csv(path)) {
      get_member_csv(path, req, resp);
    } else if (is_service(path)) {
      get_service(path, req, resp);
    } else if (is_collection(path)) {
      get_collection(path, req, resp);
    } else if (is_member(path)) {
      get_member(path, req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String[] path = get_path(req);
    if (is_collection(path)) {
      post_collection(path, req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }      
  }

  public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String[] path = get_path(req);
    if (is_member(path)) {
      put_member(path, req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String[] path = get_path(req);
    if (is_member(path)) {
      delete_member(path, req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  private String[] get_path(HttpServletRequest req) {
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

  private boolean is_service(String[] path) {
    return path.length == 0;
  }

  private boolean is_collection(String[] path) {
    return path.length == 1;
  }

  private boolean is_member(String[] path) {
    return path.length == 2;
  }

  private boolean is_collection_xml(String[] path) {
    return is_collection(path) && path[path.length - 1].endsWith(".xml");
  }

  private boolean is_member_xml(String[] path) {
    return is_member(path) && path[path.length - 1].endsWith(".xml");
  }

  private boolean is_collection_rng(String[] path) {
    return is_collection(path) && path[path.length - 1].endsWith(".rng");
  }

  private boolean is_collection_rnc(String[] path) {
    return is_collection(path) && path[path.length - 1].endsWith(".rnc");
  }

  private boolean is_collection_xsd(String[] path) {
    return is_collection(path) && path[path.length - 1].endsWith(".xsd");
  }

  private boolean is_collection_xslt(String[] path) {
    return is_collection(path) && path[path.length - 1].endsWith(".xslt");
  }

  private boolean is_member_xslt(String[] path) {
    return is_member(path) && path[path.length - 1].endsWith(".xslt");
  }

  private boolean is_collection_json(String[] path) {
    return is_collection(path) && path[path.length - 1].endsWith(".json");
  }

  private boolean is_member_json(String[] path) {
    return is_member(path) && path[path.length - 1].endsWith(".json");
  }

  private boolean is_collection_csv(String[] path) {
    return is_collection(path) && path[path.length - 1].endsWith(".csv");
  }

  private boolean is_member_csv(String[] path) {
    return is_member(path) && path[path.length - 1].endsWith(".csv");
  }

  private String format_dateTime(Date dateTime) {
    return dateTime_format.format(dateTime);
  }

  private String published_asString() {
    return format_dateTime(published_dateTime);
  }
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val maker = new JavaTextMaker(
      code,
      Map("%atomPackageName%" -> packageName,
          "%basePackageName%" -> basePackageName,
          "%queryPackageName%" -> queryPackageName,
          "%serviceName%" -> name
	))

    def make_get_service {
      def make_collections = {
        for (entity <- entities) yield {
          val collection_href = entity.termName + "/"
          val collection_title = entity.getTitleName match {
            case Some(name) => name
            case None => entity.term
          }

<collection href={collection_href}>
  <atom:title>{collection_title}</atom:title>
  <accept>application/atom+xml;type=entry</accept>
  <categories fixed="yes" />
</collection>
        }
      }

      maker.method("private void get_service(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        val xml = <service xmlns="http://www.w3.org/2007/app" xmlns:atom="http://www.w3.org/2005/Atom">
  <workspace>
    <atom:title>Entity Repository</atom:title>
    {make_collections}
  </workspace>
</service>
//        maker.makeVar("reqUrl", "StringBuffer", "req.getRequestURL()") 2009-05-25
        maker.makeTextXml("xml", xml)
        maker.println("""resp.setContentType("application/atomsvc+xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(xml.toString());""")
      }
    }

    def make_get_collection {
      val make_updated = """%{get_entity_last_updated(entityName)}%"""
      val make_id = """%{"uuid"}%"""
      val make_self_url = "%{req.getRequestURL()}%"
      val make_alternate_url = "%{req.getRequestURL().substring(0, req.getRequestURI().length())}%"
      val make_generator_uri = "http://code.google.com/p/simplemodeler"
      val make_generator_version = "0.1"
      val make_generator_name = "SimpleModeler"
      val make_feed_entries = "%*{make_entries(entityName, req, resp, buf)}%"

      maker.method("private void get_collection(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        val xml = <feed xmlns="http://www.w3.org/2005/Atom">
    <title>Entity Repository</title>
    <updated>{make_updated}</updated>
    <id>{make_id}</id>
    <link rel="self" type="application/atom+xml" href={make_self_url}/>
    <link rel="alternate" type="text/html" href={make_alternate_url}/>
    <generator uri={make_generator_uri} version={make_generator_version}>{make_generator_name}</generator>
    {make_feed_entries}
</feed>
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeTextXml("xml", xml)
        maker.println("""resp.setContentType("application/atom+xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(xml.toString());""")
      }
    }

    def make_entity_last_updated {
      maker.method("private String get_entity_last_updated(String name)") {
        maker.makeIfElse("name == null") {
          maker.println("return dateTime_format.format(new Date());");
        }
        for (entity <- entities) {
          maker.makeElseIfElse("\"" + entity.termName + "\".equals(name)") {
            maker.print("return MEEntityModelInfo.getEntityLastUpdated(\"");
            maker.print(entity.qualifiedName)
            maker.println("\");")
          }
        }
        maker.makeElse {
          maker.println("return dateTime_format.format(new Date());");
        }
      }
    }

    def make_make_entries {
      maker.method("private void make_entries(String name, HttpServletRequest req, HttpServletResponse resp, StringBuilder buf)") {
        maker.makeIfElse("name == null") {
        }
        for (entity <- entities) {
          maker.makeElseIfElse("\"" + entity.termName + "\".equals(name)") {
            maker.print("make_entries_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(req, resp, buf);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_make_entries_entity)
    }

    def make_make_entries_entity(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)

      maker.method("private void make_entries_" + capitalizedTerm + "(HttpServletRequest req, HttpServletResponse resp, StringBuilder buf)") {
        make_query_execute(anEntity)
        maker.print("for (")
        maker.print(gaejDocName)
        maker.println(" doc : docs) {")
        maker.indentUp
        make_entity_entry_by_doc("%*{doc.entity_id_asString(buf);buf.append(\"/\")}%", anEntity)
        maker.indentDown
        maker.println("}")
      }
    }

    def make_query_execute(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)

      maker.makeVar("startIndex", "int", "ServletUtil.getIntegerParameter(req, \"start-index\", 1)")
      maker.makeVar("maxResults", "int", "ServletUtil.getIntegerParameter(req, \"max-results\", 100)")
      maker.makeVar("updatedMin", "Date", "ServletUtil.getDateTimeParameter(req, \"updated-min\")")
      maker.makeVar("updatedMax", "Date", "ServletUtil.getDateTimeParameter(req, \"updated-max\")")
      maker.makeVar("publishedMin", "Date", "ServletUtil.getDateTimeParameter(req, \"published-min\")")
      maker.makeVar("publishedMax", "Date", "ServletUtil.getDateTimeParameter(req, \"published-max\")")
      maker.makeVar("filter", "String", "req.getParameter(\"filter\")")
      maker.makeVar("ordering", "String", "req.getParameter(\"ordering\")")
      maker.makeVar("declareParameters", "String", "req.getParameter(\"declareParameters\")")
      maker.makeVar("service", domainServiceName)
      maker.print(queryName)
      maker.print(" query = service.query")
      maker.print(capitalizedTerm)
      maker.println("();")
      maker.println("query.setStartMax(startIndex, maxResults);")
      maker.println("query.setUpdatedMin(updatedMin);")
      maker.println("query.setUpdatedMax(updatedMax);")
      maker.println("query.setPublishedMin(publishedMin);")
      maker.println("query.setPublishedMax(publishedMax);")
      maker.println("query.setFilter(filter);")
      maker.println("query.setOrdering(ordering);")
      maker.println("query.setDeclareParameters(declareParameters);")
      maker.print("List<")
      maker.print(gaejDocName)
      maker.println("> docs = query.execute();")
    }

    def make_get_member {
      maker.method("private void get_member(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          maker.makeElseIfElse("\"" + entity.termName + "\".equals(entityName)") {
            maker.print("get_member_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_member_entity)
    }

    def make_get_member_entity(anEntity: GaejEntityEntity) {
      val gaejDocName = anEntity.documentName

      maker.method("private void get_member_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("key", "String", "path[1]")
        maker.makeVar("service", domainServiceName)
	maker.makeVar("doc", gaejDocName, "service.read" + gaejContext.entityBaseName(anEntity) + "(key)")
        maker.makeVar("buf", "StringBuilder")
        make_entity_entry_by_doc("%{\".\"}%", anEntity)
        maker.println("""resp.setContentType("application/atom+xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(buf.toString());""")
      }
    }

    def make_entity_entry_by_doc(editUri: String, anEntity: GaejEntityEntity) {
      val entry_id = "%*{doc.entity_id_asString(buf)}%"
      val entry_title = "%*{doc.entity_title_asXmlString(buf)}%"
//      val entry_subtitle = "%*{doc.entity_subtitle_asXmlString(buf)}%"
      val entry_summary = "%*{doc.entity_summary_asXmlContent(buf)}%"
      val entry_category = "%*{doc.entity_category_asXmlContent(buf)}%"
      val entry_author = "%*{doc.entity_author_asXmlContent(buf)}%"
//      val entry_icon = "%*{doc.entity_icon_asString(buf)}%"
//      val entry_logo = "%*{doc.entity_logo_asString(buf)}%"
      val entry_link = "%*{doc.entity_link_asXmlContent(buf)}%"
      val entry_content = "%*{doc.entity_content_asXmlContent(buf)}%"
      val entry_updated = "%*{doc.entity_updated_asString(buf)}%"
      val entry_published = "%*{doc.entity_created_asString(buf)}%"

      val xmlOld = <entry xmlns="http://www.w3.org/2005/Atom">
  <title>{entry_title}</title>
  <id>{entry_id}</id>
  <updated>{entry_updated}</updated>
  <published>{entry_published}</published>
  <link href={editUri} rel="edit"/>
  <content type="xhtml">
    <div xmlns="http://www.w3.org/1999/xhtml">
      %*{{doc.entity_asXmlString(buf)}}%
    </div>
  </content>
</entry>
      val xml = <entry xmlns="http://www.w3.org/2005/Atom">
  <id>{entry_id}</id>
  <title>{entry_title}</title>
  <updated>{entry_updated}</updated>
  <published>{entry_published}</published>
  <link href={editUri} rel="edit"/>
{
  List(entry_summary, entry_category, entry_author, entry_link, entry_content)
}
</entry>
      maker.makeBuildTextXml("buf", xml)
    }

/*
  <content type="application/xml">
      %*{{doc.entity_asXmlString(buf)}}%
  </content>
*/

    def make_post_collection {
      maker.method("private void post_collection(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        val xml = <service xmlns="//www.w3.org/2007/app" xmlns:atom="http://www.w3.org/2005/Atom">
  <workspace>
    <atom:title>Entity Repository</atom:title>
    <collection href={make_collection_url}>
      <atom:title>Entities</atom:title>
      <accept>application/atom+xml;type=entry</accept>
      <categories fixed="yes" scheme={make_scheme_url}>
        {make_categories}
      </categories>
    </collection>
  </workspace>
</service>
        maker.makeVar("reqUrl", "StringBuffer", "req.getRequestURL()")
        maker.makeTextXml("xml", xml)
        maker.println("""resp.setContentType("text/xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(xml.toString());""")
      }
    }

    def make_put_member {
      maker.method("private void put_member(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        val xml = <service xmlns="//www.w3.org/2007/app" xmlns:atom="http://www.w3.org/2005/Atom">
  <workspace>
    <atom:title>Entity Repository</atom:title>
    <collection href={make_collection_url}>
      <atom:title>Entities</atom:title>
      <accept>application/atom+xml;type=entry</accept>
      <categories fixed="yes" scheme={make_scheme_url}>
        {make_categories}
      </categories>
    </collection>
  </workspace>
</service>
        maker.makeVar("reqUrl", "StringBuffer", "req.getRequestURL()")
        maker.makeTextXml("xml", xml)
        maker.println("""resp.setContentType("text/xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(xml.toString());""")
      }
    }

    def make_delete_member {
      maker.method("private void delete_member(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        val xml = <service xmlns="//www.w3.org/2007/app" xmlns:atom="http://www.w3.org/2005/Atom">
  <workspace>
    <atom:title>Entity Repository</atom:title>
    <collection href={make_collection_url}>
      <atom:title>Entities</atom:title>
      <accept>application/atom+xml;type=entry</accept>
      <categories fixed="yes" scheme={make_scheme_url}>
        {make_categories}
      </categories>
    </collection>
  </workspace>
</service>
        maker.makeVar("reqUrl", "StringBuffer", "req.getRequestURL()")
        maker.makeTextXml("xml", xml)
        maker.println("""resp.setContentType("text/xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(xml.toString());""")
      }
    }

    // XML
    def make_get_collection_xml {
      maker.method("private void get_collection_xml(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName + ".xml"
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_collection_xml_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_collection_entity_xml)
    }

    def make_get_collection_entity_xml(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)
//      val ns = "http://simplemodeling.org/xmlns/SimpleModeler/" // XXX magic number
      val ns = anEntity.xmlNamespace
      maker.method("private void get_collection_xml_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        make_query_execute(anEntity)
        maker.makeVar("buf", "StringBuilder")
        maker.makeAppendString("<list_of_members xmlns=\"" + ns + "\">")
        maker.print("for (")
        maker.print(gaejDocName)
        maker.println(" doc : docs) {")
        maker.indentUp
        maker.println("doc.entity_asXmlString(buf);");
        maker.indentDown
        maker.println("}")
        maker.makeAppendString("</list_of_members>")
        maker.println("""resp.setContentType("application/xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(buf.toString());""")
      }
    }

    def make_get_member_xml {
      maker.method("private void get_member_xml(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_member_xml_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_member_entity_xml)
    }

    def make_get_member_entity_xml(anEntity: GaejEntityEntity) {
      val gaejDocName = anEntity.documentName

      maker.method("private void get_member_xml_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("key", "String", "path[1].substring(0, path[1].length() - \".xml\".length());")
        maker.makeVar("service", domainServiceName)
	maker.makeVar("doc", gaejDocName, "service.read" + gaejContext.entityBaseName(anEntity) + "(key)")
        maker.makeVar("buf", "StringBuilder")
        maker.println("doc.entity_asXmlString(buf);");
        maker.println("""resp.setContentType("application/xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(buf.toString());""")
      }
    }

    // RELAX NG
    def make_get_collection_rng {
      maker.method("private void get_collection_rng(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName + ".rng"
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_collection_rng_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_collection_entity_rng)
    }

    def make_get_collection_entity_rng(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)

      maker.method("private void get_collection_rng_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("rng", "String", gaejDocName + ".entity_rng();");
        maker.println("""resp.setContentType("application/rng+xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(rng);""")
      }
    }

    // RELAX NG Compact
    def make_get_collection_rnc {
      maker.method("private void get_collection_rnc(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName + ".rnc"
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_collection_rnc_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_collection_entity_rnc)
    }

    def make_get_collection_entity_rnc(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)

      maker.method("private void get_collection_rnc_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("rnc", "String", gaejDocName + ".entity_rnc();");
        maker.print(gaejDocName)
        maker.println(".entity_rnc();");
        maker.println("""resp.setContentType("application/rnc+xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(rnc);""")
      }
    }

    // W3C XML Schema
    def make_get_collection_xsd {
      maker.method("private void get_collection_xsd(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName + ".xsd"
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_collection_xsd_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_collection_entity_xsd)
    }

    def make_get_collection_entity_xsd(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)

      maker.method("private void get_collection_xsd_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("xsd", "String", gaejDocName + ".entity_xsd();");
        maker.print(gaejDocName)
        maker.println(".entity_xsd();");
        maker.println("""resp.setContentType("application/xsd+xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(xsd);""")
      }
    }

    // XSLT
    def make_get_collection_xslt {
      maker.method("private void get_collection_xslt(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName + ".xslt"
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_collection_xslt_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_collection_entity_xslt)
    }

    def make_get_collection_entity_xslt(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)

      maker.method("private void get_collection_xslt_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("xslt", "String", gaejDocName + ".entity_rng();"); // XXX
        maker.println("""resp.setContentType("application/xslt+xml; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(xslt);""")
      }
    }

    def make_get_member_xslt {
      maker.method("private void get_member_xslt(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_member_xslt_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_member_entity_xslt)
    }

    def make_get_member_entity_xslt(anEntity: GaejEntityEntity) {
      val gaejDocName = anEntity.documentName

      maker.method("private void get_member_xslt_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("key", "String", "path[1].substring(0, path[1].length() - \".xslt\".length());")
        maker.makeVar("service", domainServiceName)
	maker.makeVar("doc", gaejDocName, "service.read" + gaejContext.entityBaseName(anEntity) + "(key)")
        maker.makeVar("buf", "StringBuilder")
//        maker.println("doc.entity_asRngString(buf);"); // XXX
        maker.println("""resp.setContentType("application/xslt; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(buf.toString());""")
      }
    }

    // JSON
    def make_get_collection_json {
      maker.method("private void get_collection_json(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName + ".json"
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_collection_json_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_collection_entity_json)
    }

    def make_get_collection_entity_json(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)

      maker.method("private void get_collection_json_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        make_query_execute(anEntity)
        maker.makeVar("buf", "StringBuilder")
        maker.makeAppendString("[")
        maker.print("for (")
        maker.print(gaejDocName)
        maker.println(" doc : docs) {")
        maker.indentUp
        maker.println("doc.entity_asJsonString(buf);");
        maker.makeAppendString(",")
        maker.indentDown
        maker.println("}")
        maker.makeAppendString("]")
        maker.println("""resp.setContentType("application/json; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(buf.toString());""")
      }
    }

    def make_get_member_json {
      maker.method("private void get_member_json(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_member_json_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_member_entity_json)
    }

    def make_get_member_entity_json(anEntity: GaejEntityEntity) {
      val gaejDocName = anEntity.documentName

      maker.method("private void get_member_json_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("key", "String", "path[1].substring(0, path[1].length() - \".json\".length());")
        maker.makeVar("service", domainServiceName)
	maker.makeVar("doc", gaejDocName, "service.read" + gaejContext.entityBaseName(anEntity) + "(key)")
        maker.makeVar("buf", "StringBuilder")
        maker.println("doc.entity_asJsonString(buf);");
        maker.println("""resp.setContentType("application/json; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(buf.toString());""")
      }
    }

    // CSV
    def make_get_collection_csv {
      maker.method("private void get_collection_csv(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName + ".csv"
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_collection_csv_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_collection_entity_csv)
    }

    def make_get_collection_entity_csv(anEntity: GaejEntityEntity) {
      val capitalizedTerm = gaejContext.entityBaseName(anEntity)
      val gaejDocName = anEntity.documentName
      val queryName = gaejContext.queryName(anEntity)

      maker.method("private void get_collection_csv_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        make_query_execute(anEntity)
        maker.makeVar("buf", "StringBuilder")
        maker.print("for (")
        maker.print(gaejDocName)
        maker.println(" doc : docs) {")
        maker.indentUp
        maker.println("doc.entity_asCsvString(buf);");
        maker.indentDown
        maker.println("}")
        maker.println("""resp.setContentType("application/csv; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(buf.toString());""")
      }
    }

    def make_get_member_csv {
      maker.method("private void get_member_csv(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("entityName", "String", "path[0]")
        maker.makeIfElse("entityName == null") {
        }
        for (entity <- entities) {
          name = entity.termName
          maker.makeElseIfElse("\"" + name + "\".equals(entityName)") {
            maker.print("get_member_csv_")
            maker.print(gaejContext.entityBaseName(entity))
            maker.println("(path, req, resp);")
          }
        }
        maker.makeElse {
        }
      }
      entities.foreach(make_get_member_entity_csv)
    }

    def make_get_member_entity_csv(anEntity: GaejEntityEntity) {
      val gaejDocName = anEntity.documentName

      maker.method("private void get_member_csv_" + gaejContext.entityBaseName(anEntity) + "(String[] path, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException") {
        maker.makeVar("key", "String", "path[1].substring(0, path[1].length() - \".csv\".length());")
        maker.makeVar("service", domainServiceName)
	maker.makeVar("doc", gaejDocName, "service.read" + gaejContext.entityBaseName(anEntity) + "(key)")
        maker.makeVar("buf", "StringBuilder")
        maker.println("doc.entity_asCsvString(buf);");
        maker.println("""resp.setContentType("application/csv; charset=UTF-8");""")
        maker.makeVar("out", "PrintWriter", "resp.getWriter()")
        maker.println("""out.println(buf.toString());""")
      }
    }

    make_get_service
    make_get_collection
    make_entity_last_updated
    make_make_entries
    make_get_member
    make_post_collection
    make_put_member
    make_delete_member
    //
    make_get_collection_xml
    make_get_member_xml
    make_get_collection_rng
    make_get_collection_rnc
    make_get_collection_xsd
    make_get_collection_xslt
    make_get_member_xslt
    make_get_collection_json
    make_get_member_json
    make_get_collection_csv
    make_get_member_csv
    out.append(maker.toString)
    out.flush()
  }

  private def make_categories = {
    for (entity <- entities) yield {
      <atom:category term={entity.term_en}/>
    }
  }

  private def make_scheme_url = {
    "%{reqUrl}%/entities/scheme.rng"
  }

  private def make_collection_url = {
    "%{reqUrl}%/entities/"
  }
}
