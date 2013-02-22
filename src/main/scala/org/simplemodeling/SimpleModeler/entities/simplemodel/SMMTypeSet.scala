package org.simplemodeling.SimpleModeler.entities.simplemodel

import scalaz._, Scalaz._
import org.simplemodeling.dsl.util.PropertyRecord
import org.simplemodeling.SimpleModeler.builder._

/*
 * @since   Oct.  8, 2012
 *  version Oct. 26, 2012
 *  version Nov. 26, 2012
 *  version Dec. 26, 2012
 * @version Feb. 21, 2013
 * @author  ASAMI, Tomoharu
 */
trait SMMTypeSet {
  def getName(): Option[String]
  def name: String = getName | "UnknownType"
  def sqlDataType: Option[SMMSqlDataType]
}

object SMMTypeSet {
  def sqlType(entry: Seq[PropertyRecord]): Option[SMMSqlDataType] = {
//    println("SMMTypeSet#sqlType(%s) = %s".format(SqlDatatypeLabel.findData(entry), entry))
    SqlDatatypeLabel.findData(entry).map(SMMObjectType.sqlDataType)
  }
}

class SMMAttributeTypeSet(
  val attributeType: Option[SMMValueDataType],
  val dataType: Option[SMMValueDataType] = None, // SMMDataType
  val sqlDataType: Option[SMMSqlDataType] = None
) extends SMMTypeSet {
  def getName = {
    attributeType.map(_.name) orElse
    dataType.map(_.name) orElse
    sqlDataType.map(_.name)
  }

  def packageName = {
    attributeType.map(_.packageName) orElse
    dataType.map(_.name) orElse
    sqlDataType.map(_.name) getOrElse "Unknown"
  }

  def getAttributeType: Option[SMMValueDataType] = {
    attributeType orElse
    dataType orElse
    sqlDataType.map(_.dataType)
  }

/*
  def effectiveAttributeType: SMMValueDataType = {
    getAttributeType getOrElse SMMStringType
  }
*/

  def idType: Option[SMMValueIdType] = {
    attributeType collect {
      case x: SMMValueIdType => x
    }
  }

  def getSqlDataType: Option[SMMSqlDataType] = {
    sqlDataType orElse
    dataType.collect { case x: SMMSqlDataType => x } orElse
    attributeType.collect { case x: SMMSqlDataType => x }
  }

  def getSqlDataTypeName: Option[String] = {
    getSqlDataType.map(_.text)
  }

  override def toString() = {
    "SMMAttributeTypeSet(%s, %s, %s)".format(attributeType, dataType, sqlDataType)
  }
}

/**
 * Currently non pre defined datatype is ignored.
 */
object SMMAttributeTypeSet {
  def apply(entry: Seq[PropertyRecord], pkg: String): SMMAttributeTypeSet = {
    new SMMAttributeTypeSet(
      _value_data_type(entry, pkg),
      _data_type(entry),
      SMMTypeSet.sqlType(entry))
  }

  private def _value_data_type(entry: Seq[PropertyRecord], pkg: String): Option[SMMValueDataType] = {
    TypeLabel.findData(entry).map(SMMObjectType.getValueDataType(_, pkg))
  }

  private def _data_type(entry: Seq[PropertyRecord]): Option[SMMValueDataType] = { // SMMDataType
    DatatypeLabel.findData(entry).flatMap(SMMObjectType.getDataType)
  }

  def in(entry: Seq[PropertyRecord], pkg: String): SMMAttributeTypeSet = {
    new SMMAttributeTypeSet(_in_value_data_type(entry, pkg))
  }

  private def _in_value_data_type(entry: Seq[PropertyRecord], pkg: String): Option[SMMValueDataType] = {
    InLabel.findData(entry).map(SMMObjectType.getValueDataType(_, pkg))
  }


  def out(entry: Seq[PropertyRecord], pkg: String): SMMAttributeTypeSet = {
    new SMMAttributeTypeSet(_out_value_data_type(entry, pkg))
  }

  private def _out_value_data_type(entry: Seq[PropertyRecord], pkg: String): Option[SMMValueDataType] = {
    OutLabel.findData(entry).map(SMMObjectType.getValueDataType(_, pkg))
  }
}

class SMMEntityTypeSet(
  val entityType: Option[SMMEntityType],
  val sqlDataType: Option[SMMSqlDataType] = None
) extends SMMTypeSet {
  def getName = {
    entityType.map(_.name) orElse
    sqlDataType.map(_.name)
  }
}

object SMMEntityTypeSet {
  def apply(packageName: String, entry: Seq[PropertyRecord]): SMMEntityTypeSet = {
    new SMMEntityTypeSet(_entity_type(packageName, entry), SMMTypeSet.sqlType(entry))
  }

  private def _entity_type(packageName: String, entry: Seq[PropertyRecord]): Option[SMMEntityType] = {
    NaturalLabel.getEntityTypeName(entry).map(x => {
      new SMMEntityType(x, packageName)
    })
/*
    val name = NameLabel.find(entry)
    val term = TermLabel.find(entry)
    name.flatMap(n => {
      val etype = new SMMEntityType(n, packageName)
      term.foreach(x => etype.term = x)
      etype.some
    })
*/
  }
}
