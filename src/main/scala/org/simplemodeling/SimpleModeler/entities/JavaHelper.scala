package org.simplemodeling.SimpleModeler.entities

import scalaz._
import Scalaz._
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Oct. 29, 2012
 * @version Oct. 29, 2012
 * @author  ASAMI, Tomoharu
 */
trait JavaHelper {
  protected final def is_primitive(attr: PAttribute) = {
    attr.typeName match {
      case "boolean" => true
      case "byte" => true
      case "short" => true
      case "int" => true
      case "long" => true
      case "float" => true
      case "double" => true
      case "Boolean" => true
      case "Byte" => true
      case "Short" => true
      case "Integer" => true
      case "Long" => true
      case "Float" => true
      case "Double" => true
      case "String" => true
      case _ => false
    }
  }

  protected final def java_typename(attr: PAttribute): String = {
    attr.typeName
  }

  protected final def xtypename_to_java_typename(xtype: String): String = {
    xtype match {
      case "XBoolean" => "Boolean"
      case "XByte" => "Byte"
      case "XShort" => "Short"
      case "XInt" => "Integer"
      case "XLong" => "Long"
      case "XFloat" => "Float"
      case "XDouble" => "Double"
      case "XString" => "String"
      case _ => sys.error("not implemented yet:" + xtype)
    }
  }
}

object JavaHelper extends JavaHelper {
  def isPrimitive(attr: PAttribute) = is_primitive(attr)
}
