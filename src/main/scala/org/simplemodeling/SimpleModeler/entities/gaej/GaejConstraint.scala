package org.simplemodeling.SimpleModeler.entities.gaej

import scala.collection.mutable.{Buffer, ArrayBuffer}
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Jun. 17, 2009
 * @version Dec. 18, 2010
 * @author  ASAMI, Tomoharu
 */
class GaejConstraint(val name: String, val value: Any) {
  def literal: String = {
    value match {
      case v: Byte => "\"" + value + "\""
      case v: Short => "\"" + value + "\""
      case v: Int => "\"" + value + "\""
      case v: Long => "\"" + value + "\""
      case v: Float => "\"" + value + "\""
      case v: Double => "\"" + value + "\""
      case o: AnyRef => "\"\\\"" + value + "\\\"\""
    }
  }

  def getLongValue(): Option[Long] = {
    value match {
      case v: Byte => Some(v)
      case v: Short => Some(v)
      case v: Int => Some(v)
      case v: Long => Some(v)
      case v: String => Some(java.lang.Long.parseLong(v))
      case None => None
    }
  }
}
