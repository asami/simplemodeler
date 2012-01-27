package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, JavaTextMaker}

/*
 * @since   May. 12, 2009
 * @version Jun. 28, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejMDEntityInfoEntity(val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import %packageName%.MDEntityInfo.MDHistory.MDEvent;

public class MDEntityInfo {
  public Date created = null;
  public Date updated = null;
  public Date removed = null;
  public final List<MDAuthor> authors = new ArrayList<MDAuthor>();
  public MDModel model;
  public MDHistory history = new MDHistory();

  //
  private boolean is_used = false;
  private final DateFormat dateTime_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private final String format_version = "%format_version%";
  private final String generator_version = "%generator_version%";
  private final String generator_build = "%generator_build%";

  public MDEntityInfo() {
    TimeZone utc = TimeZone.getTimeZone("UTC");
    dateTime_format.setTimeZone(utc);
  }

  public void create() {
    MDEvent event = history.event("create");
    append_author(event.author);
    created = event.dateTime;
    is_used = true;
  }

  public void update() {
    MDEvent event = history.event("update");
    append_author(event.author);
    updated = event.dateTime;
    is_used = true;
  }

  public void remove() {
    MDEvent event = history.event("remove");
    append_author(event.author);
    removed = event.dateTime;
    is_used = true;
  }

  private void append_author(MDAuthor author) {
    if (authors.contains(author)) {
      return;
    }
    authors.add(author);
  }

  public Date sync() {
    if (is_used) {
      is_used = false;
      if (removed != null) {
    	return removed;
      } else if (updated != null) {
    	return updated;
      } else if (created != null) {
    	return created;
      } else {
    	return new Date();
      }
    } else {
      if (created == null) {
    	create();
        is_used = false;
        return created;
      } else {
    	update();
        is_used = false;
        return updated;
      }
    }
  }

  public String getCreatedAsString() {
    if (created != null) {
      return format_dateTime(created);
    } else {
      return format_dateTime(new Date());
    }
  }

  public String getUpdatedAsString() {
    if (updated != null) {
      return format_dateTime(updated);
    } else if (created != null) {
      return format_dateTime(created);
    } else {
      return format_dateTime(new Date());
    }
  }

  //
  private void set_created(Date dateTime) {
    created = dateTime;
  }

  private void set_created(Object value) throws ParseException {
    if (value == null || "".equals(value)) {
      return;
    } else if (value instanceof Date) {
      set_created((Date)value);
    } else if (value instanceof String) {
      set_created(parse_dateTime((String)value));
    } else {
      throw new IllegalArgumentException(value.toString());
    }
  }

  private void set_updated(Date dateTime) {
    updated = dateTime;
  }

  private void set_updated(Object value) throws ParseException {
    if (value == null || "".equals(value)) {
      return;
    } else if (value instanceof Date) {
      set_updated((Date)value);
    } else if (value instanceof String) {
      set_updated(parse_dateTime((String)value));
    } else {
      throw new IllegalArgumentException(value.toString());
    }
  }

  private void set_removed(Date dateTime) {
    removed = dateTime;
  }

  private void set_removed(Object value) throws ParseException {
    if (value == null || "".equals(value)) {
      return;
    } else if (value instanceof Date) {
      set_removed((Date)value);
    } else if (value instanceof String) {
      set_removed(parse_dateTime((String)value));
    } else {
      throw new IllegalArgumentException(value.toString());
    }
  }

  private void set_authors(NodeList nodes) {
    int length = nodes.getLength();
    for (int i = 0;i < length;i++) {
      add_author(nodes.item(i));
    }
  }

  private void add_author(Node node) {
    authors.add(MDAuthor.newMDAuthor(node));
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("<info xmlns=\"http://org.simplemodeling/xmlns/SimpleModeler/\" version=\"");
    buf.append(format_version);
    buf.append("\"");
    if (created != null) {
      buf.append(" created=\"");
      buf.append(format_dateTime(created));
      buf.append("\"");
    }
    if (updated != null) {
      buf.append(" updated=\"");
      buf.append(format_dateTime(updated));
      buf.append("\"");
    }
    if (removed != null) {
      buf.append(" removed=\"");
      buf.append(format_dateTime(removed));
      buf.append("\"");
    }
    buf.append(">");
    for (MDAuthor author: authors) {
      author.toString(buf);
    }
    buf.append("<generator uri=\"http://org.simplemodeling/SimpleModeler/\" version=\"");
    buf.append(generator_version);
    buf.append("\" build=\"");
    buf.append(generator_build);
    buf.append("\">SimpleModeler</generator>");
    model.toString(buf);
    if (history != null) {
      history.toString(buf);
    }
    buf.append("</info>");
    return buf.toString();
  }

  private String format_dateTime(Date dateTime) {
    return dateTime_format.format(dateTime);
  }

  private Date parse_dateTime(String value) throws ParseException {
    return dateTime_format.parse(value);
  }

  private String escape_string(String string) {
    if (string.indexOf('<') != -1) {
      return "<![CDATA[" + string + "]]>";
    } else {
      return string;
    }
  }

  public static class MDAuthor {
    public String name = "";
    public String uri = null;
    public String email = null;

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof MDAuthor)) {
        return false;
      }
      return name.equals(((MDAuthor)obj).name);
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    public void toString(StringBuilder buf) {
      buf.append("<author");
      if (uri != null) {
        buf.append(" uri=\"");
        buf.append(uri);
        buf.append("\"");
      }
      if (email != null) {
        buf.append(" email=\"");
        buf.append(email);
        buf.append("\"");
      }
      buf.append(">");
      buf.append(name);
      buf.append("</author>");
    }

    public static MDAuthor newMDAuthor(Node node) {
      try {
        XPathFactory xpath_factory = XPathFactory.newInstance();  
        XPath xpath = xpath_factory.newXPath();
        xpath.setNamespaceContext(ns_context);
        MDAuthor author = new MDAuthor();
        author.name = (String)xpath.evaluate("/sm:author/text()", node, XPathConstants.STRING);
        author.uri = (String)xpath.evaluate("/sm:author/@uri", node, XPathConstants.STRING);
        author.email = (String)xpath.evaluate("/sm:author/@email", node, XPathConstants.STRING);
        return author;
      } catch (Exception e) {
        throw new IllegalArgumentException(node.toString(), e); // XXX
      }
    }
  }

  public static class MDModel {
    public String name = null;
    public String version = null;
    public String build = null;
    public MDEntity entity = null;
    public final List<MDEntity> entities = new ArrayList<MDEntity>();

    public void toString(StringBuilder buf) {
      buf.append("<model");
      if (name != null) {
        buf.append(" name=\"");
        buf.append(name);
        buf.append("\"");
      }
      if (version != null) {
        buf.append(" version=\"");
        buf.append(version);
        buf.append("\"");
      }
      if (build != null) {
        buf.append(" build=\"");
        buf.append(build);
        buf.append("\"");
      }
      buf.append(">");
      if (entity != null) {
        entity.toString(buf);
      }
      for (MDEntity entity: entities) {
        entity.toString(buf);
      }
      buf.append("</model>");
    }

    public static class MDEntity {
      public String name = null;
      public String version = null;
      public String build = null;
      public final List<MDAttribute> attributes = new ArrayList<MDAttribute>();

      public void toString(StringBuilder buf) {
        buf.append("<entity");
        if (name != null) {
          buf.append(" name=\"");
          buf.append(name);
          buf.append("\"");
        }
        if (version != null) {
          buf.append(" version=\"");
          buf.append(version);
          buf.append("\"");
        }
        if (build != null) {
          buf.append(" build=\"");
          buf.append(build);
          buf.append("\"");
        }
        buf.append(">");
        for (MDAttribute attr: attributes) {
          attr.toString(buf);
        }
        buf.append("</entity>");
      }

      public void addAttribute(String name, String attributeType,
          String columnName, String columnType) {
        MDAttribute attr = new MDAttribute();
        attr.name = name;
        attr.attributeType = attributeType;
        attr.columnName = columnName;
        attr.columnType = columnType;
        attributes.add(attr);
      }
    }

    public static class MDAttribute {
      public String name = null;
      public String attributeType = null;
      public String columnName = null;
      public String columnType = null;

      public void toString(StringBuilder buf) {
        buf.append("<attribute");
        if (name != null) {
          buf.append(" name=\"");
          buf.append(name);
          buf.append("\"");
        }
        if (attributeType != null) {
          buf.append(" type=\"");
          buf.append(attributeType);
          buf.append("\"");
        }
        if (columnName != null) {
          buf.append(" column-name=\"");
          buf.append(columnName);
          buf.append("\"");
        }
        if (columnType != null) {
          buf.append(" column-type=\"");
          buf.append(columnType);
          buf.append("\"");
        }
        buf.append(">");
        buf.append("</attribute>");
      }
    }
  }

  public class MDHistory {
    public List<MDEvent> events = new ArrayList<MDEvent>();

    public void toString(StringBuilder buf) {
      buf.append("<history>");
      for (MDEvent event: events) {
        event.toString(buf);
      }
      buf.append("</history>");
    }

    public MDEvent event(String name) {
      MDEvent event = new MDEvent(name);
      events.add(event);
      return event;
    }

    public class MDEvent {
      public final String name;
      public final MDAuthor author = new MDAuthor();
      public final Date dateTime = new Date();
      public String message = null;
      public Exception exception = null;

      public MDEvent(String name) {
        this.name = name;
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user != null) {
          author.name = user.getNickname();
          author.email = user.getEmail();
        } else {
          author.name = "Anon";
        }
      }

      public MDEvent message(String aMessage) {
        message = aMessage;
        return this;
      }

      public MDEvent exception(Exception e) {
        exception = e;
        return this;
      }

      public void toString(StringBuilder buf) {
        buf.append("<event datetime=\"");
        buf.append(format_dateTime(dateTime));
        buf.append("\" name=\"");
        buf.append(name);
        buf.append("\"");
        if (message == null && exception == null) {
          buf.append("/>");
        } else {
          buf.append(">");
          if (message != null) {
            buf.append("<message>");
            buf.append(escape_string(message));
            buf.append("</message>");
          }
          if (exception != null) {
            buf.append("<exception>");
            buf.append(escape_string(exception.toString()));
            buf.append("</exception>");
          }
          buf.append("</event>");
        }
      }
    }
  }

  public static MDEntityInfo reconstituteMDEntityInfo(String string) {
    try {
      DocumentBuilderFactory xdoc_factory = DocumentBuilderFactory.newInstance();
      xdoc_factory.setNamespaceAware(true);
      DocumentBuilder builder = xdoc_factory.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(string));
      Document doc = builder.parse(is);
      XPathFactory xpath_factory = XPathFactory.newInstance();  
      XPath xpath = xpath_factory.newXPath();
      xpath.setNamespaceContext(ns_context);
      MDEntityInfo info = new MDEntityInfo();
      info.set_created(xpath.evaluate("/sm:info/@created", doc, XPathConstants.STRING));
      info.set_updated(xpath.evaluate("/sm:info/@updated", doc, XPathConstants.STRING));
      info.set_removed(xpath.evaluate("/sm:info/@removed", doc, XPathConstants.STRING));
      info.set_authors((NodeList)xpath.evaluate("/sm:info/sm:author", doc, XPathConstants.NODESET));
      return info;
    } catch (Exception e) {
      MDEntityInfo info = new MDEntityInfo();
      info.history.event("crash").message(string).exception(e);
      return info;
    }
  }

  private static NamespaceContext ns_context = new NamespaceContext() {
    @Override
    public String getNamespaceURI(String prefix) {
      if (prefix == null) {
        throw new IllegalArgumentException();
      } else if ("sm".equals(prefix)) {
        return "http://org.simplemodeling/xmlns/SimpleModeler/";
      } else {
        return null;
      }
    }

    @Override
    public String getPrefix(String namespaceURI) {
      if (namespaceURI == null) {
	throw new IllegalArgumentException();
      } else if ("http://org.simplemodeling/xmlns/SimpleModeler/".equals(namespaceURI)) {
        return "sm";
      } else {
        return null;
      }
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
      if (namespaceURI == null) {
        throw new IllegalArgumentException();
      } else if ("http://org.simplemodeling/xmlns/SimpleModeler/".equals(namespaceURI)) {
        return Arrays.asList("sm").iterator();
      } else {
        return null;
      }
    }
  };
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val generator_version = gaejContext.simplemodelerVersion
    val generator_build = gaejContext.simplemodelerBuild

    val maker = new JavaTextMaker(
      code,
      Map("%packageName%" -> packageName,
          "%format_version%" -> "0.1",
          "%generator_version%" -> generator_version,
          "%generator_build%" -> generator_build))
    out.append(maker.toString)
    out.flush()
  }
}
