package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.simplemodeling.SimpleModeler.builder._

/*
 * @version Nov. 13, 2012
 * @version Nov. 13, 2012
 * @author  ASAMI, Tomoharu
 */
trait SMMElement {
  var name_ja: String = ""
  var name_en: String = ""
  var term: String = ""
  var term_ja: String = ""
  var term_en: String = ""
  var title: String = ""
  var subtitle: String = ""
  var caption: String = ""
  var brief: String = ""
  var summary: String = ""
  var description: String = ""

  final def annotation(aKey: String, aValue: String) {
    if (true) {
      
    }
    if (!set_Annotation_Pf(aKey, aValue)) aKey match {
      case "name_en"     => name_en = aValue
      case "name_ja"     => name_ja = aValue
      case "term"        => term = aValue
      case "caption"     => caption = aValue
      case "brief"       => brief = aValue
      case "summary"     => summary = aValue
      case "description" => description = aValue
      case _             => description = "bad key [" + aKey + "] = " + aValue
    }
  }

  protected def set_Annotation_Pf(key: String, value: String): Boolean = false

  def update(entry: Seq[(String, String)]) {
    entry.foreach(updateField)
  }

  def updateField(field: (String, String)) {
    val (key, value) = field
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
      case CaptionLabel => caption = value
      case BriefLabel => brief = value
      case SummaryLabel => brief = value
      case DescriptionLabel => description = value
      case ColumnNameLabel => {}
      case SqlDatatypeLabel => {}
      case _ => {}
    }
  }
}
