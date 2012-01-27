package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import com.asamioffice.goldenport.text.{AppendableTextBuilder, UString, UJavaString, StringTextBuilder, TextBuilder, Templater}

/*
 * @since   Apr. 19, 2009
 * @version Jul. 15, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtEntityReferenceInputFieldEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
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
import java.util.Date;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class EntityReferenceInputField extends HorizontalPanel implements
        IInputField {
    private final TextBox idText = new TextBox();
    private final PushButton searchButton = new PushButton("Search");
    private final SearchDialog searchDialog = new SearchDialog(idText);

    public EntityReferenceInputField() {
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
        setValue(null);
    }

    public void setCandidateItems(Iterable<String> candidates) {
        searchDialog.setCandidateItems(candidates);
    }

    public void setCandidates(Iterable<EntityReferenceCandidate> candidates) {
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
            mainPanel.setCellHorizontalAlignment(southPanel,
                    DockPanel.ALIGN_RIGHT);
            setWidget(mainPanel);
        }

        public void execute(ClickEvent event) {
            center();
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
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            setValue((String) null);
        } else {
            setValue(value.toString());
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

    @Override
    public String getStringValue() {
        return idText.getValue();
    }

    @Override
    public Boolean getBooleanValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Byte getByteValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Short getShortValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getIntValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getLongValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Float getFloatValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double getDoubleValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIntegerValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDecimalValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDateValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getTimeValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDateTimeValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getStringListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Boolean> getBooleanListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Byte> getByteListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Short> getShortListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> getIntListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Long> getLongListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Float> getFloatListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Double> getDoubleListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getDecimalListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getIntegerListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Date> getDateListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Date> getTimeListValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Date> getDateTimeListValue() {
        throw new UnsupportedOperationException();
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
