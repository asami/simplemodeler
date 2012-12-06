package org.simplemodeling.dsl.util

/*
 * @since   Nov. 26, 2012
 * @version Nov. 26, 2012
 * @author  ASAMI, Tomoharu
 */
case class PropertyRecord(
  key: String, value: Option[String],
  location: Option[DslLocation]
) {
  def isMatch(k: String) = {
    key.equalsIgnoreCase(k)
  }

  def toTuple = (key, value.get)
}

object PropertyRecord {
  def create(kv: (String, String)): PropertyRecord = {
    new PropertyRecord(kv._1, Some(kv._2), None)
  }

  def create(kvs: Seq[(String, String)]): Seq[PropertyRecord] = {
    kvs.map(create)
  }
}
