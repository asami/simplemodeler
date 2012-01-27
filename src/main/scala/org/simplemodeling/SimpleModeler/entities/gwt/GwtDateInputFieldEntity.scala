package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jun. 15, 2009
 * @version Jul. 15, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtDateInputFieldEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateInputField extends HorizontalPanel implements IInputField {
    private Date date = null;
    private final TextBox dateText = new TextBox();
    private final PushButton editDate = new PushButton("Date");
    private final CheckBox now = new CheckBox();
    private final DateDialog dateDialog = new DateDialog(dateText);

    public DateInputField() {
        dateText.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                date = null;
            }
        });
        editDate.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dateDialog.execute(event);
            }
        });
        now.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    dateText.setEnabled(false);
                    editDate.setEnabled(false);
                } else {
                    dateText.setEnabled(true);
                    editDate.setEnabled(true);
                }
            }
        });
        add(dateText);
        add(editDate);
        add(new Label("Now"));
        add(now);
    }

    public Date getValue() {
        if (now.getValue()) {
            date = new Date();
        }
        if (date != null) {
            return date;
        } else {
            return Util.string2date(dateText.getText());
        }
    }

    public void setValue(Object value) {
        if (value == null) {
            date = null;
        } else if (value instanceof Date) {
            date = (Date)value;
        } else if (value instanceof String) {
            date = Util.string2date((String) value);
        } else {
            throw new IllegalArgumentException(value + " is not Date or String");
        }
        if (date != null) {
            dateText.setValue(Util.date2text(date));
        } else {
            dateText.setValue(null);
        }
    }

    public void clearValue() {
        setValue(null);
    }

    class DateDialog extends DialogBox {
        private final DockPanel mainPanel = new DockPanel();
        private final Button closeButton = new Button("Close");
        private final DatePicker date = new DatePicker();
        private final HasText target;

        public DateDialog(HasText target) {
            this.target = target;
            setText("Date");
            closeButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
            date.addValueChangeHandler(new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(ValueChangeEvent<Date> event) {
                    DateDialog.this.target.setText(Util.date2text(event
                            .getValue()));
                    hide();
                }
            });
            setAnimationEnabled(true);
            mainPanel.add(date, DockPanel.CENTER);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(closeButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event) {
            setDate(target.getText());
            center();
        }

        public void setDate(String text) {
            if (text == null || "".equals(text)) {
                return;
            }
            date.setValue(Util.string2date(text));
        }
    }

    @Override
    public Widget widget() {
        return this;
    }

    @Override
    public Widget widget(Object value) {
        setValue(value);
        return this;
    }

    @Override
    public Widget widget(Object value, Column column) {
        return widget(value);
    }

    @Override
    public String getStringValue() {
        if (date != null) {
            return Util.date2text(date);
        } else {
            return null;
        }
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

    @Override
    public List<String> getStringListValue() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<Boolean> getBooleanListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Byte> getByteListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Short> getShortListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> getIntListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Long> getLongListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Float> getFloatListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Double> getDoubleListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getIntegerListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getDecimalListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Date> getDateListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Date> getTimeListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Date> getDateTimeListValue() {
        throw new UnsupportedOperationException();
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
