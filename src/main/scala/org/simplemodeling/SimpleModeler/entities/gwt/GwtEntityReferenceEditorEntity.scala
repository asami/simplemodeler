package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jul. 18, 2009
 * @version Jul. 18, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtEntityReferenceEditorEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EntityReferenceEditor extends VerticalPanel implements
        IValueEditor {

    private ListBox entities = new ListBox(true);
    
    public EntityReferenceEditor() {
        add(entities);
        entities.setVisibleItemCount(10);
    }

    public void setCandidateItems(Iterable<String> candidates) {
        entities.clear();
        for (String item : candidates) {
            entities.addItem(item, item);
        }
    }

    public void setCandidates(Iterable<EntityReferenceCandidate> candidates) {
        entities.clear();
        for (EntityReferenceCandidate candidate : candidates) {
            entities.addItem(candidate.item, candidate.value);
        }
    }

    public List<String> getStringListValue() {
        List<String> values = new ArrayList<String>();
        int length = entities.getItemCount();
        for (int i = 0;i < length;i++) {
            if (entities.isItemSelected(i)) {
                values.add(entities.getValue(i));
            }
        }
        return values;
    }

    @Override
    public void clearValue() {
        int length = entities.getItemCount();
        for (int i = 0;i < length;i++) {
            entities.setItemSelected(i, false);
        }
    }

    @Override
    public String getTextValue() {
        StringBuilder buf = new StringBuilder();
        Iterator<String> iter = getStringListValue().iterator();
        if (iter.hasNext()) {
            buf.append(iter.next());
            while (iter.hasNext()) {
                buf.append(", ");
                buf.append(iter.next());
            }
        }
        return buf.toString();
    }

    @Override
    public Object getValue() {
        return getStringListValue();
    }

    private void setItem(String value) {
        int length = entities.getItemCount();
        for (int i = 0;i < length;i++) {
            if (value.equals(entities.getValue(i))) {
                entities.setItemSelected(i, true);
                return;
            }
        }
        throw new IllegalArgumentException(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) {
        if (value == null) {
            return;
        } else {
            List<String> list = (List<String>)value;
            for (String name: list) {
                setItem(name);
            }
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
