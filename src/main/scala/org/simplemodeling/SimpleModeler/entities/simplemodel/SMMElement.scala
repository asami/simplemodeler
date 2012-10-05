package org.simplemodeling.SimpleModeler.entities.simplemodel

/*
 * @version Oct.  2, 2012
 * @version Oct.  2, 2012
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
}
