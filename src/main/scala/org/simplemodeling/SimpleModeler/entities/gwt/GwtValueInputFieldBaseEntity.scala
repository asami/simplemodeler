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
class GwtValueInputFieldBaseEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ValueInputFieldBase extends HorizontalPanel implements IInputField {
    private final TextBox textBox = new TextBox();
    private EditDialog editDialog = new EditDialog();

    public ValueInputFieldBase() {
        textBox.setTextAlignment(TextBox.ALIGN_RIGHT);
        add(textBox);
        add(new EditButton(editDialog));
    }

    protected IValueEditor createEditor() {
      return new StringEditor();
    }

    class EditButton extends Button {
        public EditButton(final EditDialog editDialog) {
            setText("Edit");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    editDialog.execute(event);
                }
            });
        }
    }

    class EditDialog extends DialogBox {
        private DockPanel mainPanel = new DockPanel();
        private Button okButton = new Button("Ok");
        private Button cancelButton = new Button("Cancel");
        private IValueEditor editor = createEditor();

        EditDialog() {
            addStyleName("newDialog");
            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                    textBox.setValue(editor.getTextValue());
                    editor.clearValue();
                }
            });
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
            mainPanel.addStyleName("dialogPanel");
            setText("New");
            setAnimationEnabled(true);
            mainPanel.add(editor.widget(), DockPanel.CENTER);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event) {
            setText(getLabelEdit());
            Integer value = getIntValue();
            if (value == null) {
                value = 1;
            }
            editor.setValue(value);
            center();
        }
    }

    public String getLabelEdit() {
        return "Edit";
    }
    
    @Override
    public Object getValue() {
        String string = textBox.getValue();
        if (string == null) {
            return null;
        } else {
            return string2value(string);
        }
    }

    protected Object string2value(String value) {
        if (value == null) {
            return null;
        } else {
            return value;
        }
    }

    @Override
    public String getStringValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2string(string);
        }
    }

    protected String string2string(String value) {
        return value;
    }

    @Override
    public Boolean getBooleanValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2boolean(string);
        }
    }

    protected boolean string2boolean(String value) {
        return value.toLowerCase().equals("true");
    }

    @Override
    public Byte getByteValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2byte(string);
        }
    }

    protected byte string2byte(String value) {
        return Byte.parseByte(value);
    }

    @Override
    public Short getShortValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2short(string);
        }
    }

    protected short string2short(String value) {
        return Short.parseShort(value);
    }

    @Override
    public Integer getIntValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2int(string);
        }
    }

    protected int string2int(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Long getLongValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2long(string);
        }
    }

    protected Long string2long(String string) {
        return Long.parseLong(string);
    }

    @Override
    public Float getFloatValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2float(string);
        }
    }

    protected Float string2float(String string) {
        return Float.parseFloat(string);
    }

    @Override
    public Double getDoubleValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2double(string);
        }
    }

    protected Double string2double(String string) {
        return Double.parseDouble(string);
    }

    @Override
    public String getIntegerValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2integer(string);
        }
    }

    protected String string2integer(String string) {
        return string;
    }

    @Override
    public String getDecimalValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2decimal(string);
        }
    }

    protected String string2decimal(String string) {
        return new String(string);
    }

    @Override
    public Date getDateValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2date(string);
        }
    }

    protected Date string2date(String string) {
        return Util.string2date(string);
    }

    @Override
    public Date getTimeValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2time(string);
        }
    }

    protected Date string2time(String string) {
        return Util.string2time(string);
    }

    @Override
    public Date getDateTimeValue() {
        String string = textBox.getValue();
        if (string == null || "".equals(string)) {
            return null;
        } else {
            return string2dateTime(string);
        }
    }

    protected Date string2dateTime(String string) {
        return Util.string2dateTime(string);
    }

    @Override
    public List<String> getStringListValue() {
        return null;
    }

    @Override
    public List<Boolean> getBooleanListValue() {
        return null;
    }

    @Override
    public List<Byte> getByteListValue() {
        return null;
    }

    @Override
    public List<Short> getShortListValue() {
        return null;
    }

    @Override
    public List<Integer> getIntListValue() {
        return null;
    }

    @Override
    public List<Long> getLongListValue() {
        return null;
    }

    @Override
    public List<Float> getFloatListValue() {
        return null;
    }

    @Override
    public List<Double> getDoubleListValue() {
        return null;
    }

    @Override
    public List<String> getDecimalListValue() {
        return null;
    }

    @Override
    public List<String> getIntegerListValue() {
        return null;
    }

    @Override
    public List<Date> getDateListValue() {
        return null;
    }

    @Override
    public List<Date> getTimeListValue() {
        return null;
    }

    @Override
    public List<Date> getDateTimeListValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            textBox.setValue(null);
        } else {
            textBox.setValue(value.toString());
        }
    }

    @Override
    public void clearValue() {
        textBox.setValue(null);
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
