package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jul. 14, 2009
 * @version Jul. 14, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtValueSetLabelFieldBaseEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class ValueSetLabelFieldBase extends Label implements ILabelField {
    @SuppressWarnings("unchecked")
    @Override
    public Widget widget(Object value) {
        Iterable<Object> iterable = (Iterable<Object>)value;
        StringBuilder buf = new StringBuilder();
        Iterator<Object> iter = iterable.iterator();
        if (iter.hasNext()) {
            buf.append(iter.next().toString());
            while (iter.hasNext()) {
                buf.append(", ");
                buf.append(iter.next().toString());
            }
        }
        setText(buf.toString());
        return this;
    }

    @Override
    public Widget widget(Object value, Column column) {
        return widget(value);
    }
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
