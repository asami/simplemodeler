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
class GwtFieldFactoryEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

public class FieldFactory {
    public IInputField createInputField(Column column) {
        if (column instanceof DatatypeColumn) {
            return createDatatypeInputField((DatatypeColumn)column);
        } else if (column instanceof CompositeColumn) {
            return createCompositeInputField((CompositeColumn)column);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private IInputField createDatatypeInputField(DatatypeColumn column) {
        String typeName = column.typeName;
        if (column.isSet()) {
            if ("string".equals(typeName)) {
                return new StringSetInputField();
            } else if ("boolean".equals(typeName.toLowerCase())) {
                return new BooleanSetInputField();
            } else if ("byte".equals(typeName.toLowerCase())) {
                return new ByteSetInputField();
            } else if ("short".equals(typeName.toLowerCase())) {
                return new ShortSetInputField();
            } else if ("int".equals(typeName) || "Integer".equals(typeName)) {
                return new IntSetInputField();
            } else if ("long".equals(typeName.toLowerCase())) {
                return new LongSetInputField();
            } else if ("float".equals(typeName.toLowerCase())) {
                return new FloatSetInputField();
            } else if ("double".equals(typeName.toLowerCase())) {
                return new DoubleSetInputField();
            } else if ("integer".equals(typeName.toLowerCase())) {
                return new IntegerSetInputField();
            } else if ("decimal".equals(typeName.toLowerCase())) {
                return new DecimalSetInputField();
            } else if ("date".equals(typeName)) {
                return new StringSetInputField(); // XXX
            } else if ("time".equals(typeName)) {
                return new StringSetInputField(); // XXX
            } else if ("dateTime".equals(typeName)) {
                return new StringSetInputField(); // XXX
            } else {
                return new StringSetInputField();
            }
        } else {
            if ("string".equals(typeName)) {
                return new StringInputField();
            } else if ("boolean".equals(typeName.toLowerCase())) {
                return new BooleanInputField();
            } else if ("byte".equals(typeName.toLowerCase())) {
                return new ByteInputField();
            } else if ("short".equals(typeName.toLowerCase())) {
                return new ShortInputField();
            } else if ("int".equals(typeName) || "Integer".equals(typeName)) {
                return new IntInputField();
            } else if ("long".equals(typeName.toLowerCase())) {
                return new LongInputField();
            } else if ("float".equals(typeName.toLowerCase())) {
                return new FloatInputField();
            } else if ("double".equals(typeName.toLowerCase())) {
                return new DoubleInputField();
            } else if ("integer".equals(typeName.toLowerCase())) {
                return new IntegerInputField();
            } else if ("decimal".equals(typeName.toLowerCase())) {
                return new DecimalInputField();
            } else if ("date".equals(typeName.toLowerCase())) {
                return new DateInputField();
            } else if ("time".equals(typeName.toLowerCase())) {
                return new TimeInputField();
            } else if ("dateTime".equals(typeName.toLowerCase())) {
                return new DateTimeInputField();
            } else {
                return new StringInputField();
            }
        }
    }

    private IInputField createCompositeInputField(CompositeColumn column) {
        return null; // XXX
    }

    public ILabelField createLabelField(Column column) {
        if (column instanceof DatatypeColumn) {
            return createDatatypeLabelField((DatatypeColumn)column);
        } else if (column instanceof CompositeColumn) {
            return createCompositeLabelField((CompositeColumn)column);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private ILabelField createDatatypeLabelField(DatatypeColumn column) {
        String typeName = column.typeName;
        if (column.isSet()) {
            if ("string".equals(typeName)) {
                return new StringSetLabelField();
            } else if ("boolean".equals(typeName.toLowerCase())) {
                return new BooleanSetLabelField();
            } else if ("byte".equals(typeName.toLowerCase())) {
                return new ByteSetLabelField();
            } else if ("short".equals(typeName.toLowerCase())) {
                return new ShortSetLabelField();
            } else if ("int".equals(typeName) || "Integer".equals(typeName)) {
                return new IntSetLabelField();
            } else if ("long".equals(typeName.toLowerCase())) {
                return new LongSetLabelField();
            } else if ("float".equals(typeName.toLowerCase())) {
                return new FloatSetLabelField();
            } else if ("double".equals(typeName.toLowerCase())) {
                return new DoubleSetLabelField();
            } else if ("integer".equals(typeName.toLowerCase())) {
                return new IntegerSetLabelField();
            } else if ("decimal".equals(typeName.toLowerCase())) {
                return new DecimalSetLabelField();
            } else if ("date".equals(typeName.toLowerCase())) {
                return new StringSetLabelField();
            } else if ("time".equals(typeName)) {
                return new StringSetLabelField();
            } else if ("dateTime".equals(typeName)) {
                return new StringLabelField();
            } else {
                return new StringSetLabelField();
            }
        } else {
            if ("string".equals(typeName)) {
                return new StringLabelField();
            } else if ("boolean".equals(typeName)) {
                return new BooleanLabelField();
            } else if ("byte".equals(typeName.toLowerCase())) {
                return new ByteLabelField();
            } else if ("short".equals(typeName.toLowerCase())) {
                return new ShortLabelField();
            } else if ("int".equals(typeName) || "Integer".equals(typeName)) {
                return new IntLabelField();
            } else if ("long".equals(typeName.toLowerCase())) {
                return new LongLabelField();
            } else if ("float".equals(typeName.toLowerCase())) {
                return new FloatLabelField();
            } else if ("double".equals(typeName.toLowerCase())) {
                return new DoubleLabelField();
            } else if ("integer".equals(typeName.toLowerCase())) {
                return new IntegerLabelField();
            } else if ("decimal".equals(typeName.toLowerCase())) {
                return new DecimalLabelField();
            } else if ("date".equals(typeName)) {
                return new DateLabelField();
            } else if ("time".equals(typeName)) {
                return new TimeLabelField();
            } else if ("dateTime".equals(typeName)) {
                return new DateTimeLabelField();
            } else {
                return new StringLabelField();
            }
        }
    }

    private ILabelField createCompositeLabelField(CompositeColumn column) {
        return null; // XXX
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
