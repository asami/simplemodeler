package org.simplemodeling.dsl

import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._
import org.simplemodeling.dsl.datatype._
import org.simplemodeling.dsl.datatype.ext._

/*
 * @since   Sep. 10, 2008
 * @version Jul. 23, 2011
 * @author  ASAMI, Tomoharu
 */
class SDatatype extends SAttributeType {
  def apply[T](f: Function1[SDatatype, T]): T = {
    f(this)
  }
}

abstract class SDatatypeFunction[T] extends PartialFunction[SDatatype, T] {
  override def isDefinedAt(x: SDatatype) = true

  override def apply(datatype: SDatatype): T = {
    datatype match {
      case dt: XAnyURI => apply_AnyURI(dt)
      case dt: XBase64Binary => apply_Base64Binary(dt)
      case dt: XBoolean => apply_Boolean(dt)
      case dt: XByte => apply_Byte(dt)
      case dt: XDate => apply_Date(dt)
      case dt: XDateTime => apply_DateTime(dt)
      case dt: XDecimal => apply_Decimal(dt)
      case dt: XDouble => apply_Double(dt)
      case dt: XDuration => apply_Duration(dt)
      case dt: XFloat => apply_Float(dt)
      case dt: XGDay => apply_GDay(dt)
      case dt: XGMonth => apply_GMonth(dt)
      case dt: XGMonthDay => apply_GMonthDay(dt)
      case dt: XGYear => apply_GYear(dt)
      case dt: XGYearMonth => apply_GYearMonth(dt)
      case dt: XHexBinary => apply_HexBinary(dt)
      case dt: XInt => apply_Int(dt)
      case dt: XInteger => apply_Integer(dt)
      case dt: XLanguage => apply_Language(dt)
      case dt: XLong => apply_Long(dt)
      case dt: XNegativeInteger => apply_NegativeInteger(dt)
      case dt: XNonNegativeInteger => apply_NonNegativeInteger(dt)
      case dt: XPositiveInteger => apply_PositiveInteger(dt)
      case dt: XNonPositiveInteger => apply_NonPositiveInteger(dt)
      case dt: XShort => apply_Short(dt)
      case dt: XString => apply_String(dt)
      case dt: XTime => apply_Time(dt)
      case dt: XUnsignedByte => apply_UnsignedByte(dt)
      case dt: XUnsignedInt => apply_UnsignedInt(dt)
      case dt: XUnsignedLong => apply_UnsignedLong(dt)
      case dt: XUnsignedShort => apply_UnsignedShort(dt)
      case dt: XCategory => apply_Category(dt)
      case dt: XEmail => apply_Email(dt)
      case dt: XGeoPt => apply_GeoPt(dt)
      case dt: XIM => apply_IM(dt)
      case dt: XLink => apply_Link(dt)
      case dt: XPhoneNumber => apply_PhoneNumber(dt)
      case dt: XPostalAddress => apply_PostalAddress(dt)
      case dt: XRating => apply_Rating(dt)
      case dt: XText => apply_Text(dt)
      case dt: XUser => apply_User(dt)
    }
  }

  protected def apply_AnyURI(datatype: XAnyURI): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Base64Binary(datatype: XBase64Binary): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Boolean(datatype: XBoolean): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Byte(datatype: XByte): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Date(datatype: XDate): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_DateTime(datatype: XDateTime): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Decimal(datatype: XDecimal): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Double(datatype: XDouble): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Duration(datatype: XDuration): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Float(datatype: XFloat): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_GDay(datatype: XGDay): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_GMonth(datatype: XGMonth): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_GMonthDay(datatype: XGMonthDay): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_GYear(datatype: XGYear): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_GYearMonth(datatype: XGYearMonth): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_HexBinary(datatype: XHexBinary): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Int(datatype: XInt): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Integer(datatype: XInteger): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Language(datatype: XLanguage): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Long(datatype: XLong): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_NegativeInteger(datatype: XNegativeInteger): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_NonNegativeInteger(datatype: XNonNegativeInteger): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_PositiveInteger(datatype: XPositiveInteger): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_NonPositiveInteger(datatype: XNonPositiveInteger): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Short(datatype: XShort): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_String(datatype: XString): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Time(datatype: XTime): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_UnsignedByte(datatype: XUnsignedByte): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_UnsignedInt(datatype: XUnsignedInt): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_UnsignedLong(datatype: XUnsignedLong): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_UnsignedShort(datatype: XUnsignedShort): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Category(datatype: XCategory): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Email(datatype: XEmail): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_GeoPt(datatype: XGeoPt): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_IM(datatype: XIM): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Link(datatype: XLink): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_PhoneNumber(datatype: XPhoneNumber): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_PostalAddress(datatype: XPostalAddress): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Rating(datatype: XRating): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_Text(datatype: XText): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }

  protected def apply_User(datatype: XUser): T = {
    throw new UnsupportedOperationException("not implemented yet")
  }
}
