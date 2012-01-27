package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Apr. 17, 2009
 * @version Oct. 23, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtUtilitiesEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val utilities = """package %packageName%;

import java.util.*;

import com.google.gwt.i18n.client.DateTimeFormat;

public final class Util {
    private static final DateTimeFormat dateTimeFormat = DateTimeFormat
            .getFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormat dateFormat = DateTimeFormat
            .getFormat("yyyy-MM-dd");
    private static final DateTimeFormat timeFormat = DateTimeFormat
            .getFormat("HH:mm:ss");

    private Util() {
    }

    public static Date makeDateTime(Date dateTime, Date date, Date time,
            boolean now) {
        if (now) {
            return new Date();
        } else if (dateTime != null) {
            return dateTime;
        } else if (date != null && time != null) {
            return string2dateTime(date2text(date) + "T" + time2text(time));
        } else {
            return new Date();
        }
    }

    public static Date makeDate(Date dateTime) {
        if (dateTime == null)
            return null;
        return dateFormat.parse(dateFormat.format(dateTime));
    }

    public static Date makeTime(Date dateTime) {
        if (dateTime == null)
            return null;
        return timeFormat.parse(timeFormat.format(dateTime));
    }

    public static Date string2dateTime(String text) {
        if (text == null || text.equals(""))
            return null;
        return dateTimeFormat.parse(text);
    }

    public static Date string2date(String text) {
        if (text == null || text.equals(""))
            return null;
        return dateFormat.parse(text);
    }

    public static Date string2time(String text) {
        if (text == null || text.equals(""))
            return null;
        if (text.charAt(0) == 'T') {
            text = text.substring(1);
        }
        return timeFormat.parse(text);
    }

    public static boolean string2boolean(String text) {
        return text != null
                && (text.toLowerCase().equals("true") || text.equals("1"));
    }

    public static String dateTime2text(Date dateTime) {
        return dateTime == null ? "" : dateTimeFormat.format(dateTime);
    }

    public static String date2text(Date date) {
        return date == null ? "" : dateFormat.format(date);
    }

    public static String time2text(Date time) {
        return time == null ? "" : timeFormat.format(time);
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

    public static String datatype2string(Boolean value) {
        return value == null ? "" : value.toString();
    }

    public static String datatype2string(Byte value) {
        return value == null ? "" : value.toString();
    }

    public static String datatype2string(Short value) {
        return value == null ? "" : value.toString();
    }

    public static String datatype2string(Integer value) {
        return value == null ? "" : value.toString();
    }

    public static String datatype2string(Long value) {
        return value == null ? "" : value.toString();
    }

    public static String datatype2string(Float value) {
        return value == null ? "" : value.toString();
    }

    public static String datatype2string(Double value) {
        return value == null ? "" : value.toString();
    }

    public static String datatype2string(String value) {
        return value == null ? "" : value;
    }

    public static String datatype2string(Object value) {
        return value == null ? "" : value.toString();
    }

    public static Boolean object2boolean(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Boolean)value;
        } else {
            return string2boolean(value.toString());
        }
    }

    public static Byte object2byte(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Byte)value;
        } else {
            return Byte.parseByte(value.toString());
        }
    }

    public static Short object2short(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Short)value;
        } else {
            return Short.parseShort(value.toString());
        }
    }

    public static Integer object2int(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Integer)value;
        } else {
            return Integer.parseInt(value.toString());
        }
    }

    public static Long object2long(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Long)value;
        } else {
            return Long.parseLong(value.toString());
        }
    }

    public static Float object2float(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Float)value;
        } else {
            return Float.parseFloat(value.toString());
        }
    }

    public static Double object2double(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Double)value;
        } else {
            return Double.parseDouble(value.toString());
        }
    }

    public static String object2integer(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String)value;
        } else {
            return value.toString();
        }
    }

    public static String object2decimal(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String)value;
        } else {
            return value.toString();
        }
    }

    public static Date object2dateTime(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            return (Date)value;
        } else {
            return string2dateTime(value.toString());
        }
    }

    public static String escapeXmlText(String string) {
        if (string.indexOf('<') == -1 && string.indexOf('>') == -1
                && string.indexOf('&') == -1 && string.indexOf('"') == -1
                && string.indexOf('\'') == -1) {
            return (string);
        }
        StringBuffer buffer = new StringBuffer();
        int size = string.length();
        for (int i = 0; i < size; i++) {
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
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val templater = new Templater(
      utilities,
      Map("%packageName%" -> packageName))
    out.append(templater.toString)
    out.flush()
  }
}
