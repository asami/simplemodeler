package org.simplemodeling.SimpleModeler.entities.gaej

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{JavaTextMaker}

/*
 * @since   Jun.  1, 2009
 * @version Oct. 24, 2009
 * @author  ASAMI, Tomoharu
 */
class GaejServletUtilitiesEntity(val gaejContext: GaejEntityContext) extends GEntity(gaejContext) {
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
import java.math.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("unused")
public final class ServletUtil {
    public static boolean getBooleanParameter(HttpServletRequest req,
            String name, boolean defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    public static byte getByteParameter(HttpServletRequest req, String name,
            byte defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Byte.parseByte(value);
        }
    }

    public static short getShortParameter(HttpServletRequest req, String name,
            short defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Short.parseShort(value);
        }
    }

    public static int getIntegerParameter(HttpServletRequest req, String name,
            int defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static long getLongParameter(HttpServletRequest req, String name,
            long defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Long.parseLong(value);
        }
    }

    public static float getFloatParameter(HttpServletRequest req, String name,
            float defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Float.parseFloat(value);
        }
    }

    public static double getDoubleParameter(HttpServletRequest req,
            String name, double defaultValue) {
        String value = req.getParameter(name);
        if (value == null) {
            return defaultValue;
        } else {
            return Double.parseDouble(value);
        }
    }

    public static Boolean getBooleanParameter(HttpServletRequest req,
            String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return Boolean.valueOf(value);
        }
    }

    public static Byte getByteParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return Byte.valueOf(value);
        }
    }

    public static Short getShortParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return Short.valueOf(value);
        }
    }

    public static Integer getIntegerParameter(HttpServletRequest req,
            String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return Integer.valueOf(value);
        }
    }

    public static Long getLongParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return Long.valueOf(value);
        }
    }

    public static Float getFloatParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return Float.valueOf(value);
        }
    }

    public static Double getDoubleParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return Double.valueOf(value);
        }
    }

    public static BigInteger getBigIntegerParameter(HttpServletRequest req,
            String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return new BigInteger(value);
        }
    }

    public static BigDecimal getBigDecimalParameter(HttpServletRequest req,
            String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return new BigDecimal(value);
        }
    }

    public static String getStringParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return value;
        }
    }

    public static Date getDateTimeParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return string2DateTime(value);
        }
    }

    public static Date getDateParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return string2Date(value);
        }
    }

    public static Date getTimeParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return string2Time(value);
        }
    }

    public static Text getTextParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return string2Text(value);
        }
    }

    public static User getUserParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return string2User(value);
        }
    }

    public static Category getCategoryParameter(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        } else {
            return string2Category(value);
        }
    }

    public static Date string2DateTime(String text) {
        int length = text.length();
        int year = Integer.parseInt(text.substring(0, 4));
        int month = Integer.parseInt(text.substring(5, 7));
        int day = Integer.parseInt(text.substring(8, 10));
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (length > 11) {
            hour = Integer.parseInt(text.substring(11, 13));
        }
        if (length > 14) {
            minute = Integer.parseInt(text.substring(14, 16));
        }
        if (length > 17) {
            second = Integer.parseInt(text.substring(17, 19));
        }
        int millsec = -1;
        int offTz = 19;
        if (length > 19) {
            int c19 = text.charAt(19);
            if (c19 == '.') {
                int offset = 20;
                StringBuilder buf = new StringBuilder();
                while (true) {
                    char c = text.charAt(offset++);
                    if (c == '0' || c == '1' || c == '2' || c == '3'
                            || c == '4' || c == '5' || c == '6' || c == '7'
                            || c == '8' || c == '9') {
                        buf.append(c);
                    } else {
                        offTz = offset;
                        break;
                    }
                }
                if (buf.length() > 0) {
                    millsec = Integer.parseInt(buf.toString());
                }
            }
        }
        if (length <= offTz) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(year, month - 1, day, hour, minute, second);
            if (millsec != -1) {
                cal.set(Calendar.MILLISECOND, millsec);
            } else {
                cal.set(Calendar.MILLISECOND, 0);
            }
            return cal.getTime();
        }
        TimeZone tz = getTimeZone(text.substring(offTz));
        GregorianCalendar cal = new GregorianCalendar(tz);
        cal.set(year, month - 1, day, hour, minute, second);
        if (millsec != -1) {
            cal.set(Calendar.MILLISECOND, millsec);
        } else {
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal.getTime();
    }

    public static Date string2Date(String text) {
        int year = Integer.parseInt(text.substring(0, 4));
        int month = Integer.parseInt(text.substring(5, 7));
        int day = Integer.parseInt(text.substring(8, 10));
        TimeZone tz = TimeZone.getTimeZone("UTC");
        GregorianCalendar cal = new GregorianCalendar(tz);
        cal.set(year, month - 1, day);
        return cal.getTime();
    }

    public static Date string2Time(String text) {
        int length = text.length();
        int hour = Integer.parseInt(text.substring(0, 2));
        int minute = Integer.parseInt(text.substring(3, 5));
        int second = Integer.parseInt(text.substring(6, 8));
        int millsec = -1;
        int offTz = 8;
        if (length > 8) {
            int c8 = text.charAt(8);
            if (c8 == '.') {
                int offset = 9;
                StringBuilder buf = new StringBuilder();
                while (true) {
                    char c = text.charAt(offset++);
                    if (c == '0' || c == '1' || c == '2' || c == '3'
                            || c == '4' || c == '5' || c == '6' || c == '7'
                            || c == '8' || c == '9') {
                        buf.append(c);
                    } else {
                        offTz = offset;
                        break;
                    }
                }
                if (buf.length() > 0) {
                    millsec = Integer.parseInt(buf.toString());
                }
            }
        }
        if (length <= offTz) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(0, 0, 0, hour, minute, second);
            if (millsec != -1) {
                cal.set(Calendar.MILLISECOND, millsec);
            } else {
                cal.set(Calendar.MILLISECOND, 0);
            }
            return cal.getTime();
        }
        TimeZone tz = getTimeZone(text.substring(offTz));
        GregorianCalendar cal = new GregorianCalendar(tz);
        cal.set(0, 0, 0, hour, minute, second);
        if (millsec != -1) {
            cal.set(Calendar.MILLISECOND, millsec);
        } else {
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal.getTime();
    }

    private static TimeZone getTimeZone(String text) {
        int cTz = text.charAt(0);
        if (cTz == 'Z') {
            return TimeZone.getTimeZone("UTC");
        } else if (cTz == '+') {
            int index = text.indexOf(':');
            int hour = Integer.parseInt(text.substring(1, index));
            int minute = Integer.parseInt(text.substring(index + 1));
            int millsec = ((hour * 60) + minute) * 60 * 1000;
            String[] ids = TimeZone.getAvailableIDs(millsec);
            return new SimpleTimeZone(millsec, ids[0]);
        } else if (cTz == '-') {
            int index = text.indexOf(':');
            int hour = Integer.parseInt(text.substring(1, index));
            int minute = Integer.parseInt(text.substring(index + 1));
            int millsec = ((hour * 60) + minute) * 60 * 1000 * -1;
            String[] ids = TimeZone.getAvailableIDs(millsec);
            return new SimpleTimeZone(millsec, ids[0]);
        } else {
            String[] ids = TimeZone.getAvailableIDs();
            for (String id : ids) {
                if (text.equalsIgnoreCase(id)) {
                    return TimeZone.getTimeZone(id);
                }
            }
            throw new IllegalArgumentException("Illegal time zone = " + text);
        }
    }

    public static Text string2Text(String string) {
        return new Text(string);
    }

    public static User string2User(String string) {
        int index = string.indexOf(',');
        if (index != -1) {
            String email = string.substring(0, index);
            String authDomain = string.substring(index + 1);
            return new User(email, authDomain);
        } else {
            return new User(string, "gmail.com");
        }
    }

    public static Category string2Category(String string) {
      return new Category(string);
    }

    public static List<Boolean> getBooleanListParameter(HttpServletRequest req,
            String name) {
        List<Boolean> list = new ArrayList<Boolean>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(Boolean.parseBoolean(string));
        }
        return list;
    }

    public static List<Byte> getByteListParameter(HttpServletRequest req,
            String name) {
        List<Byte> list = new ArrayList<Byte>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(Byte.parseByte(string));
        }
        return list;
    }

    public static List<Short> getShortListParameter(HttpServletRequest req,
            String name) {
        List<Short> list = new ArrayList<Short>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(Short.parseShort(string));
        }
        return list;
    }

    public static List<Integer> getIntegerListParameter(HttpServletRequest req,
            String name) {
        List<Integer> list = new ArrayList<Integer>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(Integer.parseInt(string));
        }
        return list;
    }

    public static List<Long> getLongListParameter(HttpServletRequest req,
            String name) {
        List<Long> list = new ArrayList<Long>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(Long.parseLong(string));
        }
        return list;
    }

    public static List<Float> getFloatListParameter(HttpServletRequest req,
            String name) {
        List<Float> list = new ArrayList<Float>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(Float.parseFloat(string));
        }
        return list;
    }

    public static List<Double> getDoubleListParameter(HttpServletRequest req,
            String name) {
        List<Double> list = new ArrayList<Double>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(Double.parseDouble(string));
        }
        return list;
    }

    public static List<BigInteger> getBigIntegerListParameter(
            HttpServletRequest req, String name) {
        List<BigInteger> list = new ArrayList<BigInteger>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(new BigInteger(string));
        }
        return list;
    }

    public static List<BigDecimal> getBigDecimalListParameter(
            HttpServletRequest req, String name) {
        List<BigDecimal> list = new ArrayList<BigDecimal>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(new BigDecimal(string));
        }
        return list;
    }

    public static List<Text> getTextListParameter(
            HttpServletRequest req, String name) {
        List<Text> list = new ArrayList<Text>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(string2Text(string));
        }
        return list;
    }

    public static List<User> getUserListParameter(
            HttpServletRequest req, String name) {
        List<User> list = new ArrayList<User>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(string2User(string));
        }
        return list;
    }

    public static List<Category> getCategoryListParameter(
            HttpServletRequest req, String name) {
        List<Category> list = new ArrayList<Category>();
        List<String> strings = getStringListParameter(req, name);
        for (String string : strings) {
            list.add(string2Category(string));
        }
        return list;
    }

    public static List<String> getStringListParameter(HttpServletRequest req,
            String name) {
        List<String> list = new ArrayList<String>();
        String[] values = req.getParameterValues(name);
        if (values != null) {
            list.addAll(Arrays.asList(values));
        }
        int number = 0;
        while (true) {
            String numberName = name + "_" + number;
            String value = req.getParameter(numberName);
            if (value == null) {
                break;
            }
            if (!"".equals(value)) {
                list.add(value);
            }
            number++;
        }
        return list;
    }
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val maker = new JavaTextMaker(
      utilities,
      Map("%packageName%" -> packageName))
    out.append(maker.toString)
    out.flush()
  }
}
