package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jul. 15, 2009
 * @version Jul. 18, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtRecordEditorEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RecordEditor extends VerticalPanel {
    private final ColumnSet columns;
    private Record record;
    private FlexTable table = null;
    private List<IInputField> inputFields = new ArrayList<IInputField>();
    private FieldFactory fieldFactory = new FieldFactory();

    public RecordEditor(ColumnSet columns) {
        this.columns = columns;
        this.record = new Record(columns);
    }

    public Record update() {
        return record;
    }

    public void redraw() {
        inputFields.clear();
        for (Column column : columns) {
            inputFields.add(fieldFactory.createInputField(column));
        }
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
    }

    public String getLabelName() {
        return "Name";
    }

    private String getLabelValue() {
        return "Name";
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

    //
    public boolean isEmpty() {
        return record.isEmpty();
    }

    public Record getRecord() {
        if (record.isEmpty()) {
            return null;
        } else {
            return record;
        }
    }

    public void setValue(String columnName, Object value) {
        record.set(columnName, value);
    }

    public void clearValue() {
        record = new Record(columns);
    }

    public Object getValue(String columnName) {
        return record.get(columnName);
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
