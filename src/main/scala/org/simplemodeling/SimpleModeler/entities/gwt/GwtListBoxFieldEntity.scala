package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Apr. 19, 2009
 * @version Jul.  6, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtListBoxFieldEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class ListBoxField extends HorizontalPanel {
  private final TextBox idText = new TextBox();
  private final PushButton searchButton = new PushButton("Search");
  private final SearchDialog searchDialog = new SearchDialog(idText);

  public static class Candidate {
    public String item;
    public String value;
    
    public Candidate(String item, String value) {
      this.item = item;
      this.value = value;
    }
  }
  
  public ListBoxField() {
    searchButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        searchDialog.execute(event);
      }
    });
    add(idText);
    add(searchButton);
  }

  public String getValue() {
    return idText.getValue(); 
  }

  public void setValue(String text) {
    idText.setText(text);
  }

  public void clearValue() {
    setValue(null)
  }

  public void setCandidateItems(Iterable<String> candidates) {
    searchDialog.setCandidateItems(candidates);
  }

  public void setCandidates(Iterable<Candidate> candidates) {
    searchDialog.setCandidates(candidates);
  }

  class SearchDialog extends DialogBox {
    private final DockPanel mainPanel = new DockPanel();
    private final ListBox entities = new ListBox();
    private final Button okButton = new Button("Ok");
    private final Button cancelButton = new Button("Cancel");
    private final HasText target;

    public SearchDialog(HasText target) {
      this.target = target;
      setText("Search");
      okButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          int index = entities.getSelectedIndex();
          SearchDialog.this.target.setText(entities.getValue(index));
          hide();
        }
      });
      cancelButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          hide();
        }
      });
      setAnimationEnabled(true);
      entities.setVisibleItemCount(10);
      mainPanel.add(entities, DockPanel.CENTER);
      HorizontalPanel southPanel = new HorizontalPanel();
      southPanel.add(okButton);
      southPanel.add(cancelButton);
      mainPanel.add(southPanel, DockPanel.SOUTH);
      mainPanel.setCellHorizontalAlignment(southPanel, DockPanel.ALIGN_RIGHT);
      setWidget(mainPanel);
    }

    public void execute(ClickEvent event) {
      center();
    }

    public void setCandidateItems(Iterable<String> candidates) {
      entities.clear();
      for (String item: candidates) {
        entities.addItem(item, item);
      }
    }

    public void setCandidates(Iterable<Candidate> candidates) {
      entities.clear();
      for (Candidate candidate: candidates) {
        entities.addItem(candidate.item, candidate.value);
      }
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
