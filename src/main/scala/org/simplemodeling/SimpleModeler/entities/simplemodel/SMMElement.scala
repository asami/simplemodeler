package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.simplemodeling.dsl.util.PropertyRecord
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Oct.  5, 2012
 *  version Nov. 13, 2012
 *  version Nov. 26, 2012
 * @version Dec.  9, 2012
 * @author  ASAMI, Tomoharu
 */
trait SMMElement {
  var name_ja: String = ""
  var name_en: String = ""
  var term: String = ""
  var term_ja: String = ""
  var term_en: String = ""
  var xmlName: String = ""
  var label: String = ""
  var title: String = ""
  var subtitle: String = ""
  var caption: String = ""
  var brief: String = ""
  var summary: String = ""
  var description: String = ""
  /**
   * The properties allows same key entries.
   * First entry is available.
   */
  var properties: Seq[PropertyRecord] = Nil

  /*
   * SQL
   */
  var sqlTableName: String = ""
  var sqlColumnName: String = ""
  var sqlAutoId: Boolean = false
  var sqlReadOnly: Boolean = false
  var sqlCreate: Boolean = false
  var sqlUpdate: Boolean = false

  /**
   * Used by SimpleModelMakerEntity#build#resolve_annotations
   */
  final def annotation(aKey: String, aValue: String) {
    sys.error("already not supported.")
  }

/*
  final def annotation(aKey: String, aValue: String) {
    if (true) {
      
    }
    if (!set_Annotation_Pf(aKey, aValue)) aKey match {
      case "name_en"     => name_en = aValue
      case "name_ja"     => name_ja = aValue
      case "term"        => term = aValue
      case "label"       => label = aValue
      case "title"       => title = aValue
      case "caption"     => caption = aValue
      case "caption"     => caption = aValue
      case "brief"       => brief = aValue
      case "summary"     => summary = aValue
      case "description" => description = aValue
      case _             => description = "bad key [" + aKey + "] = " + aValue
    }
  }

  protected def set_Annotation_Pf(key: String, value: String): Boolean = false
*/

  def update(entry: Seq[PropertyRecord]) {
    properties = entry ++ properties
    entry.foreach(updateField)
  }

  def updateField(field: PropertyRecord) {
    val key = field.key
    val value = field.value.get // XXX
    NaturalLabel(key) match {
      case NameLabel => {}
      case TypeLabel => {}
      case DatatypeLabel => {}
      case ObjecttypeLabel => {}
      case MultiplicityLabel => {}
      case NameJaLabel => name_ja = value
      case NameEnLabel => name_en = value
      case TermLabel => term = value
      case TermJaLabel => term_ja = value
      case TermEnLabel => term_en = value
      case TitleLabel => title = value
      case SubtitleLabel => subtitle = value
      case LabelLabel => label = value
      case CaptionLabel => caption = value
      case BriefLabel => brief = value
      case SummaryLabel => brief = value
      case DescriptionLabel => description = value
      case ColumnNameLabel => {}
      case SqlDatatypeLabel => {}
      case l: NaturalLabel => update_Field(l, value)
    }
    update_Field(key, value)
  }

  protected def update_Field(label: NaturalLabel, value: String) {
  }

  protected def update_Field(key: String, value: String) {
  }
}
