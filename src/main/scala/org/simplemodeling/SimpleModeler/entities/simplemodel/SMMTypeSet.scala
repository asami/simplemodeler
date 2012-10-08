package org.simplemodeling.SimpleModeler.entities.simplemodel

import scalaz._, Scalaz._
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Oct.  8, 2012
 * @version Oct.  8, 2012
 * @author  ASAMI, Tomoharu
 */
trait SMMTypeSet {
  def name: String
  def sqlDataType: Option[SMMSqlDataType]
}

object SMMTypeSet {
  def sqlType(entry: Seq[(String, String)]): Option[SMMSqlDataType] = {
    SqlDatatypeLabel.find(entry).flatMap(SMMObjectType.getSqlDataType)
  }
}

class SMMAttributeTypeSet(
  val attributeType: Option[SMMValueDataType],
  val dataType: Option[SMMDataType] = None,
  val sqlDataType: Option[SMMSqlDataType] = None
) extends SMMTypeSet {
  def name = {
    attributeType.map(_.name) orElse
    dataType.map(_.name) orElse
    sqlDataType.map(_.name) getOrElse "Unknown"
  }

  def packageName = {
    attributeType.map(_.packageName) orElse
    dataType.map(_.name) orElse
    sqlDataType.map(_.name) getOrElse "Unknown"
  }

  def effectiveAttributeType: SMMValueDataType = {
    attributeType orElse
    dataType orElse
    sqlDataType.map(_.dataType) getOrElse SMMStringType
  }
}

object SMMAttributeTypeSet {
  def apply(entry: Seq[(String, String)]): SMMAttributeTypeSet = {
    new SMMAttributeTypeSet(
      _value_data_type(entry),
      _data_type(entry),
      SMMTypeSet.sqlType(entry))
  }

  // TODO handle Value object
  private def _value_data_type(entry: Seq[(String, String)]): Option[SMMValueDataType] = {
    TypeLabel.find(entry).flatMap(SMMObjectType.getDataType)
  }

  private def _data_type(entry: Seq[(String, String)]): Option[SMMDataType] = {
    DatatypeLabel.find(entry).flatMap(SMMObjectType.getDataType)
  }
}

class SMMEntityTypeSet(
  val entityType: Option[SMMEntityType],
  val sqlDataType: Option[SMMSqlDataType] = None
) extends SMMTypeSet {
  def name = {
    entityType.map(_.name) orElse
    sqlDataType.map(_.name) getOrElse "Unknown"
  }
}

object SMMEntityTypeSet {
  def apply(packageName: String, entry: Seq[(String, String)]): SMMEntityTypeSet = {
    new SMMEntityTypeSet(_entity_type(packageName, entry), SMMTypeSet.sqlType(entry))
  }

  private def _entity_type(packageName: String, entry: Seq[(String, String)]): Option[SMMEntityType] = {
    val name = NameLabel.find(entry)
    val term = TermLabel.find(entry)
    name.flatMap(n => {
      val etype = new SMMEntityType(n, packageName)
      term.foreach(x => etype.term = x)
      etype.some
    })
  }
}
