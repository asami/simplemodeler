package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jul. 14, 2009
 * @version Jul. 15, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtIInputFieldEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

public interface IInputField {
    Object getValue();
    void setValue(Object value);
    void clearValue();
    Widget widget();
    Widget widget(Object value);
    Widget widget(Object value, Column column);
    String getStringValue();
    Boolean getBooleanValue();
    Byte getByteValue();
    Short getShortValue();
    Integer getIntValue();
    Long getLongValue();
    Float getFloatValue();
    Double getDoubleValue();
    String getIntegerValue();
    String getDecimalValue();
    Date getDateValue();
    Date getTimeValue();
    Date getDateTimeValue();
    //
    List<String> getStringListValue();
    List<Boolean> getBooleanListValue();
    List<Byte> getByteListValue();
    List<Short> getShortListValue();
    List<Integer> getIntListValue();
    List<Long> getLongListValue();
    List<Float> getFloatListValue();
    List<Double> getDoubleListValue();
    List<String> getIntegerListValue();
    List<String> getDecimalListValue();
    List<Date> getDateListValue();
    List<Date> getTimeListValue();
    List<Date> getDateTimeListValue();
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val templater = new Templater(
      code,
      Map("%packageName%" -> packageName))
    out.append(templater.toString)
    out.flush()
  }
}
