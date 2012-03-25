package org.simplemodeling.SimpleModeler.entities.simplemodel

/**
 * @since   Jan. 30, 2009
 *          Feb. 20, 2009
 * @version Mar. 25, 2012
 * @author  ASAMI, Tomoharu
 */
class SMMAttribute(val name: String, val attributeType: SMMObjectType) {
  var multiplicity: GRMultiplicity = GROne
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
  var columnName: String = ""
  var sqlDatatype: Option[SMMObjectType] = None

  final def multiplicity_is(aMultiplicity: GRMultiplicity): SMMAttribute = {
    multiplicity = aMultiplicity
    this
  }
}

abstract class GRMultiplicity
class GROne extends GRMultiplicity
object GROne extends GROne
class GRZeroOne extends GRMultiplicity
object GRZeroOne extends GRZeroOne
class GROneMore extends GRMultiplicity
object GROneMore extends GROneMore
class GRZeroMore extends GRMultiplicity
object GRZeroMore extends GRZeroMore
class GRRange extends GRMultiplicity
object GRRange extends GRRange

object GRMultiplicity {
  def apply(string: String): GRMultiplicity = {
    val s = string.trim.toLowerCase
    s match {
      case "" => GROne
      case "1" => GROne
      case "one" => GROne
      case "require" => GROne
      case "required" => GROne
      case "必須" => GROne
      case "?" => GRZeroOne
      case "0,1" => GRZeroOne
      case "zeroone" => GRZeroOne
      case "option" => GRZeroOne
      case "optional" => GRZeroOne
      case "省略可" => GRZeroOne
      case "+" => GROneMore
      case "1..n" => GROneMore
      case "onemore" => GROneMore
      case "1以上" => GROneMore
      case "*" => GRZeroMore
      case "0..n" => GRZeroMore
      case "zeromore" => GRZeroMore
      case "collection" => GRZeroMore
      case "list" => GRZeroMore
      case "bag" => GRZeroMore
      case "0以上" => GRZeroMore
      case "複数" => GRZeroMore
      case "リスト" => GRZeroMore
      // case GRRange() => GRRange()
      case _ => GROne // TODO Warning
    }
  }
}
