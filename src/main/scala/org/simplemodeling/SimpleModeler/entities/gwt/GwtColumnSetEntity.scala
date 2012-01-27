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
class GwtColumnSetEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

public class ColumnSet implements Iterable<Column> {
	private List<Column> columns = new ArrayList<Column>();

	public int size() {
		return columns.size();
	}

	public void add(Column column) {
		columns .add(column);
	}

	public Column get(int index) {
		return columns.get(index);
	}

	public String getColumnName(int index) {
		return columns.get(index).columnName;
	}

	public int getColumnNumber(String columnName) {
		for (int i = 0;i < columns.size();i++) {
			Column column = columns.get(i);
			if (column.columnName.equals(columnName)) {
				return i;
			}
		}
		throw new IllegalArgumentException("No column = " + columnName);
	}

	@Override
	public Iterator<Column> iterator() {
		return columns.iterator();
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
