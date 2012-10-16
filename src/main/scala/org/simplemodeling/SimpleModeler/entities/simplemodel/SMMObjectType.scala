package org.simplemodeling.SimpleModeler.entities.simplemodel

import scalaz._, Scalaz._
import scala.collection.mutable.ArrayBuffer

/*
 * @since   Jan. 30, 2009
 *  version Jul. 12, 2009
 *  version Mar. 24, 2012
 * @version Oct. 16, 2012
 * @author  ASAMI, Tomoharu
 */
abstract class SMMObjectType(val name: String, val packageName: String) {
  def qualifiedName = {
    require (name != null && packageName != null)
    if (packageName == "") name
    else packageName + "." + name
  }
  var term: String = ""
  def isEntity: Boolean = false
}

trait SMMValueDataType extends SMMObjectType

class SMMEntityType(aName: String, aPackageName: String) extends SMMObjectType(aName, aPackageName) {
  def this(anEntity: SMMEntityEntity) = this(anEntity.name, anEntity.packageName)
  override def isEntity = true
}

@deprecated
class SMMTraitType(aName: String, aPackageName: String) extends SMMObjectType(aName, aPackageName) {
}

class SMMValueType(aName: String, aPackageName: String) extends SMMObjectType(aName, aPackageName) with SMMValueDataType {
}

class SMMValueIdType(aName: String, aPackageName: String) extends SMMValueType(aName, aPackageName) {
}

class SMMPowertypeType(aName: String, aPackageName: String) extends SMMObjectType(aName, aPackageName) {
  val instances = new ArrayBuffer[String]
}

class SMMRoleType(aName: String, aPackageName: String) extends SMMEntityType(aName, aPackageName) {
}

class SMMStateMachineType(aName: String, aPackageName: String) extends SMMEntityType(aName, aPackageName) {
  val states = new ArrayBuffer[(String, String)]
}

/*
 * DataType
 */
abstract class SMMDataType(name: String, pkg: String) extends SMMObjectType(name, pkg) with SMMValueDataType {
  def candidates: Seq[String]
  lazy val allCandidates: Seq[String] = {
    def augumentsSpace(s: String): Seq[String] = {
      if (s.contains(" ")) {
        List(s, s.replace(" ", ""), s.replace(" ", "-"))
      } else Seq(s)
    }

    def augumentsPrefix(s: String): Seq[String] = {
      Seq(s, "x" + s)
    }

    candidates >>= augumentsPrefix >>= augumentsSpace
  }

  def isMatch(name: String) = {
    val n = name.trim.toLowerCase
    allCandidates.contains(n)
  }
}

class SMMStringType extends SMMDataType("XString", "org.simplemodeling.dsl.datatype") {
  val candidates = List("string")
}
object SMMStringType extends SMMStringType

class SMMBooleanType extends SMMDataType("XBoolean", "org.simplemodeling.dsl.datatype") {
  val candidates = List("boolean")
}
object SMMBooleanType extends SMMBooleanType

class SMMByteType extends SMMDataType("XByte", "org.simplemodeling.dsl.datatype") {
  val candidates = List("byte")
}
object SMMByteType extends SMMByteType

class SMMShortType extends SMMDataType("XShort", "org.simplemodeling.dsl.datatype") {
    val candidates = List("short")
}
object SMMShortType extends SMMShortType

class SMMIntType extends SMMDataType("XInt", "org.simplemodeling.dsl.datatype") {
    val candidates = List("int")
}
object SMMIntType extends SMMIntType

class SMMLongType extends SMMDataType("XLong", "org.simplemodeling.dsl.datatype") {
    val candidates = List("long")
}
object SMMLongType extends SMMLongType

class SMMFloatType extends SMMDataType("XFloat", "org.simplemodeling.dsl.datatype") {
    val candidates = List("float")
}
object SMMFloatType extends SMMFloatType

class SMMDoubleType extends SMMDataType("XDouble", "org.simplemodeling.dsl.datatype") {
    val candidates = List("double")
}
object SMMDoubleType extends SMMDoubleType

class SMMUnsignedByteType extends SMMDataType("XUnsignedByte", "org.simplemodeling.dsl.datatype") {
    val candidates = List("unsigned byte")
}
object SMMUnsignedByteType extends SMMUnsignedByteType

class SMMUnsignedShortType extends SMMDataType("XUnsignedShort", "org.simplemodeling.dsl.datatype") {
    val candidates = List("unsigned short")
}
object SMMUnsignedShortType extends SMMUnsignedShortType

class SMMUnsignedIntType extends SMMDataType("XUnsignedInt", "org.simplemodeling.dsl.datatype") {
    val candidates = List("unsigned int")
}
object SMMUnsignedIntType extends SMMUnsignedIntType

class SMMUnsignedLongType extends SMMDataType("XUnsignedLong", "org.simplemodeling.dsl.datatype") {
    val candidates = List("unsigned long")
}
object SMMUnsignedLongType extends SMMUnsignedLongType

class SMMIntegerType extends SMMDataType("XInteger", "org.simplemodeling.dsl.datatype") {
    val candidates = List("integer")
}
object SMMIntegerType extends SMMIntegerType

class SMMDecimalType extends SMMDataType("XDecimal", "org.simplemodeling.dsl.datatype") {
  val candidates = List("decimal")
}
object SMMDecimalType extends SMMDecimalType

class SMMDateType extends SMMDataType("XDate", "org.simplemodeling.dsl.datatype") {
  val candidates = List("date")
}
object SMMDateType extends SMMDateType

class SMMTimeType extends SMMDataType("XTime", "org.simplemodeling.dsl.datatype") {
  val candidates = List("time")
}
object SMMTimeType extends SMMTimeType

class SMMDateTimeType extends SMMDataType("XDateTime", "org.simplemodeling.dsl.datatype") {
  val candidates = List("datetime")
}
object SMMDateTimeType extends SMMDateTimeType

// Business datatype
class SMMMoneyType extends SMMDataType("XMoney", "org.simplemodeling.dsl.datatype.business") {
  val candidates = List("money")
}
object SMMMoneyType extends SMMMoneyType

class SMMPercentType extends SMMDataType("XPercent", "org.simplemodeling.dsl.datatype.business") {
  val candidates = List("percent")
}
object SMMPercentType extends SMMPercentType

class SMMUnitType extends SMMDataType("XUnit", "org.simplemodeling.dsl.datatype.business") {
  val candidates = List("unit")
}
object SMMUnitType extends SMMUnitType

// Special datatype
class SMMUnknownDataType(val unkonwn: String) extends SMMDataType("XString", "org.simplemodeling.dsl.datatype") {
  val candidates = Nil
}

/*
 * SQL DataType
 */
abstract class SMMSqlDataType(name: String, pkg: String) extends SMMObjectType(name, pkg) {
  def dataType: SMMDataType
}

trait SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlDataType]
}

class SMMSqlCharType(val length: Int) extends SMMSqlDataType("SChar", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMStringType
}

object SMMSqlCharType extends SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlCharType] = {
    val r = """CHAR[(]([0-9]+)[)]""".r
    string.toUpperCase match {
      case r(n) => new SMMSqlCharType(n.toInt).some
      case _ => none
    }
  }
}

class SMMSqlVarCharType(val length: Int) extends SMMSqlDataType("SChar", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMStringType
}

object SMMSqlVarCharType extends SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlVarCharType] = {
    val r = """VARCHAR[(]([0-9]+)[)]""".r
    string.toUpperCase match {
      case r(n) => new SMMSqlVarCharType(n.toInt).some
      case _ => none
    }
  }
}

class SMMSqlDateType extends SMMSqlDataType("SChar", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMDateType
}

object SMMSqlDateType extends SMMSqlDateType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlDateType] = {
    if (string.toUpperCase == "DATE") SMMSqlDateType.some
    else none
  }
}

class SMMSqlIntType extends SMMSqlDataType("SChar", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMIntType
}

object SMMSqlIntType extends SMMSqlIntType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlIntType] = {
    if (string.toUpperCase == "INT") SMMSqlIntType.some
    else none
  }
}

// Special datatype
class SMMSqlUnknownDataType(val unkonwn: String) extends SMMSqlDataType("TEXT", "org.simplemodeling.dsl.datatype.sql") {
  val candidates = Nil
  val dataType = SMMStringType
}

/*
 * Factory
 */
object SMMObjectType {
  val datatypes = List(
    SMMShortType,
    SMMBooleanType,
    SMMByteType,
    SMMShortType,
    SMMIntType,
    SMMLongType,
    SMMFloatType,
    SMMDoubleType,
    SMMUnsignedByteType,
    SMMUnsignedShortType,
    SMMUnsignedIntType,
    SMMUnsignedLongType,
    SMMIntegerType,
    SMMDecimalType,
    SMMMoneyType,
    SMMPercentType,
    SMMUnitType)

  def getDataType(string: String): Option[SMMDataType] = {
    datatypes.find(_.isMatch(string))
  }

  def getDataTypeOrUnkonwn(string: String): SMMDataType = {
    getDataType(string) | new SMMUnknownDataType(string)
  }

  // SQL
  val sqlDatatypes = Stream(
    SMMSqlCharType,
    SMMSqlVarCharType,
    SMMSqlDateType,
    SMMSqlIntType
  )

  def getSqlDataType(string: String): Option[SMMSqlDataType] = {
    sqlDatatypes.flatMap(_.unapply(string)).headOption
  }

  def getSqlDataTypeOrUnkonwn(string: String): SMMSqlDataType = {
    getSqlDataType(string) | new SMMSqlUnknownDataType(string)
  }
}
