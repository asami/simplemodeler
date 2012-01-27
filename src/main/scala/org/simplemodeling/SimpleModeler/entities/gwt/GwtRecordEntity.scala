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
class GwtRecordEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

public class Record {
    private ColumnSet columnSet;
    private List<Object> values = new ArrayList<Object>();

    public Record(ColumnSet columnSet) {
        this.columnSet = columnSet;
    }
    
    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Object get(int index) {
        if (index >= values.size()) {
            return null;
        } else {
            return values.get(index);
        }
    }

    public Object get(String columnName) {
        return get(columnSet.getColumnNumber(columnName));
    }

    public void set(int index, Object value) {
        int size = values.size();
        if (size == index) {
            values.add(value);
        } else if (index < size) {
            values.remove(index);
            values.add(index, value);
        } else {
            for (int i = size;i < index;i++) {
                values.add(null);
            }
            values.add(value);
        }
    }

    public void set(String columnName, Object value) {
        set(columnSet.getColumnNumber(columnName), value);
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
