package org.simplemodeling.SimpleModeler.entities.simplemodel

import org.simplemodeling.dsl.SConstants

/*
 * @since   Oct.  6, 2012
 *  version Nov. 25, 2012
 * @version Dec. 13, 2012
 * @author  ASAMI, Tomoharu
 */
trait SMMSlot extends SMMElement {
  val name: String
  var sqlColumnName: String = ""
  var sqlDatatype: Option[SMMObjectType] = None
  var multiplicity: GRMultiplicity = GROne
  var displaySequence: Int = SConstants.DEFAULT_DISPLAY_SEQUENCE
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
