package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Jun. 12, 2009
 * @version Jul. 18, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtEntityReferenceSetInputFieldEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

public class EntityReferenceSetInputField extends ValueSetInputFieldBase {
    private EntityReferenceEditor editor;

    @Override
    protected IValueEditor createEditor() {
        if (editor == null) {
            editor = new EntityReferenceEditor();            
        }
        return editor;
    }

    public void setCandidateItems(Iterable<String> candidates) {
        editor.setCandidateItems(candidates);
    }

    public void setCandidates(Iterable<EntityReferenceCandidate> candidates) {
        editor.setCandidates(candidates);
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
