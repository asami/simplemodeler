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
class GwtCompositeColumnEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

public abstract class CompositeColumn extends Column {
	private List<Column> columns = new ArrayList<Column>();
	private D2Array<Object> data = new D2Array<Object>();
	
	public CompositeColumn(String columnName, String multiplicity) {
		super(columnName, multiplicity);
	}

	public abstract void setup();

	  //
	  public Column addColumn(String columnName, String typeName) {
		  return addColumn(columnName, typeName, "one");
	  }

	  public Column addColumn(String columnName, String typeName, String multiplicity) {
		  Column column = new DatatypeColumn(columnName, typeName, multiplicity);
		  return addColumn(column);
	  }

	  public Column addColumn(Column column) {
		  	columns.add(column);
		  	return column;
	  }

	  public int getLength() {
		return columns.size();
	  }

	  private int getColumnNumber(String columnName) {
		  for (int i = 0;i < columns.size();i++) {
			  Column column = columns.get(i);
			  if (column.columnName.equals(columnName)) {
				  return i;
			  }
		  }
		  throw new IllegalArgumentException("No column = " + columnName);
	  }

	  public Object getValue(int index, String columnName) {
		  return data.get(getColumnNumber(columnName), index);	  
	  }

	  public Integer getValue_asInteger(int index, String columnName) {
		  Object value = getValue(index, columnName);
		  if (value == null) {
			  return null;
		  } else {
			  return new Integer(value.toString());
		  }
	  }

	  public Long getValue_asLong(int index, String columnName) {
		  Object value = getValue(index, columnName);
		  if (value == null) {
			  return null;
		  } else {
			  return new Long(value.toString());
		  }
	  }

	  public List<Integer> getValue_asIntegerList(int index, String columnName) {
		  Object value = getValue(index, columnName);
		  if (value == null) {
			  return null;
		  } else {
			  List<Integer> result = new ArrayList<Integer>();
			  List<Object> list = (List<Object>)value;
			  for (Object elem: list) {
				  result.add(new Integer(elem.toString()));
			  }
			  return result;
		  }
	  }

	  public String getValue_asString(int index, String columnName) {
		  Object value = getValue(index, columnName);
		  if (value == null) {
			  return null;
		  } else {
			  return value.toString();
		  }
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
