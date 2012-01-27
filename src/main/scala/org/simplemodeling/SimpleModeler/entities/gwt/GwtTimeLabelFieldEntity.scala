package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jun. 15, 2009
 * @version Jul. 13, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtTimeLabelFieldEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TimeLabelField extends Label implements ILabelField {
	private Date time = null;

	public TimeLabelField() {
	}

	public TimeLabelField(Date time) {
		this.time = time;
		update();
	}

	private void update() {
		setText(formatDefault(time));
	}

	private String formatDefault(Date dt) {
		if (dt != null) {
			return Util.time2text(time);
		} else {
			return null;
		}
	}

	public void setValue(Date time) {
		this.time = time;
		update();
	}

	public void clearValue() {
		this.time = null;
	}

	@Override
	public Widget widget(Object value) {
		setValue((Date) value);
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
