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
class GwtRecordSetEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

public class RecordSet {
    private ColumnSet columns; 
    private List<Record> records = new ArrayList<Record>();

    public RecordSet(ColumnSet columns) {
        this.columns = columns;
    }

    public Record update(int index) {
        if (index >= records.size()) {
            Record record = new Record(columns);
            for (int i = records.size();i < index;i++) {
                records.add(null);
            }
            records.add(index, record);
            return record;
        } else {
            return get(index);
        }
    }

    public Record create() {
        Record record = new Record(columns);
        records.add(record);
        return record;
    }

    public void clear() {
        records.clear();
    }

    public int length() {
        return records.size();
    }

    public Record get(int index) {
        if (index >= records.size()) {
            return null;
        } else {
            return records.get(index);
        }
    }

    public Object get(String columnName, int index) {
        Record record = get(index);
        if (record == null) {
            return null;
        } else {
            return record.get(columnName);
        }
    }

    public void remove(Record record) {
        records.remove(record);
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
