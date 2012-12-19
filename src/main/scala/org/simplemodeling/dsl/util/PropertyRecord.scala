package org.simplemodeling.dsl.util

import org.apache.commons.lang3.StringUtils
import org.simplemodeling.SimpleModeler.builder.NaturalLabel

/*
 * @since   Nov. 26, 2012
 * @version Dec. 19, 2012
 * @author  ASAMI, Tomoharu
 */
case class PropertyRecord(
  key: String, value: Option[String],
  location: Option[DslLocation]
) {
  def isMatch(k: String) = {
    key.equalsIgnoreCase(k)
  }

  def isMatch(k: NaturalLabel) = {
    k.isMatch(key)
  }

  def toTuple: Option[(String, String)] = value.map((key, _))
}

object PropertyRecord {
  def create(kv: (String, String)): PropertyRecord = {
    val v = if (StringUtils.isBlank(kv._2)) None else Some(kv._2)
    new PropertyRecord(kv._1, v, None)
  }

  def create(kvs: Seq[(String, String)]): Seq[PropertyRecord] = {
    kvs.map(create)
  }
/*
  def create(kv: (String, Seq[String])): PropertyRecord = {
    val v = kv._2 match {
      case Nil => None
      case x :: _ => Some(x) // XXX
    }
    PropertyRecord(kv._1, v, None)
  }
*/
}
