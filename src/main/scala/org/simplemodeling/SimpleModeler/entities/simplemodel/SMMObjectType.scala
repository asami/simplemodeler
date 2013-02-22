package org.simplemodeling.SimpleModeler.entities.simplemodel

import scalaz._, Scalaz._
import scala.collection.mutable.ArrayBuffer

/*
 * @since   Jan. 30, 2009
 *  version Jul. 12, 2009
 *  version Mar. 24, 2012
 *  version Oct. 30, 2012
 *  version Nov. 15, 2012
 *  version Dec. 26, 2012
 *  version Jan. 29, 2013
 * @version Feb. 21, 2013
 * @author  ASAMI, Tomoharu
 */
/**
 * SMMObjectType is converted to PObjectType by SimpleModel2JavaRealmTransformerBase or SimpleModel2ProgramRealmTransformerBase.
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

class SMMRuleType(aName: String, aPackageName: String) extends SMMEntityType(aName, aPackageName) {
}

class SMMServiceType(aName: String, aPackageName: String) extends SMMEntityType(aName, aPackageName) {
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

class SMMTokenType extends SMMDataType("XToken", "org.simplemodeling.dsl.datatype") {
  val candidates = List("token")
}
object SMMTokenType extends SMMTokenType

class SMMTextType extends SMMDataType("XText", "org.simplemodeling.dsl.datatype") {
  val candidates = List("text")
}
object SMMTextType extends SMMTextType

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

class SMMUriType extends SMMDataType("XAnyUri", "org.simplemodeling.dsl.datatype") {
  val candidates = List("uri")
}
object SMMUriType extends SMMUriType

// Extension (AppEngine)
class SMMLinkType extends SMMDataType("XLink", "org.simplemodeling.dsl.datatype.ext") {
  val candidates = List("link", "url")
}
object SMMLinkType extends SMMLinkType

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

// Platform datatype
class SMMUuidType extends SMMDataType("XUuid", "org.simplemodeling.dsl.datatype.platform") {
  val candidates = List("uuid")
}
object SMMUuidType extends SMMUuidType

class SMMXmlType extends SMMDataType("XXml", "org.simplemodeling.dsl.datatype.platform") {
  val candidates = List("xml")
}
object SMMXmlType extends SMMXmlType

class SMMHtmlType extends SMMDataType("XHtml", "org.simplemodeling.dsl.datatype.platform") {
  val candidates = List("html")
}
object SMMHtmlType extends SMMHtmlType

class SMMEverforthidType extends SMMDataType("XEverforthid", "org.simplemodeling.dsl.datatype.platform") {
  val candidates = List("everforthid")
}
object SMMEverforthidType extends SMMEverforthidType

//
class SMMDocumentType(name: String, pkg: String) extends SMMDataType(name, pkg) {
  val candidates = Nil
}

// Special datatype
class SMMUnknownDataType(val unkonwn: String) extends SMMDataType("XString", "org.simplemodeling.dsl.datatype") {
  val candidates = Nil
}

/*
 * SQL DataType
 */
abstract class SMMSqlDataType(name: String, pkg: String) extends SMMObjectType(name, pkg) with SMMValueDataType {
  def dataType: SMMDataType
  def text: String
}

trait SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlDataType]
}

class SMMSqlCharType(val length: Int) extends SMMSqlDataType("SChar", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMStringType
  val text = "CHAR(" + length + ")"
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

class SMMSqlVarCharType(val length: Int) extends SMMSqlDataType("SVarchar", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMStringType
  val text = "VARCHAR(" + length+ ")"
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

class SMMSqlDateType extends SMMSqlDataType("SDate", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMDateType
  val text = "DATE"
}

object SMMSqlDateType extends SMMSqlDateType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlDateType] = {
    if (string.toUpperCase == "DATE") SMMSqlDateType.some
    else none
  }
}

class SMMSqlTimeType extends SMMSqlDataType("STime", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMTimeType
  val text = "TIME"
}

object SMMSqlTimeType extends SMMSqlTimeType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlTimeType] = {
    if (string.toUpperCase == "TIME") SMMSqlTimeType.some
    else none
  }
}
class SMMSqlDateTimeType extends SMMSqlDataType("SDateTime", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMDateTimeType
  val text = "DATETIME"
}

object SMMSqlDateTimeType extends SMMSqlDateTimeType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlDateTimeType] = {
    if (string.toUpperCase == "DATETIME") SMMSqlDateTimeType.some
    else none
  }
}

class SMMSqlIntType extends SMMSqlDataType("SInt", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMIntType
  val text = "Int"
}

object SMMSqlIntType extends SMMSqlIntType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlIntType] = {
    if (string.toUpperCase == "INT") SMMSqlIntType.some
    else none
  }
}

class SMMSqlBigIntType extends SMMSqlDataType("SBigInt", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMLongType
  val text = "BIGINT"
}

object SMMSqlBigIntType extends SMMSqlBigIntType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlBigIntType] = {
    if (string.toUpperCase == "BIGINT") SMMSqlBigIntType.some
    else none
  }
}

class SMMSqlFloatType extends SMMSqlDataType("SFloat", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMFloatType
  val text = "Float"
}

object SMMSqlFloatType extends SMMSqlFloatType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlFloatType] = {
    if (string.toUpperCase == "FLOAT") SMMSqlFloatType.some
    else none
  }
}

class SMMSqlDoubleType extends SMMSqlDataType("SDouble", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMDoubleType
  val text = "Double"
}

object SMMSqlDoubleType extends SMMSqlDoubleType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlDoubleType] = {
    if (string.toUpperCase == "DOUBLE") SMMSqlDoubleType.some
    else none
  }
}

class SMMSqlDecimalType extends SMMSqlDataType("SDecimal", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMDecimalType
  val text = "Decimal"
}

object SMMSqlDecimalType extends SMMSqlDecimalType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlDecimalType] = {
    if (string.toUpperCase == "DECIMAL") SMMSqlDecimalType.some
    else none
  }
}

class SMMSqlRealType extends SMMSqlDataType("SReal", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMDoubleType
  val text = "Real"
}

object SMMSqlRealType extends SMMSqlRealType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlRealType] = {
    if (string.toUpperCase == "REAL") SMMSqlRealType.some
    else none
  }
}

class SMMSqlTextType extends SMMSqlDataType("SText", "org.simplemodeling.dsl.datatype.sql") {
  val dataType = SMMDoubleType
  val text = "Text"
}

object SMMSqlTextType extends SMMSqlTextType with SMMSqlDataTypeFactory {
  def unapply(string: String): Option[SMMSqlTextType] = {
    if (string.toUpperCase == "TEXT") SMMSqlTextType.some
    else none
  }
}

// Special datatype
class SMMSqlUnknownDataType(val unkonwn: String) extends SMMSqlDataType("TEXT", "org.simplemodeling.dsl.datatype.sql") {
  val candidates = Nil
  val dataType = SMMStringType
  val text = "Unkonwn:" + unkonwn
}

/*
 * Factory
 */
object SMMObjectType {
  val datatypes = List(
    SMMStringType,
    SMMTokenType,
    SMMTextType,
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
    SMMDateType,
    SMMDateTimeType,
    SMMTimeType,
    SMMUriType,
    SMMLinkType,
    SMMMoneyType,
    SMMPercentType,
    SMMUnitType,
    SMMUuidType,
    SMMXmlType,
    SMMHtmlType,
    SMMEverforthidType)

  def getDataType(string: String): Option[SMMValueDataType] = { // SMMDataType
    val r = datatypes.find(_.isMatch(string)) orElse getSqlDataType(string)
    println("SMMObjectType#getDataType(%s) = %s".format(string, r))
    r
  }

  def getValueDataType(name: String, pkgname: String): SMMValueDataType = { // SMMDataType
    getDataType(name) | new SMMDocumentType(name, pkgname)
  }

  def getDataTypeOrUnkonwn(string: String): SMMValueDataType = { // SMMDataType
    getDataType(string) | new SMMUnknownDataType(string)
  }

  // SQL
  val sqlDatatypes = Stream(
    SMMSqlCharType,
    SMMSqlVarCharType,
    SMMSqlDateType,
    SMMSqlTimeType,
    SMMSqlDateTimeType,
    SMMSqlIntType,
    SMMSqlBigIntType,
    SMMSqlFloatType,
    SMMSqlDoubleType,
    SMMSqlDecimalType,
    SMMSqlRealType,
    SMMSqlTextType
  )

  def getSqlDataType(string: String): Option[SMMSqlDataType] = {
//    println("SMMObjectType#getSqlDataType(%s) = %s".format(string, sqlDatatypes.flatMap(_.unapply(string))))
    sqlDatatypes.flatMap(_.unapply(string)).headOption
  }

  def sqlDataType(string: String): SMMSqlDataType = {
    getSqlDataType(string) | new SMMSqlUnknownDataType(string)
  }
}
