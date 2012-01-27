package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jul. 14, 2009
 * @version Jul. 18, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtValueSetInputFieldBaseEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ValueSetInputFieldBase extends VerticalPanel implements IInputField {
    private List<String> values = new ArrayList<String>();
    private FlexTable table = new FlexTable();
    private NewDialog newDialog = new NewDialog();
    private EditDialog editDialog = new EditDialog();
    private DeleteDialog deleteDialog = new DeleteDialog();
    private TextBox appendField = new TextBox();
    private boolean isDirtyAppendField = false;

    public ValueSetInputFieldBase() {
        appendField.setTextAlignment(TextBox.ALIGN_RIGHT);
        redraw();
    }

    protected ILabelField createLabelField() {
        return new StringLabelField();
    }

    protected IValueEditor createEditor() {
        return new StringEditor();
    }

    protected Object newDefaultValue() {
        return null;
    }

    public void redraw() {
        remove(table);
        table = new FlexTable();
        table.addStyleName("datasheet");
        int length = values.size();
        for (int index = 0; index < length; index++) {
            Object value = values.get(index);
            TextBox input = new TextBox();
            input.setTextAlignment(TextBox.ALIGN_RIGHT);
            input.setValue(value.toString());
            ChangeInputValue handler = new ChangeInputValue(index, input);
            input.addValueChangeHandler(handler);
            input.addKeyPressHandler(handler);
            table.setWidget(index, 0, input);
            table.setWidget(index, 1, new EditButton(editDialog, index));
            table.setWidget(index, 2, new DeleteButton(deleteDialog, index));
        }
        isDirtyAppendField = false;
        appendField.setText(null);
        appendField.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (isDirtyAppendField) {
                    isDirtyAppendField = false;
                    appendTextValue(appendField.getText());
                }
            }
        });
        appendField.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                char c = event.getCharCode();
                if (c == '\n' || c == '\r') {
                    if (isDirtyAppendField) {
                        isDirtyAppendField = false;
                        appendTextValue(appendField.getText());
                    }
                } else {
                    isDirtyAppendField = true;
                }
            }
        });
        table.setWidget(length, 0, appendField);
        table.setWidget(length, 1, new NewButton(newDialog));
        add(table);
    }

    class ChangeInputValue implements ValueChangeHandler<String>, KeyPressHandler {
        private final int index;
        private final TextBox input; 
        private boolean isDirty = false;

        public ChangeInputValue(int index, TextBox input) {
            this.index = index;
            this.input = input;
        }

        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
            if (isDirty) {
                updateTextValue(index, input.getText());
                isDirty = false;
            }
        }

        @Override
        public void onKeyPress(KeyPressEvent event) {
            char c = event.getCharCode();
            if (c == '\n' || c == '\r') {
                if (isDirty) {
                    isDirty = false;
                    updateTextValue(index, input.getText());
                }
            } else {
                isDirty = true;
            }
        }
    }

    private void appendTextValue(String text) {
        String[] vals = text.split("[ ,:;]");
        for (String val: vals) {
            appendValue(val);
        }
    }

    private void appendValue(String value) {
        values.add(value);
        redraw();
    }

    private void updateTextValue(int index, String value) {
        updateValue(index, value);
    }

    private void updateValue(int index, String value) {
        values.remove(index);
        values.add(index, value);
    }

    //
    class NewButton extends Button {
        public NewButton(final NewDialog newDialog) {
            setText("New");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    newDialog.execute(event);
                }
            });
        }
    }

    class EditButton extends Button {
        public EditButton(final EditDialog editDialog, final int index) {
            setText("Edit");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    editDialog.execute(event, index);
                }
            });
        }
    }

    class DeleteButton extends Button {
        public DeleteButton(final DeleteDialog deleteDialog, final int index) {
            setText("Delete");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    deleteDialog.execute(event, index);
                }
            });
        }
    }

    class NewDialog extends DialogBox {
        private DockPanel mainPanel = new DockPanel();
        private Button okButton = new Button("Ok");
        private Button cancelButton = new Button("Cancel");
        private IValueEditor editor = createEditor();

        NewDialog() {
            addStyleName("newDialog");
            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                    appendTextValue(editor.getTextValue());
                    editor.clearValue();
                }
            });
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
            setText(getLabelNew());
            mainPanel.addStyleName("dialogPanel");
            setText("New");
            setAnimationEnabled(true);
            mainPanel.add(editor.widget(values), DockPanel.CENTER);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event) {
            editor.setValue(newDefaultValue());
            center();
        }
    }

    class EditDialog extends DialogBox {
        private DockPanel mainPanel = new DockPanel();
        private Button okButton = new Button("Ok");
        private Button cancelButton = new Button("Cancel");
        private IntEditor editor = new IntEditor();
        private int index = -1;

        EditDialog() {
            addStyleName("editDialog");
            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    updateValue(index, editor.getTextValue());
                    hide();
                    redraw();
                }
            });
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
            mainPanel.addStyleName("dialogPanel");
            setText("Edit");
            setAnimationEnabled(true);
            mainPanel.add(editor, DockPanel.CENTER);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event, int index) {
            Object value = values.get(index);
            setText(getLabelEdit(value));
            editor.setValue(value);
            this.index = index; 
            center();
        }
    }

    class DeleteDialog extends DialogBox {
        private DockPanel mainPanel = new DockPanel();
        private Button okButton = new Button("Ok");
        private Button cancelButton = new Button("Cancel");
        private ILabelField labelField = createLabelField();
        private int index = -1;

        DeleteDialog() {
            addStyleName("showDialog");
            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    values.remove(index);
//                    table.removeRow(index);
                    hide();
                    redraw();
                }
            });
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
            mainPanel.addStyleName("dialogPanel");
            setText("Delete");
            setAnimationEnabled(true);
            mainPanel.add(labelField.widget(values), DockPanel.CENTER);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event, int index) {
            this.index = index;
            Object value = values.get(index);
            setText(getLabelDelete(value));
            center();
            labelField.setText(value.toString());
        }
    }

    public String getLabelValue() {
        return "Value";
    }

    public String getLabelName() {
        return "Name";
    }

    public String getLabelNew() {
        return "New";
    }

    public String getLabelEdit(Object value) {
        return "Edit";
    }

    public String getLabelDelete(Object value) {
        return "Delete";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getValue() {
        return Collections.unmodifiableList(new ArrayList(values));
    }

    @Override
    public String getStringValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean getBooleanValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Byte getByteValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Short getShortValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getIntValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getLongValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Float getFloatValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double getDoubleValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIntegerValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDecimalValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDateValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getTimeValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDateTimeValue() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getStringListValue() {
        return Collections.unmodifiableList(new ArrayList(values));
    }

    @Override
    public List<Boolean> getBooleanListValue() {
        List<Boolean> list = new ArrayList<Boolean>();
        for (String elem: getStringListValue()) {
            list.add(string2boolean(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Byte> getByteListValue() {
        List<Byte> list = new ArrayList<Byte>();
        for (String elem: getStringListValue()) {
            list.add(string2byte(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Short> getShortListValue() {
        List<Short> list = new ArrayList<Short>();
        for (String elem: getStringListValue()) {
            list.add(string2short(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Integer> getIntListValue() {
        List<Integer> list = new ArrayList<Integer>();
        for (String elem: getStringListValue()) {
            list.add(string2int(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Long> getLongListValue() {
        List<Long> list = new ArrayList<Long>();
        for (String elem: getStringListValue()) {
            list.add(string2long(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Float> getFloatListValue() {
        List<Float> list = new ArrayList<Float>();
        for (String elem: getStringListValue()) {
            list.add(string2float(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Double> getDoubleListValue() {
        List<Double> list = new ArrayList<Double>();
        for (String elem: getStringListValue()) {
            list.add(string2double(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<String> getDecimalListValue() {
        List<String> list = new ArrayList<String>();
        for (String elem: getStringListValue()) {
            list.add(string2decimal(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<String> getIntegerListValue() {
        List<String> list = new ArrayList<String>();
        for (String elem: getStringListValue()) {
            list.add(string2decimal(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Date> getDateListValue() {
        List<Date> list = new ArrayList<Date>();
        for (String elem: getStringListValue()) {
            list.add(string2date(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Date> getTimeListValue() {
        List<Date> list = new ArrayList<Date>();
        for (String elem: getStringListValue()) {
            list.add(string2time(elem));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Date> getDateTimeListValue() {
        List<Date> list = new ArrayList<Date>();
        for (String elem: getStringListValue()) {
            list.add(string2dateTime(elem));
        }
        return Collections.unmodifiableList(list);
    }

    protected String string2string(String value) {
        return value;
    }

    protected boolean string2boolean(String value) {
        return value.toLowerCase().equals("true");
    }

    protected byte string2byte(String value) {
        return Byte.parseByte(value);
    }

    protected short string2short(String value) {
        return Short.parseShort(value);
    }

    protected int string2int(String value) {
        return Integer.parseInt(value);
    }

    protected Long string2long(String string) {
        return Long.parseLong(string);
    }

    protected Float string2float(String string) {
        return Float.parseFloat(string);
    }

    protected Double string2double(String string) {
        return Double.parseDouble(string);
    }

    protected String string2integer(String string) {
        return string;
    }

    protected String string2decimal(String string) {
        return string;
    }

    protected Date string2date(String string) {
        return Util.string2date(string);
    }

    protected Date string2time(String string) {
        return Util.string2time(string);
    }

    protected Date string2dateTime(String string) {
        return Util.string2dateTime(string);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) {
        Iterator<Object> iter = ((Iterable<Object>)value).iterator();
        while (iter.hasNext()) {
            values.add(value2string(iter.next()));
        }
    }

    protected String value2string(Object value) {
        return value.toString();
    }

    @Override
    public void clearValue() {
        values.clear();
    }

    @Override
    public Widget widget() {
        return this;
    }

    @Override
    public Widget widget(Object value) {
        setValue(value);
        redraw();
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
