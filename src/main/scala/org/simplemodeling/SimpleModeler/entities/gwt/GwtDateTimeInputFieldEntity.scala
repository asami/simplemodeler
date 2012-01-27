package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Apr. 19, 2009
 * @version Jul. 15, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtDateTimeInputFieldEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateTimeInputField extends HorizontalPanel implements IInputField {
    private Date dateTime = null;
    private final TextBox dateText = new TextBox();
    private final PushButton editDate = new PushButton("Date");
    private final TextBox timeText = new TextBox();
    private final PushButton editTime = new PushButton("Time");
    private final CheckBox now = new CheckBox();
    private final DateDialog dateDialog = new DateDialog(dateText);
    private final TimeDialog timeDialog = new TimeDialog(timeText);

    public DateTimeInputField() {
        dateText.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                dateTime = null;
            }
        });
        editDate.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dateDialog.execute(event);
            }
        });
        timeText.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                dateTime = null;
            }
        });
        editTime.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                timeDialog.execute(event);
            }
        });
        now.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    dateText.setEnabled(false);
                    editDate.setEnabled(false);
                    timeText.setEnabled(false);
                    editTime.setEnabled(false);
                } else {
                    dateText.setEnabled(true);
                    editDate.setEnabled(true);
                    timeText.setEnabled(true);
                    editTime.setEnabled(true);
                }
            }
        });
        add(dateText);
        add(editDate);
        add(timeText);
        add(editTime);
        add(new Label("Now"));
        add(now);
    }

    public Date getValue() {
        if (now.getValue()) {
            dateTime = new Date();
        }
        if (dateTime != null) {
            return dateTime;
        } else {
            return Util.string2dateTime(dateText.getText() + "T"
                    + timeText.getText());
        }
    }

    public void setValue(Object value) {
        if (value == null) {
            dateTime = null;
        } else if (value instanceof Date) {
            dateTime = (Date) value;
        } else if (value instanceof String) {
            dateTime = Util.string2dateTime((String) value);
        } else {
            throw new IllegalArgumentException(value + " is not Date or String");
        }
        if (dateTime != null) {
            dateText.setValue(Util.date2text(dateTime));
            timeText.setValue(Util.time2text(dateTime));
        } else {
            dateText.setValue(null);
            timeText.setValue(null);
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

    class TimeDialog extends DialogBox {
        private final DockPanel mainPanel = new DockPanel();
        private final Button okButton = new Button("Ok");
        private final Button cancelButton = new Button("Cancel");
        private final ListBox hour1 = new ListBox();
        private final ListBox hour2 = new ListBox();
        private final ListBox minute1 = new ListBox();
        private final ListBox minute2 = new ListBox();
        private final ListBox second1 = new ListBox();
        private final ListBox second2 = new ListBox();
        private final Label time = new Label();
        private final HasText target;

        public TimeDialog(HasText target) {
            this.target = target;
            setText("Time");
            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    TimeDialog.this.target.setText(time.getText());
                    hide();
                }
            });
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
            for (int i = 0; i < 12; i++) {
                hour1.addItem(Integer.toString(i));
            }
            hour1.addItem("-");
            hour1.setVisibleItemCount(13);
            for (int i = 12; i < 24; i++) {
                hour2.addItem(Integer.toString(i));
            }
            hour2.addItem("-");
            hour2.setVisibleItemCount(13);
            for (int i = 0; i < 6; i++) {
                minute1.addItem(Integer.toString(i));
                second1.addItem(Integer.toString(i));
            }
            minute1.setVisibleItemCount(6);
            second1.setVisibleItemCount(6);
            for (int i = 0; i < 10; i++) {
                minute2.addItem(Integer.toString(i));
                second2.addItem(Integer.toString(i));
            }
            minute2.setVisibleItemCount(10);
            second2.setVisibleItemCount(10);
            hour1.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    unselect(hour2);
                    update();
                }
            });
            hour2.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    unselect(hour1);
                    update();
                }
            });
            minute1.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    update();
                }
            });
            minute2.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    update();
                }
            });
            second1.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    update();
                }
            });
            second2.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    update();
                }
            });
            setAnimationEnabled(true);
            Grid timePanel = new Grid(2, 6);
            RowFormatter gridRowFormatter = timePanel.getRowFormatter();
            gridRowFormatter.setVerticalAlign(1, ALIGN_TOP);
            timePanel.setText(0, 0, "AM");
            timePanel.setText(0, 1, "PM");
            timePanel.setText(0, 2, "Min10");
            timePanel.setText(0, 3, "Min1");
            timePanel.setText(0, 4, "Sec10");
            timePanel.setText(0, 5, "Sec1");
            timePanel.setWidget(1, 0, hour1);
            timePanel.setWidget(1, 1, hour2);
            timePanel.setWidget(1, 2, minute1);
            timePanel.setWidget(1, 3, minute2);
            timePanel.setWidget(1, 4, second1);
            timePanel.setWidget(1, 5, second2);
            mainPanel.add(timePanel, DockPanel.CENTER);
            HorizontalPanel northPanel = new HorizontalPanel();
            northPanel.add(time);
            mainPanel.add(northPanel, DockPanel.NORTH);
            mainPanel.setCellHorizontalAlignment(northPanel,
                    DockPanel.ALIGN_CENTER);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void setTime(String text) {
            if (text == null || "".equals(text)) {
                Date d = new Date();
                setHour(d.getHours());
            } else {
                Date d = Util.string2time(text);
                setHour(d.getHours());
                setMinute(d.getMinutes());
                setSecond(d.getSeconds());
            }
        }

        public void setHour(int hour) {
            if (hour < 12) {
                unselect(hour2);
                hour1.setSelectedIndex(hour - 1);
            } else {
                unselect(hour1);
                hour2.setSelectedIndex(hour - 12);
            }
        }

        public void setMinute(int minute) {
            minute1.setSelectedIndex(minute / 10);
            minute2.setSelectedIndex(minute % 10);
        }

        public void setSecond(int second) {
            second1.setSelectedIndex(second / 10);
            second2.setSelectedIndex(second % 10);
        }

        private void unselect(ListBox listbox) {
            listbox.setItemSelected(listbox.getItemCount() - 1, true);
            // for (int i = 0; i < listbox.getItemCount(); i++) {
            // listbox.setItemSelected(i, false);
            // }
        }

        public void update() {
            StringBuilder buffer = new StringBuilder();
            String text;
            int index = hour1.getSelectedIndex();
            text = hour1.getItemText(index);
            if (!"-".equals(text)) {
                if (text.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(hour1.getItemText(index));
            }
            index = hour2.getSelectedIndex();
            text = hour2.getItemText(index);
            if (!"-".equals(text)) {
                buffer.append(hour2.getItemText(index));
            }
            buffer.append(":");
            index = minute1.getSelectedIndex();
            if (index != -1) {
                buffer.append(minute1.getItemText(index));
            }
            index = minute2.getSelectedIndex();
            if (index != -1) {
                buffer.append(minute2.getItemText(index));
            }
            buffer.append(":");
            index = second1.getSelectedIndex();
            if (index != -1) {
                buffer.append(second1.getItemText(index));
            }
            index = second2.getSelectedIndex();
            if (index != -1) {
                buffer.append(second2.getItemText(index));
            }
            time.setText(buffer.toString());
        }

        public void execute(ClickEvent event) {
            setTime(target.getText());
            update();
            center();
        }
    }

    @Override
    public Widget widget() {
        return this;
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

    @Override
    public String getStringValue() {
        if (dateTime != null) {
            return Util.dateTime2text(dateTime);
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
    public List<String> getDecimalListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getIntegerListValue() {
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
