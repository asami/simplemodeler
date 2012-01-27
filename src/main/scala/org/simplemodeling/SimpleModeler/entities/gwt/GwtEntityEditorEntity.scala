package org.simplemodeling.SimpleModeler.entities.gwt

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource
import org.simplemodeling.SimpleModeler.entities.gaej._
import com.asamioffice.goldenport.text.{UString, JavaTextMaker}

/*
 * @since   Apr. 16, 2009
 * @version Nov. 13, 2009
 * @author  ASAMI, Tomoharu
 */
class GwtEntityEditorEntity(val gaejEntity: GaejEntityEntity, val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  override def is_Text_Output = true

  override protected def open_Entity_Create() {
  }

  var packageName = ""
  private def attributes = gaejEntity.attributes
  private def gwtServiceName = gwtContext.entityRepositoryServiceName(gaejEntity.packageName)
  private def gwtDocumentName = gwtContext.documentName(gaejEntity)
  private def gwtKeyType = gwtContext.keyTypeName(gaejEntity)
  private val idName = gaejEntity.idName
  private val capitalizedTerm = gwtContext.gaejContext.entityBaseName(gaejEntity)
  private def queryOperation = "query" + capitalizedTerm
  private def createOperation = "create" + capitalizedTerm
  private def updateOperation = "update" + capitalizedTerm
  private def deleteOperation = "delete" + capitalizedTerm
  private def entryPointId = gwtContext.entryPointId(gaejEntity)

  val editor = """package %packageName%;

import java.util.*;
import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class %editorName% implements EntryPoint {
  private DockPanel mainPanel = new DockPanel();
  private FlexTable indexTable = new FlexTable();
  private NewDialog newDialog = new NewDialog(); 
  private ShowDialog showDialog = new ShowDialog();
  private EditDialog editDialog = new EditDialog();
  private DeleteDialog deleteDialog = new DeleteDialog();
  private List<%gwtDoc%> docs = new ArrayList<%gwtDoc%>();
  private %serviceName%Async service = GWT.create(%serviceName%.class);

  public %editorName%() {
  }

  public String getTitleNew() {
    return "New %capitalizedTerm%";
  }

  public String getTitleShow(%gwtDoc% gwtDoc) {
    return "Show %capitalizedTerm% - " + gwtDoc.%idName%;
  }

  public String getTitleEdit(%gwtDoc% gwtDoc) {
    return "Edit %capitalizedTerm% - " + gwtDoc.%idName%;
  }

  public String getTitleDelete(%gwtDoc% gwtDoc) {
    return "Delete %capitalizedTerm% - " + gwtDoc.%idName%;
  }

  @Override
  public void onModuleLoad() {
    RootPanel root = RootPanel.get("%entryPointId%");
    if (root == null) return;
    indexTable.addStyleName("datasheet");
    indexTable.getRowFormatter().addStyleName(0, "datasheetHeader");
    %set_header%
    reload();
    mainPanel.add(indexTable, DockPanel.CENTER);
//    FlowPanel northPanel = new FlowPanel();
//    northPanel.add(new ReloadButton());
//    northPanel.add(new NewButton(newDialog));
//    mainPanel.add(northPanel, DockPanel.NORTH);
    root.add(mainPanel);
  }

  public void reload() {
    GwtQuery query = new GwtQuery();
    service.%queryOperation%(query, new AsyncCallback<Collection<%gwtDoc%>>() {
      @Override
      public void onSuccess(Collection<%gwtDoc%> result) {
        docs.clear();
        docs.addAll(result);
        redraw();
      }
      @Override
      public void onFailure(Throwable caught) {
      }
    });
  }

  public void redraw() {
    for (int i = indexTable.getRowCount() - 1;i > 0;i--) {
      indexTable.removeRow(1);
    }
    int y = 0;
    for (%gwtDoc% doc : docs) {
      y++;
      %set_record%
    }
    %set_new_button%
  }

  public void addRecord(%gwtDoc% doc) {
    service.%createOperation%(doc, new AsyncCallback<%gwtDoc%>() {
      @Override
      public void onSuccess(%gwtDoc% result) {
      }

      @Override
      public void onFailure(Throwable caught) {
      }
    });
    for (int i = 0;i < docs.size();i++) {
      %gwtDoc% d = docs.get(i);
      if (d.%idName%.equals(doc.%idName%)) {
        throw new IllegalArgumentException(); // XXX
      }
    }
    docs.add(doc);
    redraw();
  }

  public void updateRecord(%gwtDoc% newDoc) {
    service.%updateOperation%(newDoc, new AsyncCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
      }

      @Override
      public void onFailure(Throwable caught) {
      }
    });
    %gwtKeyType% key = newDoc.%idName%;
    for (%gwtDoc% doc: docs) {
      if (doc.%idName%.equals(key)) {
        %update_doc_in_updateRecord%
        break;
      }
    }
    redraw();
  }

  public void removeRecord(%gwtKeyType% key) {
    service.%deleteOperation%(key, new AsyncCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
        
      }
    });
    for (%gwtDoc% doc: docs) {
      if (doc.%idName%.equals(key)) {
        docs.remove(doc);
        break;
      }
    }
    redraw();
  }

  class NewButton extends Button {
    public NewButton(final NewDialog newDialog) {
      setText("New");
      addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          newDialog.execute(event);
        }
      });
    }
  }

  class ReloadButton extends Button {
    public ReloadButton() {
      setText("Reload");
      addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          reload();
        }
      });
    }
  }

  class ShowButton extends Button {
    public ShowButton(final ShowDialog showDialog, final %gwtDoc% doc) {
      setText("Show");
      addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          showDialog.execute(event, doc);
        }
      });
    }
  }

  class EditButton extends Button {
    public EditButton(final EditDialog editDialog, final %gwtDoc% doc) {
      setText("Edit");
      addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          editDialog.execute(event, doc);
        }
      });
    }
  }

  class DeleteButton extends Button {
    public DeleteButton(final DeleteDialog deleteDialog, final %gwtDoc% doc) {
      setText("Delete");
      addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          deleteDialog.execute(event, doc);
        }
      });
    }
  }

  class NewDialog extends DialogBox {
    private DockPanel mainPanel = new DockPanel();
    private Button okButton = new Button("Ok");
    private Button cancelButton = new Button("Cancel");
    private FlexTable table = null;
%declare_inputs_in_new%

    NewDialog() {
      addStyleName("newDialog");
      okButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (table != null) {
            mainPanel.remove(table);
          }
          hide();
          %gwtDoc% doc = new %gwtDoc%();
          %execute_ok_in_new%
        }
      });
      cancelButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (table != null) {
            mainPanel.remove(table);
          }
          hide();
        }
      });
      mainPanel.addStyleName("dialogPanel");
      setText("New");
      setAnimationEnabled(true);
%initialize_inputs_in_new%
      HorizontalPanel southPanel = new HorizontalPanel();
      southPanel.add(okButton);
      southPanel.add(cancelButton);
      mainPanel.add(southPanel, DockPanel.SOUTH);
      mainPanel.setCellHorizontalAlignment(southPanel, DockPanel.ALIGN_RIGHT);
      setWidget(mainPanel);
    }

    public void execute(ClickEvent event) {
      %execute_launch_set_in_new%
      setText(getTitleNew());
      table = new FlexTable();
      table.addStyleName("datasheet");
      table.getRowFormatter().addStyleName(0, "datasheetHeader");
      table.setText(0, 0, "Name");
      table.setText(0, 1, "Value");
      %execute_launch_in_new%
      mainPanel.add(table, DockPanel.CENTER);
      center();
      %update_eref_candidates%
    }
%update_eref_candidates_methods%
  }

  class ShowDialog extends DialogBox {
    private DockPanel mainPanel = new DockPanel();
    private Button closeButton = new Button("Close");
    private FlexTable table = null;

    ShowDialog() {
      addStyleName("showDialog");
      closeButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (table != null) {
            mainPanel.remove(table);
          }
          hide();
        }
      });
      mainPanel.addStyleName("dialogPanel");
      setText("Show");
      setAnimationEnabled(true);
      HorizontalPanel southPanel = new HorizontalPanel();
      southPanel.add(closeButton);
      mainPanel.add(southPanel, DockPanel.SOUTH);
      mainPanel.setCellHorizontalAlignment(southPanel, DockPanel.ALIGN_RIGHT);
      setWidget(mainPanel);
    }

    public void execute(ClickEvent event, %gwtDoc% gwtDoc) {
      setText(getTitleShow(gwtDoc));
      table = new FlexTable();
      table.addStyleName("datasheet");
      table.getRowFormatter().addStyleName(0, "datasheetHeader");
      table.setText(0, 0, "Name");
      table.setText(0, 1, "Value");
      %execute_launch_in_show%
      mainPanel.add(table, DockPanel.CENTER);
      center();
    }
  }

  class EditDialog extends DialogBox {
    private DockPanel mainPanel = new DockPanel();
    private Button okButton = new Button("Ok");
    private Button cancelButton = new Button("Cancel");
    private FlexTable table = null;
    %declare_inputs_in_edit%
    private %gwtDoc% doc = null;

    EditDialog() {
      addStyleName("editDialog");
      okButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (table != null) {
            mainPanel.remove(table);
          }
          hide();
          %gwtDoc% newDoc = new %gwtDoc%();
          %execute_ok_in_edit%
        }
      });
      cancelButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (table != null) {
            mainPanel.remove(table);
          }
          hide();
        }
      });
      mainPanel.addStyleName("dialogPanel");
      setText("Edit");
      setAnimationEnabled(true);
%initialize_inputs_in_edit%
      HorizontalPanel southPanel = new HorizontalPanel();
      southPanel.add(okButton);
      southPanel.add(cancelButton);
      mainPanel.add(southPanel, DockPanel.SOUTH);
      mainPanel.setCellHorizontalAlignment(southPanel, DockPanel.ALIGN_RIGHT);
      setWidget(mainPanel);
    }

    public void execute(ClickEvent event, %gwtDoc% gwtDoc) {
      doc = gwtDoc;
      %execute_launch_set_in_edit%
      setText(getTitleEdit(gwtDoc));
      table = new FlexTable();
      table.addStyleName("datasheet");
      table.getRowFormatter().addStyleName(0, "datasheetHeader");
      table.setText(0, 0, "Name");
      table.setText(0, 1, "Value");
      %execute_launch_in_edit%
      mainPanel.add(table, DockPanel.CENTER);
      center();
      %update_eref_candidates%
    }
%update_eref_candidates_methods%
  }

  class DeleteDialog extends DialogBox {
    private DockPanel mainPanel = new DockPanel();
    private Button okButton = new Button("Ok");
    private Button cancelButton = new Button("Cancel");
    private FlexTable table = null;
    private %gwtDoc% doc = null;

    DeleteDialog() {
      addStyleName("showDialog");
      okButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (table != null) {
            mainPanel.remove(table);
          }
          hide();
          %execute_ok_in_delete%
        }
      });
      cancelButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (table != null) {
            mainPanel.remove(table);
          }
          hide();
        }
      });
      mainPanel.addStyleName("dialogPanel");
      setText("Show");
      setAnimationEnabled(true);
      HorizontalPanel southPanel = new HorizontalPanel();
      southPanel.add(okButton);
      southPanel.add(cancelButton);
      mainPanel.add(southPanel, DockPanel.SOUTH);
      mainPanel.setCellHorizontalAlignment(southPanel, DockPanel.ALIGN_RIGHT);
      setWidget(mainPanel);
    }

    public void execute(ClickEvent event, %gwtDoc% gwtDoc) {
      doc = gwtDoc;
      setText(getTitleDelete(gwtDoc));
      table = new FlexTable();
      table.addStyleName("datasheet");
      table.getRowFormatter().addStyleName(0, "datasheetHeader");
      table.setText(0, 0, "Name");
      table.setText(0, 1, "Value");
      %execute_launch_in_delete%
      mainPanel.add(table, DockPanel.CENTER);
      center();
    }
  }
%build_part_methods%
}
"""

  override protected def write_Content(out: BufferedWriter) {
    val maker = new JavaTextMaker(
      editor,
      Map("%packageName%" -> packageName,
          "%editorName%" -> name,
          "%serviceName%" -> gwtServiceName,
          "%gwtDoc%" -> gwtDocumentName,
          "%idName%" -> idName,
          "%queryOperation%" -> queryOperation,
          "%createOperation%" -> createOperation,
          "%updateOperation%" -> updateOperation,
          "%deleteOperation%" -> deleteOperation,
	  "%capitalizedTerm%" -> gaejEntity.term.capitalize,
          "%entryPointId%" -> entryPointId,
          "%gwtKeyType%" -> gwtKeyType
        ))
    maker.replace("%update_doc_in_updateRecord%")(update_doc_in_updateRecord)
    maker.replace("%set_header%")(set_header)
    maker.replace("%set_record%")(set_record)
    maker.replace("%set_new_button%")(set_new_button)
    maker.replace("%declare_inputs_in_new%")(declare_inputs_in_new)
    maker.replace("%initialize_inputs_in_new%")(initialize_inputs_in_new)
    maker.replace("%execute_ok_in_new%")(execute_ok_in_new)
    maker.replace("%execute_launch_set_in_new%")(execute_launch_set_in_new)
    maker.replace("%execute_launch_in_new%")(execute_launch_in_new)
    maker.replace("%execute_launch_in_show%")(execute_launch_in_show)
    maker.replace("%declare_inputs_in_edit%")(declare_inputs_in_edit)
    maker.replace("%initialize_inputs_in_edit%")(initialize_inputs_in_edit)
    maker.replace("%execute_ok_in_edit%")(execute_ok_in_edit)
    maker.replace("%execute_launch_set_in_edit%")(execute_launch_set_in_edit)
    maker.replace("%execute_launch_in_edit%")(execute_launch_in_edit)
    maker.replace("%execute_ok_in_delete%")(execute_ok_in_delete)
    maker.replace("%execute_launch_in_delete%")(execute_launch_in_delete)
    maker.replace("%update_eref_candidates%")(update_eref_candidates)
    maker.replace("%update_eref_candidates_methods%")(update_eref_candidates_methods)
    maker.replace("%build_part_methods%")(build_part_methods)
    out.append(maker.toString)
    out.flush()
  }

  private def update_doc_in_updateRecord(buffer: JavaTextMaker) {
    for (attr <- attributes if !attr.isId) {
      doc_to_doc(attr, "newDoc", "doc", buffer)
    }
  }
  
  private def set_header(buffer: JavaTextMaker) {
    var index = 0;
    for (attr <- attributes) {
      buffer.print("indexTable.setText(0, ")
      buffer.print(index)
      buffer.print(", \"")
      buffer.print(var_name(attr))
      buffer.println("\");")
      index += 1
    }
    buffer.print("indexTable.setText(0, ")
    buffer.print(index);
    buffer.println(" , \"\");")
    index += 1
    buffer.print("indexTable.setText(0, ")
    buffer.print(index);
    buffer.println(" , \"\");")
    index += 1
    buffer.print("indexTable.setWidget(0, ")
    buffer.print(index);
    buffer.println(" , new ReloadButton());")
  }

  private def set_record(buffer: JavaTextMaker) {
    var index = 0;
    for (attr <- attributes) {
      attr.attributeType match {
        case t: GaejDateTimeType => { // unify execute_launch_in_show
          buffer.print("indexTable.setWidget(y, ")
          buffer.print(index)
          buffer.print(", new DateTimeLabelField(doc.")
          buffer.print(var_name(attr))
          buffer.println("));")
        }
        case _ => {
          buffer.print("indexTable.setText(y, ")
          buffer.print(index)
          buffer.print(", doc.get")
          buffer.print(var_name(attr).capitalize)
          buffer.println("_asString());")
        }
      }
      index += 1
    }
    buffer.print("indexTable.setWidget(y, ")
    buffer.print(index);
    buffer.println(" , new ShowButton(showDialog, doc));")
    index += 1
    buffer.print("indexTable.setWidget(y, ")
    buffer.print(index);
    buffer.println(" , new EditButton(editDialog, doc));")
    index += 1
    buffer.print("indexTable.setWidget(y, ")
    buffer.print(index);
    buffer.println(" , new DeleteButton(deleteDialog, doc));")
  }

  private def set_new_button(buffer: JavaTextMaker) {
    buffer.print("indexTable.setWidget(y + 1, ")
    buffer.print(attributes.length + 2);
    buffer.println(" , new NewButton(newDialog));")
  }

  private def declare_inputs_in_new(buffer: JavaTextMaker) {
    for (attr <- attributes) {
      declare_inputs_editable(attr, buffer)
    }
  }

  private def initialize_inputs_in_new(buffer: JavaTextMaker) {
    for (attr <- attributes) {
      initialize_inputs_editable(attr, buffer)
    }
  }

  private def execute_ok_in_new(buffer: JavaTextMaker) {
    for (attr <- attributes) {
      execute_ok_update_attribute(attr, "doc", buffer)
    }
    buffer.println("addRecord(doc);")
    for (attr <- attributes) {
      execute_ok_clear_attribute(attr, buffer)
    }
  }

  private def execute_ok_update_attribute(attr: GaejAttribute, doc: String, buffer: JavaTextMaker) {
    attr.attributeType match {
      case t: GaejDateTimeType => {
        buffer.print(doc)
	buffer.print(".")
	buffer.print(var_name(attr))
	buffer.print(" = input_")
	buffer.print(var_name(attr))
	buffer.println(".getValue();")
      }
      case e: GaejEntityType => {
        if (attr.isHasMany) {
          buffer.print(doc)
	  buffer.print(".")
	  buffer.print(var_name(attr))
	  buffer.println(".clear();")
          buffer.print(doc)
	  buffer.print(".")
	  buffer.print(var_name(attr))
	  buffer.print(".addAll(input_")
	  buffer.print(var_name(attr))
	  buffer.print(".get")
          buffer.print(gwtContext.gaejContext.attributeTypeName4RefId(attr))
          buffer.println("ListValue());")
        } else {
          buffer.print(doc)
	  buffer.print(".")
	  buffer.print(var_name(attr))
	  buffer.print(" = input_")
	  buffer.print(var_name(attr))
	  buffer.print(".get")
          buffer.print(gwtContext.gaejContext.attributeTypeName4RefId(attr))
          buffer.println("Value();")
        }
      }
      case p: GaejEntityPartType => {
        if (attr.isHasMany) {
          buffer.print(doc)
	  buffer.print(".")
	  buffer.print(var_name(attr))
	  buffer.print(".addAll(build_")
	  buffer.print(var_name(attr))
	  buffer.print("(input_")
          buffer.print(var_name(attr))
          buffer.println("));")
        } else {
          buffer.print(doc)
	  buffer.print(".")
	  buffer.print(var_name(attr))
	  buffer.print(" = build_")
	  buffer.print(var_name(attr))
	  buffer.print("(input_")
          buffer.print(var_name(attr))
          buffer.println(");")
        }
      }
      case _ => {
        if (attr.isHasMany) {
          buffer.print(doc)
	  buffer.print(".")
	  buffer.print(var_name(attr))
	  buffer.println(".clear();")
          buffer.print(doc)
	  buffer.print(".")
	  buffer.print(var_name(attr))
	  buffer.print(".addAll(input_")
	  buffer.print(var_name(attr))
	  buffer.print(".get")
          buffer.print(attr.attributeType.xmlDatatypeName.capitalize)
          buffer.println("ListValue());")
        } else {
          buffer.print(doc)
	  buffer.print(".")
	  buffer.print(var_name(attr))
	  buffer.print(" = input_")
	  buffer.print(var_name(attr))
	  buffer.print(".get")
          buffer.print(attr.attributeType.xmlDatatypeName.capitalize)
          buffer.println("Value();")
        }
      }
    }
  }

  private def execute_ok_clear_attribute(attr: GaejAttribute, buffer: JavaTextMaker) {
    attr.attributeType match {
      case p: GaejEntityPartType => {
        buffer.print("input_")
        buffer.print(var_name(attr))
        buffer.println(".clearValue();")
      }
      case _ => {
        buffer.print("input_")
        buffer.print(var_name(attr))
        buffer.println(".setValue(null);")
      }
    }
  }

  private def execute_launch_set_in_new(buffer: JavaTextMaker) {
    for (attr <- attributes if !attr.isId) {
      attr.attributeType match {
        case p: GaejEntityPartType => {
          buffer.print("input_")
          buffer.print(var_name(attr))
          buffer.println(".redraw();")
        }
        case _ => {}
      }
    }
  }

  private def execute_launch_in_new(buffer: JavaTextMaker) {
    var index = 0
    for (attr <- attributes) {
      index += 1
      buffer.print("table.setText(")
      buffer.print(index)
      buffer.print(", 0, \"")
      buffer.print(var_name(attr))
      buffer.println("\");")
      buffer.print("table.setWidget(")
      buffer.print(index)
      buffer.print(", 1, input_")
      buffer.print(var_name(attr))
      buffer.println(");")
    }
  }

  private def execute_launch_in_show(buffer: JavaTextMaker) {
    var index = 0
    for (attr <- attributes) {
      def make_set_widget(labelWidget: String) {
        buffer.print("table.setWidget(")
        buffer.print(index)
        buffer.print(", 1, new ")
        buffer.print(labelWidget)
        buffer.print("(gwtDoc.")
        buffer.print(var_name(attr))
        buffer.println("));")
      }

      index += 1
      buffer.print("table.setText(")
      buffer.print(index)
      buffer.print(", 0, \"")
      buffer.print(var_name(attr))
      buffer.println("\");")
      attr.attributeType match {
        case t: GaejDateTimeType => make_set_widget("DateTimeLabelField")
        case t: GaejDateType => make_set_widget("DateLabelField")
        case t: GaejTimeType => make_set_widget("TimeLabelField")
        case _ => {
          buffer.print("table.setText(")
          buffer.print(index)
          buffer.print(", 1, gwtDoc.get")
          buffer.print(var_name(attr).capitalize)
          buffer.println("_asString());")
        }
      }
    }
  }

  private def declare_inputs_in_edit(buffer: JavaTextMaker) {
    for (attr <- attributes if !attr.isId) {
      declare_inputs_editable(attr, buffer)
    }
  }

  private def declare_inputs_editable(attr: GaejAttribute, buffer: JavaTextMaker) {
    val widgetName = if (attr.isHasMany) {
      attr.attributeType match {
        case t: GaejBooleanType => "BooleanSetInputField"
        case t: GaejByteType => "ByteSetInputField"
        case t: GaejShortType => "ShortSetInputField"
        case t: GaejIntType => "IntSetInputField"
        case t: GaejLongType => "LongSetInputField"
        case t: GaejFloatType => "FloatSetInputField"
        case t: GaejDoubleType => "DoubleSetInputField"
        case t: GaejIntegerType => "IntegerSetInputField"
        case t: GaejDecimalType => "DecimalSetInputField"
        case t: GaejDateTimeType => "DateTimeSetInputField"
        case t: GaejDateType => "DateSetInputField"
        case t: GaejTimeType => "TimeSetInputField"
        case e: GaejEntityType => "EntityReferenceSetInputField"
        case p: GaejEntityPartType => "RecordSetInputField"
        case _ => "StringSetInputField"
      }
    } else {
      attr.attributeType match {
        case t: GaejBooleanType => "BooleanInputField"
        case t: GaejByteType => "ByteInputField"
        case t: GaejShortType => "ShortInputField"
        case t: GaejIntType => "IntInputField"
        case t: GaejLongType => "LongInputField"
        case t: GaejFloatType => "FloatInputField"
        case t: GaejDoubleType => "DoubleInputField"
        case t: GaejIntegerType => "IntegerInputField"
        case t: GaejDecimalType => "DecimalInputField"
        case t: GaejDateTimeType => "DateTimeInputField"
        case t: GaejDateType => "DateInputField"
        case t: GaejTimeType => "TimeInputField"
        case e: GaejEntityType => "EntityReferenceInputField"
        case p: GaejEntityPartType => "RecordInputField"
        case _ => "StringInputField"
      }
    }
    val attrName = var_name(attr)
    buffer.print("private ")
    buffer.print(widgetName)
    buffer.print(" input_")
    buffer.print(attrName)
    buffer.print(" = new ")
    buffer.print(widgetName)
    buffer.println("();")
  }

  private def initialize_inputs_in_edit(buffer: JavaTextMaker) {
    for (attr <- attributes if !attr.isId) {
      initialize_inputs_editable(attr, buffer)
    }
  }

  private def initialize_inputs_editable(attr: GaejAttribute, buffer: JavaTextMaker) {
    val attrName = var_name(attr)
    val widget = "input_" + attrName

    def initialize_part(part: GaejEntityPartEntity) {
      def multiplicity(attr: GaejAttribute) = attr.multiplicity match {
        case m: GaejOne => "\"one\""
        case m: GaejZeroOne => "\"zeroOne\""
        case m: GaejOneMore => "\"oneMore\""
        case m: GaejZeroMore => "\"zeroMore\""
      }

      def make_composite(internalPart: GaejEntityPartEntity) {
        for (internalPartAttr <- internalPart.attributes) {
          internalPartAttr.attributeType match {
            case e: GaejEntityType => {
              buffer.print("addColumn(\"")
              buffer.print(internalPartAttr.name)
              buffer.print("\", \"")
              buffer.print(e.entity.qualifiedName)
              buffer.print("\", ")
              buffer.print(multiplicity(internalPartAttr))
              buffer.println(");")
            }
            case p: GaejEntityPartType => {
              buffer.print("addColumn(\"")
              buffer.print(internalPartAttr.name)
              buffer.println("\", new CompositeColumn() {")
              buffer.indentUp
              buffer.println("@Override")
              buffer.method("public void setup()") {
                make_composite(p.part)
              }
              buffer.indentDown
              buffer.print("}, ")
              buffer.print(multiplicity(internalPartAttr))
              buffer.println(");")
            }
            case _ => {
              buffer.print("addColumn(\"")
              buffer.print(internalPartAttr.name)
              buffer.print("\", \"")
              buffer.print(internalPartAttr.elementTypeName)
              buffer.print("\", ")
              buffer.print(multiplicity(internalPartAttr))
              buffer.println(");")
            }
          }
        }
      }

      for (partAttr <- part.attributes) {
        partAttr.attributeType match {
          case e: GaejEntityType => {
            buffer.print(widget)
            buffer.print(".addColumn(\"")
            buffer.print(partAttr.name)
            buffer.print("\", \"")
            buffer.print(e.entity.qualifiedName)
            buffer.print("\", ")
            buffer.print(multiplicity(partAttr))
            buffer.println(");")
          }
          case p: GaejEntityPartType => {
            buffer.print(widget)
            buffer.print(".addColumn(new CompositeColumn(\"")
            buffer.print(partAttr.name)
            buffer.print("\", ")
            buffer.print(multiplicity(partAttr))
            buffer.println(") {")
            buffer.indentUp
            buffer.println("@Override")
            buffer.method("public void setup()") {
              make_composite(p.part)
            }
            buffer.indentDown
            buffer.println(");")
          }
          case _ => {
            buffer.print(widget)
            buffer.print(".addColumn(\"")
            buffer.print(partAttr.name)
            buffer.print("\", \"")
            buffer.print(partAttr.elementTypeName)
            buffer.print("\", ")
            buffer.print(multiplicity(partAttr))
            buffer.println(");")
          }
        }
      }
    }

    attr.attributeType match {
      case p: GaejEntityPartType => initialize_part(p.part)
      case _ => {}
    }
    for ((_, constraint) <- attr.constraints) {
      buffer.print(widget)
      buffer.print(".setConstraint(\"")
      buffer.print(constraint.name)
      buffer.print("\", ")
      buffer.print(constraint.literal)
      buffer.println(");")
    }
  }

  private def execute_ok_in_edit(buffer: JavaTextMaker) {
    for (attr <- attributes) {
      if (attr.isId) {
	buffer.print("newDoc.")
	buffer.print(var_name(attr))
	buffer.print(" = doc.")
	buffer.print(var_name(attr))
	buffer.println(";")
      } else {
        execute_ok_update_attribute(attr, "newDoc", buffer)
/* 2009-07-05
	attr.attributeType match {
	  case t: GaejDateTimeType => {
	    buffer.print("newDoc.")
	    buffer.print(var_name(attr))
	    buffer.print(" = input_")
	    buffer.print(var_name(attr))
	    buffer.println(".getValue();")
	  }
          case p: GaejEntityPartType => {
            if (attr.isHasMany) {
	      buffer.print("newDoc.")
	      buffer.print(var_name(attr))
	      buffer.print(".addAll(build_")
	      buffer.print(var_name(attr))
	      buffer.print("(input_")
              buffer.print(var_name(attr))
              buffer.println("));")
            } else {
	      buffer.print("newDoc.")
	      buffer.print(var_name(attr))
	      buffer.print(" = build_")
	      buffer.print(var_name(attr))
	      buffer.print("(input_")
              buffer.print(var_name(attr))
              buffer.println(");")
            }
          }
	  case _ => {
	    buffer.print("newDoc.")
	    buffer.print(var_name(attr))
	    buffer.print(" = input_")
	    buffer.print(var_name(attr))
	    buffer.println(".getValue();")
	  }
	}
*/
      }
    }
    buffer.println("updateRecord(newDoc);")
    for (attr <- attributes if !attr.isId) {
      execute_ok_clear_attribute(attr, buffer)
    }
  }

  private def execute_launch_set_in_edit(buffer: JavaTextMaker) {
    for (attr <- attributes if !attr.isId) {
      def make_many {
        attr.attributeType match {
          case e: GaejEntityType => {
          }
          case p: GaejEntityPartType => {
            buffer.print("input_")
            buffer.print(var_name(attr))
            buffer.println(".clearValue();")
            buffer.makeFor("int i = 0;i < gwtDoc." + var_name(attr) + ".size();i++") {
              for (partAttr <- p.part.attributes) {
                partAttr.attributeType match {
                  case pp: GaejEntityPartType => error("not implemented yet")
                  case _ => {
                    buffer.print("input_")
                    buffer.print(var_name(attr))
                    buffer.print(".setValue(")
                    buffer.makeString(partAttr.name)
                    buffer.print(", i, gwtDoc.")
                    buffer.print(var_name(attr))
                    buffer.print(".get(i).")
                    buffer.print(var_name(partAttr))
                    buffer.println(");")
//                    buffer.print(".get(i).get") 2009-07-11
//                    buffer.print(var_name(partAttr).capitalize)
//                    buffer.println("_asString());")
                  }
                }
              }
            }
            buffer.print("input_")
            buffer.print(var_name(attr))
            buffer.println(".redraw();")
          }
          case _ => {
            buffer.print("input_")
            buffer.print(var_name(attr))
            buffer.print(".setValue(gwtDoc.get")
            buffer.print(var_name(attr).capitalize)
            buffer.println("_asStringList());")
          }
        }
      }

      def make_one {
        attr.attributeType match {
          case p: GaejEntityPartType => {
            for (partAttr <- p.part.attributes) {
              partAttr.attributeType match {
                case pp: GaejEntityPartType => error("not implemented yet")
                case _ => {
                  buffer.print("input_")
                  buffer.print(var_name(attr))
                  buffer.print(".setValue(")
                  buffer.makeString(partAttr.name)
                  buffer.print(", gwtDoc.")
                  buffer.print(var_name(attr))
                  buffer.print(".")
                  buffer.print(var_name(partAttr))
                  buffer.println(");")
//                  buffer.print(".get") 2009-07-11
//                  buffer.print(var_name(partAttr).capitalize)
//                  buffer.println("_asString());")
                }
              }
            }
            buffer.print("input_")
            buffer.print(var_name(attr))
            buffer.println(".redraw();")
          }
          case _ => {
            buffer.print("input_")
            buffer.print(var_name(attr))
            buffer.print(".setValue(gwtDoc.get")
            buffer.print(var_name(attr).capitalize)
            buffer.println("_asString());")
          }
        }
      }

      if (attr.isHasMany) make_many else make_one
    }
  }

  private def execute_launch_in_edit(buffer: JavaTextMaker) {
    var index = 0
    for (attr <- attributes) {
      index += 1
      if (attr.isId) {
	buffer.print("table.setText(")
	buffer.print(index)
	buffer.print(", 0, \"")
	buffer.print(var_name(attr))
	buffer.println("\");")
	buffer.print("table.setText(")
	buffer.print(index)
	buffer.print(", 1, gwtDoc.get")
	buffer.print(var_name(attr).capitalize)
	buffer.println("_asString());")
      } else {
	buffer.print("table.setText(")
	buffer.print(index)
	buffer.print(", 0, \"")
	buffer.print(var_name(attr))
	buffer.println("\");")
	buffer.print("table.setWidget(")
	buffer.print(index)
	buffer.print(", 1, input_")
	buffer.print(var_name(attr))
	buffer.println(");")
      }
    }
  }

  private def execute_ok_in_delete(buffer: JavaTextMaker) {
    buffer.print("removeRecord(doc.")
    buffer.print(gaejEntity.idName)
    buffer.println(");")
  }

  private def execute_launch_in_delete(buffer: JavaTextMaker) {
    execute_launch_in_show(buffer)
  }
    
  private def var_name(attr: GaejAttribute): String = {
    attr.attributeType match {
      case e: GaejEntityType => gwtContext.gaejContext.variableName4RefId(attr)
      case _ => gwtContext.gaejContext.variableName(attr)
    }
  }

  private def doc_to_doc(attr: GaejAttribute, in: String, out: String, buffer: JavaTextMaker) {
    doc_to_doc_by_name(var_name(attr), in, out, buffer)
  }

  private def doc_to_doc_by_name(name: String, in: String, out: String, buffer: JavaTextMaker) {
    buffer.print(out);
    buffer.print(".");
    buffer.print(name);
    buffer.print(" = ");
    buffer.print(in);
    buffer.print(".");
    buffer.print(name);
    buffer.println(";")
  }

  private def update_eref_candidates(buffer: JavaTextMaker) {
    for (attr <- attributes) {
      attr.attributeType match {
        case e: GaejEntityType => {
          buffer.print("update_candidates_")
          buffer.print(gwtContext.gaejContext.variableName4RefId(attr))
          buffer.println("();")
        }
        case _ => //
      }
    }
  }

  private def update_eref_candidates_methods(buffer: JavaTextMaker) {
    val method = """  private void update_candidates_%refName%() {
    GwtQuery query = new GwtQuery();
    service.%queryOperation%(query, new AsyncCallback<Collection<%gwtDoc%>>() {
      @Override
      public void onSuccess(Collection<%gwtDoc%> docs) {
        ArrayList<EntityReferenceCandidate> candidates = new ArrayList<EntityReferenceCandidate>();
        for (%gwtDoc% doc: docs) {
          String caption = doc.entity_title_asString();
          candidates.add(new EntityReferenceCandidate(caption, doc.%idName%));
        }
        input_%refName%.setCandidates(candidates);
      }

      @Override
      public void onFailure(Throwable caught) {
      }
    });
  }
"""

    for (attr <- attributes) {
      attr.attributeType match {
        case e: GaejEntityType => {
          def gwt_doc = gwtContext.documentName(e.entity)
          def id_name = e.entity.idName
          def query_operation = "query" + gwtContext.gaejContext.entityBaseName(e.entity)

          buffer.append(method, Map("%gwtDoc%" -> gwt_doc,
                                    "%refName%" -> gwtContext.gaejContext.variableName4RefId(attr),
                                    "%idName%" -> id_name,
                                    "%queryOperation%" -> query_operation
                                  ))
        }
        case _ => //
      }
    }
  }

  private def build_part_methods(buffer: JavaTextMaker) {
    for (attr <- attributes) {
      def build_part_method_one(part: GaejEntityPartEntity) {
        val docName = gwtContext.documentName(part)
        val typeName = docName
        buffer.method("private " + typeName + " build_" + attr.name + "(RecordInputField input)") {
          buffer.makeIf("input.isEmpty()") {
            buffer.makeReturn("null")
          }
          buffer.makeVar("doc", docName, "new " + docName + "()")
          for (partAttr <- part.attributes) {
            if (partAttr.isHasMany) {
              partAttr.attributeType match {
                case e: GaejEntityType => {
                  buffer.print("doc.")
                  buffer.print(e.entity.idName)
                  buffer.print(" = input.get")
                  buffer.print(gwtContext.gaejContext.attributeTypeName4RefId(attr))
                  buffer.print("ListValue(")
                  buffer.print("\"")
                  buffer.print(partAttr.name)
                  buffer.print("\"")
                  buffer.println(");")
                }
                case p: GaejEntityPartType => {
                  error("not implemented yet")
                }
                case _ => {
//                  println("GwtEntityEditorEntity: xmlDatatypeName(om) = " + partAttr.xmlDatatypeName.capitalize) 2009-10-24
                  buffer.print("doc.")
                  buffer.print(partAttr.name)
                  buffer.print(" = input.get")
                  buffer.print(partAttr.xmlDatatypeName.capitalize)
                  buffer.print("ListValue(")
                  buffer.print("\"")
                  buffer.print(partAttr.name)
                  buffer.print("\"")
                  buffer.println(");")
                }
              }
            } else {
              partAttr.attributeType match {
                case e: GaejEntityType => {
                  buffer.print("doc.")
                  buffer.print(e.entity.idName)
                  buffer.print(" = input.get")
                  buffer.print(gwtContext.gaejContext.attributeTypeName4RefId(attr))
                  buffer.print("Value")
                  buffer.print("(")
                  buffer.print("\"")
                  buffer.print(partAttr.name)
                  buffer.print("\"")
                  buffer.println(");")
                }
                case p: GaejEntityPartType => {
                  error("not implemented yet")
                }
                case _ => {
//                  println("GwtEntityEditorEntity: xmlDatatypeName(oo) = " + partAttr.xmlDatatypeName.capitalize) 2009-10-25
                  buffer.print("doc.")
                  buffer.print(partAttr.name)
                  buffer.print(" = input.get")
                  buffer.print(partAttr.xmlDatatypeName.capitalize)
                  buffer.print("Value(")
                  buffer.print("\"")
                  buffer.print(partAttr.name)
                  buffer.print("\"")
                  buffer.println(");")
                }
              }
            }
          }
          buffer.makeReturn("doc")
        }
      }

      def build_part_method_many(part: GaejEntityPartEntity) {
        val docName = gwtContext.documentName(part)
        val typeName = "List<" + docName + ">"
        val containerName = "ArrayList<" + docName + ">"
        buffer.method("private " + typeName + " build_" + attr.name + "(RecordSetInputField input)") {
          buffer.makeVar("list", typeName, "new " + containerName + "()")
          buffer.makeFor("int i = 0;i < input.getLength();i++") {
            buffer.makeVar("doc", docName, "new " + docName + "()")
            for (partAttr <- part.attributes) {
              if (partAttr.isHasMany) {
                partAttr.attributeType match {
                  case e: GaejEntityType => {
                    buffer.print("doc.")
                    buffer.print(e.entity.idName)
                    buffer.print(" = input.get")
                    buffer.print("String")
                    buffer.print("ListValue(")
                    buffer.print("\"")
                    buffer.print(partAttr.name)
                    buffer.print("\"")
                    buffer.println(", i);")
                  }
                  case p: GaejEntityPartType => {
                    error("not implemented yet")
                  }
                  case _ => {
//                    println("GwtEntityEditorEntity: xmlDatatypeName(mm) = " + partAttr.xmlDatatypeName.capitalize) 2009-10-25
                    buffer.print("doc.")
                    buffer.print(partAttr.name)
                    buffer.print(" = input.get")
                    buffer.print(partAttr.xmlDatatypeName.capitalize)
                    buffer.print("ListValue(")
                    buffer.print("\"")
                    buffer.print(partAttr.name)
                    buffer.print("\"")
                    buffer.println(", i);")
                  }
                }
              } else {
                partAttr.attributeType match {
                  case e: GaejEntityType => {
                    buffer.print("doc.")
                    buffer.print(e.entity.idName)
                    buffer.print(" = input.get")
                    buffer.print("StringValue")
                    buffer.print("(")
                    buffer.print("\"")
                    buffer.print(partAttr.name)
                    buffer.print("\"")
                    buffer.println(", i);")
                  }
                  case p: GaejEntityPartType => {
                    error("not implemented yet")
                  }
                  case _ => {
//                    println("GwtEntityEditorEntity: xmlDatatypeName(mo) = " + partAttr.xmlDatatypeName.capitalize) 2009-10-25
                    buffer.print("doc.")
                    buffer.print(partAttr.name)
                    buffer.print(" = input.get")
                    buffer.print(partAttr.xmlDatatypeName.capitalize)
                    buffer.print("Value(")
                    buffer.print("\"")
                    buffer.print(partAttr.name)
                    buffer.print("\"")
                    buffer.println(", i);")
                  }
                }
              }
            }
            buffer.println("list.add(doc);")
          }
          buffer.makeReturn("list")
        }
      }

      attr.attributeType match {
        case p: GaejEntityPartType => {
          if (attr.isHasMany) build_part_method_many(p.part)
          else build_part_method_one(p.part)
        }
        case _ => {}
      }
    }
  }
}
