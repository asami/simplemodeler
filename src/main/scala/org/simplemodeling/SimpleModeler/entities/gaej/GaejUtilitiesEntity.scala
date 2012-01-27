package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Apr. 14, 2009
 * @version Nov.  6, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejUtilitiesEntity(val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""
  val entities = new ArrayBuffer[GaejEntityEntity]

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val utilities = """package %packageName%;

import java.util.*;
import java.io.StringReader;
import java.math.*;
import java.text.*;
import javax.jdo.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.Text;

public final class Util {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private Util() {}

    public static PersistenceManager getPersistenceManager() {
        return pmfInstance.getPersistenceManager();
    }    

    public static Date makeDateTime(Date dateTime, Date date, Date time, boolean now) {
        if (now) {
            return new Date();
        } else if (dateTime != null) {
            return dateTime;
        } else if (date != null && time != null) {
            GregorianCalendar calDate = new GregorianCalendar();
            calDate.setTime(date);
            GregorianCalendar calTime = new GregorianCalendar();
            calTime.setTime(time);
            GregorianCalendar cal = new GregorianCalendar(calDate.get(Calendar.YEAR), calDate.get(Calendar.MONTH), calDate.get(Calendar.DATE), calTime.get(Calendar.HOUR_OF_DAY), calTime.get(Calendar.MINUTE), calTime.get(Calendar.SECOND));
            return cal.getTime();
        } else {
            return new Date();
        }
    }

    public static Date makeDate(Date dateTime) {
        if (dateTime == null) return null;
        try {
            return dateFormat.parse(dateFormat.format(dateTime));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date makeTime(Date dateTime) {
        if (dateTime == null) return null;
        try {
            return timeFormat.parse(timeFormat.format(dateTime));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date string2dateTime(String text) {
        if (text == null || text.equals("")) return null;
        try {
            return dateTimeFormat.parse(text);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date string2date(String text) {
        if (text == null || text.equals("")) return null;
        try {
            return dateFormat.parse(text);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date string2time(String text) {
        if (text == null || text.equals("")) return null;
        if (text.charAt(0) == 'T') {
            text = text.substring(1);
        }
        try {
            return timeFormat.parse(text);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean string2boolean(String text) {
        return text != null && (text.toLowerCase().equals("true") || text.equals("1"));
    }

    public static Boolean string2Boolean(String text) {
        return Boolean.valueOf(text);
    }

    public static byte string2byte(String text) {
        return Byte.parseByte(text);
    }

    public static Byte string2Byte(String text) {
        return Byte.valueOf(text);
    }

    public static short string2short(String text) {
        return Short.parseShort(text);
    }

    public static Short string2Short(String text) {
        return Short.valueOf(text);
    }

    public static int string2int(String text) {
        return Integer.parseInt(text);
    }

    public static Integer string2Integer(String text) {
        return Integer.valueOf(text);
    }

    public static long string2long(String text) {
        return Long.parseLong(text);
    }

    public static Long string2Long(String text) {
        return Long.valueOf(text);
    }

    public static float string2float(String text) {
        return Float.parseFloat(text);
    }

    public static Float string2Float(String text) {
        return Float.valueOf(text);
    }

    public static double string2double(String text) {
        return Double.parseDouble(text);
    }

    public static Double string2Double(String text) {
        return Double.valueOf(text);
    }

    public static BigInteger string2BigInteger(String text) {
        return new BigInteger(text);
    }

    public static BigDecimal string2BigDecimal(String text) {
        return new BigDecimal(text);
    }

    public static Text string2Text(String string) {
	return new Text(string);
    }

    public static String string2String(String text) {
        return text;
    }

    public static String dateTime2text(Date dateTime) {
        return dateTimeFormat.format(dateTime);
    }

    public static String date2text(Date date) {
        return dateFormat.format(date);
    }

    public static String time2text(Date time) {
        return timeFormat.format(time);
    }

    public static String datatype2string(boolean value) {
        return Boolean.toString(value);
    }

    public static String datatype2string(byte value) {
        return Byte.toString(value);
    }

    public static String datatype2string(short value) {
        return Short.toString(value);
    }

    public static String datatype2string(int value) {
        return Integer.toString(value);
    }

    public static String datatype2string(long value) {
        return Long.toString(value);
    }

    public static String datatype2string(float value) {
        return Float.toString(value);
    }

    public static String datatype2string(double value) {
        return Double.toString(value);
    }

    public static String datatype2string(String value) {
        return value;
    }

    public static String datatype2string(Object value) {
        return value.toString();
    }

    public static Key allocateKey(String kind) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        KeyRange ids = ds.allocateIds(kind, 1);
        long id = ids.getStart().getId();
        return KeyFactory.createKey(kind, id);
    }

    public static Key allocateKey(String kind, Key parent) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        KeyRange ids = ds.allocateIds(parent, kind, 1);
        long id = ids.getStart().getId();
        return KeyFactory.createKey(parent, kind, id);
    }

    public static Key allocateAppUuidKey(String kind, String name) {
    	String id = name + ":" + UUID.randomUUID().toString();
        return KeyFactory.createKey(kind, id);
    }

    public static Key allocateAppUuidKey(String kind, String name, Key parent) {
    	String id = name + ":" + UUID.randomUUID().toString();
        return KeyFactory.createKey(parent, kind, id);
    }

    public static boolean isAvailableTimestamp(Date timestamp, long ttl) {
        if (timestamp == null) return false;
        return timestamp.getTime() + ttl <= System.currentTimeMillis();
    }

    public static String timestampString() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new java.util.Date());
    }

    public static String escapeXmlText(String string) {
        if (string.indexOf('<') == -1 &&
                string.indexOf('>') == -1 &&
                string.indexOf('&') == -1 &&
                string.indexOf('"') == -1 &&
                string.indexOf('\'') == -1) {
            return (string);
        }
        StringBuffer buffer = new StringBuffer();
        int size = string.length();
        for (int i = 0;i < size;i++) {
            char c = string.charAt(i);
            if (c == '<') {
                buffer.append("&lt;");
            } else if (c == '>') {
                buffer.append("&gt;");
            } else if (c == '&') {
                buffer.append("&amp;");
            } else if (c == '"') {
                buffer.append("&quot;");
            } else if (c == '\'') {
                buffer.append("&apos;");
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

 
    public static List<Element> getElements(String text) {
        try {
            DocumentBuilderFactory xdoc_factory = DocumentBuilderFactory.newInstance();
            xdoc_factory.setNamespaceAware(true);
            DocumentBuilder builder = xdoc_factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(text));
            Document doc = builder.parse(is);
            Element root = doc.getDocumentElement();
            ArrayList<Element> result = new ArrayList<Element>();
            NodeList children = root.getChildNodes();
            int length = children.getLength();
            for (int i = 0;i < length;i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    result.add((Element)child);
                }
            }
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val templater = new Templater(
      utilities,
      Map("%packageName%" -> packageName))
//    templater.replace("%referenceMethods%")(make_reference_methods)
    out.append(templater.toString)
    out.flush()
  }

  private def make_reference_methods(buffer: TextBuilder) {
    for (entity <- entities) {
      val reference_method = """
  public %entityName% makeReference_%entityTerm%(String idName, PersistenceManager pm) {
    return pm.getObjectById(%entityName%.class, idName);
  }
"""
      buffer.append(reference_method, Map("%entityName%" -> entity.name,
                                          "%entityTerm%" -> entity.term.capitalize))
    }
  }
}
