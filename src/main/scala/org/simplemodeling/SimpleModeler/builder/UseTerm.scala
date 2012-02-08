package org.simplemodeling.SimpleModeler.builder

import com.asamioffice.goldenport.text.UString
import org.simplemodeling.SimpleModeler.entities.simplemodel._

/**
 * @since   Feb.  7, 2012
 * @version Feb.  9, 2012
 * @author  ASAMI, Tomoharu
 */
trait UseTerm {
  def get_name_by_term(aTerm: String): String = {
    val index = aTerm.indexOf(':')
    if (index != -1) return _normalize_plain_name(aTerm.substring(0, index))
    val index2 = aTerm.indexOf('(')
    if (index2 != -1) return _normalize_plain_name(aTerm.substring(0, index2))
    _normalize_plain_name(get_name_without_multiplicity(aTerm))
  }

  private def _normalize_plain_name(term: String): String = {
    UString.uncapitalize(term).trim
  }

  def get_name_without_multiplicity(aTerm: String): String = {
    val name = aTerm(aTerm.length - 1) match {
      case '?' => aTerm.substring(0, aTerm.length - 1)
      case '+' => aTerm.substring(0, aTerm.length - 1)
      case '*' => aTerm.substring(0, aTerm.length - 1)
      case _   => aTerm
    }
    name.trim
  }

/*
  def get_type_name_by_term(aTerm:String): String = {
    val index = aTerm.indexOf(':')
    if (index == -1) {
      _normalize_type_name(get_name_by_term(aTerm))
    } else {
      val index2 = aTerm.indexOf('(')
      if (index2 != -1) {
        _normalize_type_name(aTerm.substring(index, index2))
      } else {
        _normalize_type_name(get_name_without_multiplicity(aTerm.substring(index + 1)))
      }
    }
  }
*/

  private def _normalize_type_name(term: String): String = {
    UString.capitalize(term).trim
  }

  def get_entity_by_term_in_entities(entities: Traversable[SMMEntityEntity], term: String): Option[SMMEntityEntity] = {
    entities.find(_.term == term)
  } 

  def get_attribute_type_by_term(aTerm: String): SMMObjectType = {
    def get_type(aTypeName: String) = {
      aTypeName match {
        case "boolean"       => SMMBooleanType
        case "byte"          => SMMByteType
        case "short"         => SMMShortType
        case "int"           => SMMIntType
        case "long"          => SMMLongType
        case "float"         => SMMFloatType
        case "double"        => SMMDoubleType
        case "integer"       => SMMIntegerType
        case "decimal"       => SMMDecimalType
        case "unsignedByte"  => SMMUnsignedByteType
        case "unsignedShort" => SMMUnsignedShortType
        case "unsignedInt"   => SMMUnsignedIntType
        case "unsignedLong"  => SMMUnsignedLongType
        case _               => SMMStringType
      }
    }

    val index = aTerm.indexOf(':')
    if (index == -1) return SMMStringType
    val index2 = aTerm.indexOf('(')
    if (index2 != -1) return get_type(aTerm.substring(index + 1, index2))
    aTerm(aTerm.length - 1) match {
      case '?' => get_type(aTerm.substring(index + 1, aTerm.length - 1))
      case '+' => get_type(aTerm.substring(index + 1, aTerm.length - 1))
      case '*' => get_type(aTerm.substring(index + 1, aTerm.length - 1))
      case _   => get_type(aTerm.substring(index + 1))
    }
  }

  def get_labels_by_term(aTerm: String): Seq[String] = {
    val start = aTerm.indexOf('(')
    if (start == -1) return Nil
    val end = aTerm.indexOf(')')
    if (end == -1) error("syntax error: = " + aTerm) // XXX
    aTerm.substring(start + 1, end).split("[;. ]+")
  }

  def get_multiplicity_by_term(aTerm: String): GRMultiplicity = {
    aTerm(aTerm.length - 1) match {
      case '?' => GRZeroOne
      case '+' => GROneMore
      case '*' => GRZeroMore
      case _   => GROne
    }
  }
}
/*
  // common with SimpleModelMakerEntity : XXX unify
  def get_name_by_term(aTerm: String): String = {
    val index = aTerm.indexOf(':')
    if (index != -1) return aTerm.substring(0, index).trim
    val index2 = aTerm.indexOf('(')
    if (index2 != -1) return aTerm.substring(0, index2).trim
    get_name_without_multiplicity(aTerm)
  }

  def get_name_without_multiplicity(aTerm: String): String = {
    (aTerm(aTerm.length - 1) match {
      case '?' => aTerm.substring(0, aTerm.length - 1)
      case '+' => aTerm.substring(0, aTerm.length - 1)
      case '*' => aTerm.substring(0, aTerm.length - 1)
      case _   => aTerm
    }).trim
  }

  def get_type_name_by_term(aTerm: String): String = {
    val index = aTerm.indexOf(':')
    if (index == -1) {
      get_name_by_term(aTerm)
    } else {
      val index2 = aTerm.indexOf('(')
      if (index2 != -1) {
        aTerm.substring(index, index2).trim
      } else {
        get_name_without_multiplicity(aTerm.substring(index + 1))
      }
    }
  }

  def get_attribute_type_by_term(aTerm: String): SMMObjectType = {
    def get_type(aTypeName: String) = {
      aTypeName match {
        case "boolean"       => SMMBooleanType
        case "byte"          => SMMByteType
        case "short"         => SMMShortType
        case "int"           => SMMIntType
        case "long"          => SMMLongType
        case "float"         => SMMFloatType
        case "double"        => SMMDoubleType
        case "integer"       => SMMIntegerType
        case "decimal"       => SMMDecimalType
        case "unsignedByte"  => SMMUnsignedByteType
        case "unsignedShort" => SMMUnsignedShortType
        case "unsignedInt"   => SMMUnsignedIntType
        case "unsignedLong"  => SMMUnsignedLongType
        case _               => SMMStringType
      }
    }

    val index = aTerm.indexOf(':')
    if (index == -1) return SMMStringType
    val index2 = aTerm.indexOf('(')
    if (index2 != -1) return get_type(aTerm.substring(index + 1, index2))
    aTerm(aTerm.length - 1) match {
      case '?' => get_type(aTerm.substring(index + 1, aTerm.length - 1))
      case '+' => get_type(aTerm.substring(index + 1, aTerm.length - 1))
      case '*' => get_type(aTerm.substring(index + 1, aTerm.length - 1))
      case _   => get_type(aTerm.substring(index + 1))
    }
  }

  def get_labels_by_term(aTerm: String): Seq[String] = {
    val start = aTerm.indexOf('(')
    if (start == -1) return Nil
    val end = aTerm.indexOf(')')
    if (end == -1) error("syntax error: = " + aTerm) // XXX
    aTerm.substring(start + 1, end).split("[;. ]+")
  }

  def get_multiplicity_by_term(aTerm: String): GRMultiplicity = {
    aTerm(aTerm.length - 1) match {
      case '?' => GRZeroOne
      case '+' => GROneMore
      case '*' => GRZeroMore
      case _   => GROne
    }
  }
*/
