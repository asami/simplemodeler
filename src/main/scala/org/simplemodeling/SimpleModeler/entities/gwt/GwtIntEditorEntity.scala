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
class GwtIntEditorEntity(val gwtContext: GwtEntityContext) extends GEntity(gwtContext) {
  type DataSource_TYPE = GDataSource
  override def is_Text_Output = true

  var packageName: String = ""

  override protected def open_Entity_Create() {
  }

  override protected def open_Entity_Update(aDataSource: GDataSource) {
    error("not implemented yet")
  }

  val code = """package %packageName%;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class IntEditor extends VerticalPanel implements IValueEditor {
	private Integer value;
	private final Label textLabel = new Label();
	private final TextBox textBox = new TextBox();
	private final RadioButton choiceZero = new RadioButton("numbers", "0");
	private final RadioButton choiceOne = new RadioButton("numbers", "1");
	private final RadioButton choiceTwo = new RadioButton("numbers", "2");
	private final RadioButton choiceThree = new RadioButton("numbers", "3");
	private final RadioButton choiceFour = new RadioButton("numbers", "4");
	private final RadioButton choiceFive = new RadioButton("numbers", "5");
	private final RadioButton choiceSix = new RadioButton("numbers", "6");
	private final RadioButton choiceSeven = new RadioButton("numbers", "7");
	private final RadioButton choiceEight = new RadioButton("numbers", "8");
	private final RadioButton choiceNine = new RadioButton("numbers", "9");
	private final RadioButton choiceTen = new RadioButton("numbers", "10");
	private final RadioButton choiceMinusOne = new RadioButton("numbers", "-1");
	private final RadioButton choiceMax = new RadioButton("numbers", "Max(" + Integer.MAX_VALUE + ")");
	private final RadioButton choiceMin = new RadioButton("numbers", "Min(" + Integer.MIN_VALUE + ")");

	public IntEditor() {
		setValue(1);
		textLabel.setHorizontalAlignment(Label.ALIGN_RIGHT);
		add(textLabel);
		textBox.setTextAlignment(TextBox.ALIGN_RIGHT);
		textBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = event.getCharCode();
				if (!(c == '\n' || c == '\r')) {
					return;
				}
				choiceZero.setValue(false);
				choiceOne.setValue(false);
				choiceTwo.setValue(false);
				choiceThree.setValue(false);
				choiceFour.setValue(false);
				choiceFive.setValue(false);
				choiceSix.setValue(false);
				choiceSeven.setValue(false);
				choiceEight.setValue(false);
				choiceNine.setValue(false);
				choiceTen.setValue(false);
				choiceMinusOne.setValue(false);
				choiceMax.setValue(false);
				choiceMin.setValue(false);
				value = Integer.parseInt(textBox.getValue());
				textLabel.setText(value.toString());
			}
		});
/*
		textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				choiceZero.setValue(false);
				choiceOne.setValue(false);
				choiceTwo.setValue(false);
				choiceThree.setValue(false);
				choiceFour.setValue(false);
				choiceFive.setValue(false);
				choiceSix.setValue(false);
				choiceSeven.setValue(false);
				choiceEight.setValue(false);
				choiceNine.setValue(false);
				choiceTen.setValue(false);
				choiceMinusOne.setValue(false);
				choiceMax.setValue(false);
				choiceMin.setValue(false);
				value = Integer.parseInt(event.getValue());
				textLabel.setText(value.toString());
				switch (value.intValue()) {
				case 0: choiceZero.setValue(true); break;
				case 1: choiceOne.setValue(true); break;
				case 2: choiceTwo.setValue(true); break;
				case 3: choiceThree.setValue(true); break;
				case 4: choiceFour.setValue(true); break;
				case 5: choiceFive.setValue(true); break;
				case 6: choiceSix.setValue(true); break;
				case 7: choiceSeven.setValue(true); break;
				case 8: choiceEight.setValue(true); break;
				case 9: choiceNine.setValue(true); break;
				case 10: choiceTen.setValue(true); break;
				case -1: choiceMinusOne.setValue(true); break;
				case Integer.MAX_VALUE: choiceMax.setValue(true); break;
				case Integer.MIN_VALUE: choiceMin.setValue(true); break;
				default: {}
				}
			}
		});
*/
		add(textBox);
		choiceOne.setValue(true);
		choiceZero.addValueChangeHandler(new SelectChoice(0));
		choiceOne.addValueChangeHandler(new SelectChoice(1));
		choiceTwo.addValueChangeHandler(new SelectChoice(2));
		choiceThree.addValueChangeHandler(new SelectChoice(3));
		choiceFour.addValueChangeHandler(new SelectChoice(4));
		choiceFive.addValueChangeHandler(new SelectChoice(5));
		choiceSix.addValueChangeHandler(new SelectChoice(6));
		choiceSeven.addValueChangeHandler(new SelectChoice(7));
		choiceEight.addValueChangeHandler(new SelectChoice(8));
		choiceNine.addValueChangeHandler(new SelectChoice(9));
		choiceTen.addValueChangeHandler(new SelectChoice(10));
		choiceMinusOne.addValueChangeHandler(new SelectChoice(-1));
		choiceMax.addValueChangeHandler(new SelectChoice(Integer.MAX_VALUE));
		choiceMin.addValueChangeHandler(new SelectChoice(Integer.MIN_VALUE));
		HorizontalPanel numbers = new HorizontalPanel();
		numbers.add(choiceZero);
		numbers.add(choiceOne);
		numbers.add(choiceTwo);
		numbers.add(choiceThree);
		numbers.add(choiceFour);
		numbers.add(choiceFive);
		numbers.add(choiceSix);
		numbers.add(choiceSeven);
		numbers.add(choiceEight);
		numbers.add(choiceNine);
		numbers.add(choiceTen);
		add(numbers);
		HorizontalPanel specialNumbers = new HorizontalPanel();
		specialNumbers.add(choiceMinusOne);
		specialNumbers.add(choiceMax);
		specialNumbers.add(choiceMin);
		add(specialNumbers);
	}

	class SelectChoice implements ValueChangeHandler<Boolean> {
		private int choice;

		SelectChoice(int choice) {
			this.choice = choice;
		}

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			setValue(choice);
			textBox.setValue(null);
		}
	}

	@Override
	public void clearValue() {
		textBox.setValue(null);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getTextValue() {
		if (value == null) {
			return null;
		} else {
			return value.toString();
		}
	}

/*
//	@Override
	public Object getValue() {
		String value = textBox.getValue();
		if (value != null && !"".equals(value)) {
			return value;
		}
		if (choiceZero.getValue()) {
			return new Integer(0);
		} else if (choiceOne.getValue()) {
			return new Integer(1);
		} else if (choiceTwo.getValue()) {
			return new Integer(1);
		} else if (choiceThree.getValue()) {
			return new Integer(1);
		} else if (choiceFour.getValue()) {
			return new Integer(1);
		} else if (choiceFive.getValue()) {
			return new Integer(1);
		} else if (choiceSix.getValue()) {
			return new Integer(1);
		} else if (choiceSeven.getValue()) {
			return new Integer(1);
		} else if (choiceEight.getValue()) {
			return new Integer(1);
		} else if (choiceNine.getValue()) {
			return new Integer(1);
		} else if (choiceTen.getValue()) {
			return new Integer(1);
		} else if (choiceMinusOne.getValue()) {
			return new Integer(1);
		} else if (choiceMax.getValue()) {
			return new Integer(1);
		} else if (choiceMin.getValue()) {
			return new Integer(1);
		} else {
			return new Integer(999);
		}
	}
*/

	@Override
	public void setValue(Object value) {
		this.value = (Integer)value;
		if (value == null) {
			textLabel.setText("--");
		} else {
			textLabel.setText(value.toString());
		}
	}

	@Override
	public Widget widget() {
		return this;
	}

	@Override
	public Widget widget(Object value) {
		textBox.setValue(value.toString());
		return this;
	}

	@Override
	public Widget widget(Object value, Column column) {
		return widget(value);
	}

	public Integer getIntValue() {
		return Integer.parseInt(getValue().toString());
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
