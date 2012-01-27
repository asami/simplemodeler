package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jul. 14, 2009
 * @version Oct. 23, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtRecordSetInputFieldEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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
import java.util.Iterator;
import java.util.List;
import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RecordSetInputField extends VerticalPanel {
    private ColumnSet columns = new ColumnSet();
    private RecordSet records = new RecordSet(columns);
    private FlexTable table = new FlexTable();
    private NewDialog newDialog = new NewDialog();
    private ShowDialog showDialog = new ShowDialog();
    private EditDialog editDialog = new EditDialog();
    private DeleteDialog deleteDialog = new DeleteDialog();
    private FieldFactory fieldFactory = new FieldFactory();

    public RecordSetInputField() {
        add(table);
    }

    public String getLabelName() {
        return "Name";
    }

    public String getLabelValue() {
        return "Value";
    }

    public String getLabelNew() {
        return "New";
    }

    public String getLabelShow(Record record) {
        return "Show";
    }

    private String getLabelEdit(Record record) {
        return "Edit";
    }

    public String getLabelDelete(Record record) {
        return "Delete";
    }

    public void setValue(String columnName, int index, Object value) {
        Record record = records.update(index);
        record.set(columnName, value);
    }

    public void clearValue() {
        records.clear();
        remove(table);
        table = new FlexTable();
        add(table);
    }

    public void redraw() {
        table.addStyleName("datasheet");
        table.getRowFormatter().addStyleName(0, "datasheetHeader");
        int height = records.length();
        int width = columns.size();
        for (int x = 0; x < width; x++) {
            table.setText(0, x, columns.getColumnName(x));
        }
        for (int y = 0; y < height; y++) {
            Record record = records.get(y);
            for (int x = 0; x < width; x++) {
                Object value = record.get(x);
                Column column = columns.get(x);
                ILabelField label = fieldFactory.createLabelField(column);
                table.setWidget(y + 1, x, label.widget(value, column));
            }
            table.setWidget(y + 1, width, new ShowButton(showDialog, record));
            table.setWidget(y + 1, width + 1, new EditButton(editDialog, record));
            table.setWidget(y + 1, width + 2, new DeleteButton(deleteDialog, record));
        }
        table.setWidget(height + 1, width, new NewButton(newDialog));
    }

    //
    public Column addColumn(String columnName, String typeName) {
        return addColumn(columnName, typeName, "one");
    }

    public Column addColumn(String columnName, String typeName,
            String multiplicity) {
        Column column = new DatatypeColumn(columnName, typeName, multiplicity);
        return addColumn(column);
    }

    public Column addColumn(Column column) {
        columns.add(column);
        return column;
    }

    public int getLength() {
        return records.length();
    }

    public Object getValue(String columnName, int index) {
        return records.get(columnName, index);
    }

    public Integer getValue_asInteger(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return new Integer(value.toString());
        }
    }

    public Long getValue_asLong(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return new Long(value.toString());
        }
    }

    public List<Integer> getValue_asIntegerList(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            List<Integer> result = new ArrayList<Integer>();
            List<Object> list = (List<Object>) value;
            for (Object elem : list) {
                result.add(new Integer(elem.toString()));
            }
            return result;
        }
    }

    public String getValue_asString(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
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

    class ShowButton extends Button {
        public ShowButton(final ShowDialog showDialog, final Record record) {
            setText("Show");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    showDialog.execute(event, record);
                }
            });
        }
    }

    class EditButton extends Button {
        public EditButton(final EditDialog editDialog, final Record record) {
            setText("Edit");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    editDialog.execute(event, record);
                }
            });
        }
    }

    class DeleteButton extends Button {
        public DeleteButton(final DeleteDialog deleteDialog, final Record record) {
            setText("Delete");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    deleteDialog.execute(event, record);
                }
            });
        }
    }

    class NewDialog extends DialogBox {
        private DockPanel mainPanel = new DockPanel();
        private Button okButton = new Button("Ok");
        private Button cancelButton = new Button("Cancel");
        private FlexTable table = null;
        private List<IInputField> inputFields = new ArrayList<IInputField>();

        NewDialog() {
            addStyleName("newDialog");
            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (table != null) {
                        mainPanel.remove(table);
                    }
                    hide();
                    Record record = records.create();
                    for (int i = 0; i < inputFields.size(); i++) {
                        record.set(i, inputFields.get(i).getValue());
                    }
                    for (int i = 0; i < inputFields.size(); i++) {
                        inputFields.get(i).clearValue();
                    }
                    redraw();
                }
            });
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (table != null) {
                        mainPanel.remove(table);
                    }
                    hide();
                }
            });
            mainPanel.addStyleName("dialogPanel");
            setText("New");
            setAnimationEnabled(true);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event) {
            inputFields.clear();
            for (Column column : columns) {
                inputFields.add(fieldFactory.createInputField(column));
            }
            setText(getLabelNew());
            table = new FlexTable();
            table.addStyleName("datasheet");
            table.getRowFormatter().addStyleName(0, "datasheetHeader");
            table.setText(0, 0, getLabelName());
            table.setText(0, 1, getLabelValue());
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                table.setText(i + 1, 0, column.columnName);
                table.setWidget(i + 1, 1, inputFields.get(i).widget());
            }
            mainPanel.add(table, DockPanel.CENTER);
            center();
            // update_candidates_customerId();
        }
        /*
         * private void update_candidates_customerId() { GwtQuery query = new
         * GwtQuery(); service.listCustomer(query, new
         * AsyncCallback<Collection<GwtDEACustomer>>() {
         * 
         * @Override public void onSuccess(Collection<GwtDEACustomer> docs) {
         * ArrayList<EntityReferenceCandidate> candidates = new
         * ArrayList<EntityReferenceCandidate>(); for (GwtDEACustomer doc :
         * docs) { String caption = doc.phone + " (" + doc.customerId + ")";
         * candidates.add(new EntityReferenceCandidate( caption,
         * doc.customerId)); } input_customerId.setCandidates(candidates); }
         * 
         * @Override public void onFailure(Throwable caught) { } });
         */
    }

    class ShowDialog extends DialogBox {
        private DockPanel mainPanel = new DockPanel();
        private Button closeButton = new Button("Close");
        private FlexTable table = null;
        private List<ILabelField> labelFields = new ArrayList<ILabelField>();

        ShowDialog() {
            addStyleName("showDialog");
            closeButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (table != null) {
                        mainPanel.remove(table);
                    }
                    hide();
                }
            });
            mainPanel.addStyleName("dialogPanel");
            setText("Show");
            setAnimationEnabled(true);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(closeButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event, Record record) {
            labelFields.clear();
            for (Column column : columns) {
                labelFields.add(fieldFactory.createLabelField(column));
            }
            setText(getLabelShow(record));
            table = new FlexTable();
            table.addStyleName("datasheet");
            table.getRowFormatter().addStyleName(0, "datasheetHeader");
            table.setText(0, 0, getLabelName());
            table.setText(0, 1, getLabelValue());
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                table.setText(i + 1, 0, column.columnName);
                table.setWidget(i + 1, 1, labelFields.get(i).widget(record.get(i),
                        column));
            }
            mainPanel.add(table, DockPanel.CENTER);
            center();
        }
    }

    class EditDialog extends DialogBox {
        private DockPanel mainPanel = new DockPanel();
        private Button okButton = new Button("Ok");
        private Button cancelButton = new Button("Cancel");
        private FlexTable table = null;
        private List<IInputField> inputFields = new ArrayList<IInputField>();
        private Record record = null;

        EditDialog() {
            addStyleName("editDialog");
            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (table != null) {
                        mainPanel.remove(table);
                    }
                    hide();
                    for (int i = 0; i < inputFields.size(); i++) {
                        record.set(i, inputFields.get(i).getValue());
                    }
                    for (int i = 0; i < inputFields.size(); i++) {
                        inputFields.get(i).clearValue();
                    }
                    record = null;
                    redraw();
                }
            });
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (table != null) {
                        mainPanel.remove(table);
                    }
                    hide();
                    record = null;
                }
            });
            mainPanel.addStyleName("dialogPanel");
            setText("Edit");
            setAnimationEnabled(true);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event, Record record) {
            this.record = record;
            inputFields.clear();
            for (Column column : columns) {
                inputFields.add(fieldFactory.createInputField(column));
            }
            setText(getLabelEdit(record));
            table = new FlexTable();
            table.addStyleName("datasheet");
            table.getRowFormatter().addStyleName(0, "datasheetHeader");
            table.setText(0, 0, getLabelName());
            table.setText(0, 1, getLabelValue());
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                table.setText(i + 1, 0, column.columnName);
                table.setWidget(i + 1, 1, inputFields.get(i).widget(record.get(i),
                        column));
            }
            mainPanel.add(table, DockPanel.CENTER);
            center();
            // update_candidates_customerId();
        }

        /*
         * private void update_candidates_customerId() { GwtQuery query = new
         * GwtQuery(); service.listCustomer(query, new
         * AsyncCallback<Collection<GwtDEACustomer>>() {
         * 
         * @Override public void onSuccess(Collection<GwtDEACustomer> docs) {
         * ArrayList<EntityReferenceCandidate> candidates = new
         * ArrayList<EntityReferenceCandidate>(); for (GwtDEACustomer doc: docs)
         * { String caption = doc.phone + " (" + doc.customerId + ")";
         * candidates.add(new EntityReferenceCandidate(caption,
         * doc.customerId)); } input_customerId.setCandidates(candidates); }
         * 
         * @Override public void onFailure(Throwable caught) { } }); }
         */
    }

    class DeleteDialog extends DialogBox {
        private DockPanel mainPanel = new DockPanel();
        private Button okButton = new Button("Ok");
        private Button cancelButton = new Button("Cancel");
        private FlexTable table = null;
        private Record record = null;
        private List<ILabelField> labelFields = new ArrayList<ILabelField>();

        DeleteDialog() {
            addStyleName("showDialog");
            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    records.remove(record);
                    if (table != null) {
                        mainPanel.remove(table);
                    }
                    hide();
                    redraw();
                }
            });
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (table != null) {
                        mainPanel.remove(table);
                    }
                    hide();
                }
            });
            mainPanel.addStyleName("dialogPanel");
            setText("Delete");
            setAnimationEnabled(true);
            HorizontalPanel southPanel = new HorizontalPanel();
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            mainPanel.add(southPanel, DockPanel.SOUTH);
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event, Record record) {
            this.record = record;
            labelFields.clear();
            for (Column column : columns) {
                labelFields.add(fieldFactory.createLabelField(column));
            }
            setText(getLabelDelete(record));
            table = new FlexTable();
            table.addStyleName("datasheet");
            table.setText(0, 0, getLabelName());
            table.setText(0, 1, getLabelValue());
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                table.setText(i + 1, 0, column.columnName);
                table.setWidget(i + 1, 1, labelFields.get(i).widget(record.get(i),
                        column));
            }
            mainPanel.add(table, DockPanel.CENTER);
            center();

        }
    }

    //
    public String getStringValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    public Boolean getBooleanValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2boolean(value);
        }
    }

    public Byte getByteValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2byte(value);
        }
    }

    public Short getShortValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2short(value);
        }
    }

    public Integer getIntValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2int(value);
        }
    }

    public Long getLongValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2long(value);
        }
    }

    public Float getFloatValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2float(value);
        }
    }

    public Double getDoubleValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2double(value);
        }
    }

    public String getIntegerValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2integer(value);
        }
    }

    public String getDecimalValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2decimal(value);
        }
    }

    public Date getDateTimeValue(String columnName, int index) {
        Object value = getValue(columnName, index);
        if (value == null) {
            return null;
        } else {
            return Util.object2dateTime(value);
        }
    }

    public List<String> getStringListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<String> result = new ArrayList<String>();
        while (iter.hasNext()) {
            result.add(iter.next().toString());
        }
        return result;
    }

    public List<Boolean> getBooleanListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<Boolean> result = new ArrayList<Boolean>();
        while (iter.hasNext()) {
            result.add(Util.object2boolean(iter.next()));
        }
        return result;
    }

    public List<Byte> getByteListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<Byte> result = new ArrayList<Byte>();
        while (iter.hasNext()) {
            result.add(Util.object2byte(iter.next()));
        }
        return result;
    }

    public List<Short> getShortListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<Short> result = new ArrayList<Short>();
        while (iter.hasNext()) {
            result.add(Util.object2short(iter.next()));
        }
        return result;
    }

    public List<Integer> getIntListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<Integer> result = new ArrayList<Integer>();
        while (iter.hasNext()) {
            result.add(Util.object2int(iter.next()));
        }
        return result;
    }

    public List<Long> getLongListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<Long> result = new ArrayList<Long>();
        while (iter.hasNext()) {
            result.add(Util.object2long(iter.next()));
        }
        return result;
    }

    public List<Float> getFloatListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<Float> result = new ArrayList<Float>();
        while (iter.hasNext()) {
            result.add(Util.object2float(iter.next()));
        }
        return result;
    }

    public List<Double> getDoubleListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<Double> result = new ArrayList<Double>();
        while (iter.hasNext()) {
            result.add(Util.object2double(iter.next()));
        }
        return result;
    }

    public List<String> getIntegerListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<String> result = new ArrayList<String>();
        while (iter.hasNext()) {
            result.add(Util.object2integer(iter.next()));
        }
        return result;
    }

    public List<String> getDecimalListValue(String columnName, int index) {
        Iterable<Object> iterable = (Iterable<Object>)getValue(columnName, index);
        Iterator<Object> iter = iterable.iterator();
        List<String> result = new ArrayList<String>();
        while (iter.hasNext()) {
            result.add(Util.object2decimal(iter.next()));
        }
        return result;
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
