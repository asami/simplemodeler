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
class GwtStringEditorEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StringEditor extends VerticalPanel implements IValueEditor {
    private String value;
    private final Label textLabel = new Label();
    private final TextBox textBox = new TextBox();

    public StringEditor() {
        setValue(null);
        add(textLabel);
        textBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                char c = event.getCharCode();
                if (!(c == '\n' || c == '\r')) {
                    return;
                }
                value = textBox.getValue();
                textLabel.setText(value.toString());
            }
        });
        add(textBox);
    }

    @Override
    public void clearValue() {
        setValue(null);
        textBox.setValue(null);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getTextValue() {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            this.value = null;
            textLabel.setText(null);
        } else {
            this.value = value.toString();
            textLabel.setText(value.toString());
        }
    }

    @Override
    public Widget widget() {
        return this;
    }

    @Override
    public Widget widget(Object value) {
        textBox.setValue(value.toString());
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
