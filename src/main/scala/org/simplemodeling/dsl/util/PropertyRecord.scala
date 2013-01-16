package org.simplemodeling.dsl.util

import org.apache.commons.lang3.StringUtils
import org.simplemodeling.SimpleModeler.builder.NaturalLabel

/*
 * @since   Nov. 26, 2012
 *  version Dec. 26, 2012
 * @version Jan. 17, 2013
 * @author  ASAMI, Tomoharu
 */
case class PropertyRecord(
  key: String, value: Option[String],
  location: Option[DslLocation]
) {
  def isMatch(k: String) = {
    val a = k.replace(" ", "")
    key.equalsIgnoreCase(a)
  }

  def isMatch(k: NaturalLabel) = {
    k.isMatch(key)
  }

  def toTuple: Option[(String, String)] = value.map((key, _))
}

object PropertyRecord {
  def create(kv: (String, String)): PropertyRecord = {
//    println("PropertyRecord#create = " + kv)
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
