package org.simplemodeling.SimpleModeler.entities.simplemodel

import scala.collection.mutable.ArrayBuffer
import java.io._
import org.goldenport.entity._
import org.goldenport.entity.datasource.GDataSource
import org.goldenport.entity.datasource.GContentDataSource

/*
 * @since   Jan. 30, 2009
 * @version Jul. 12, 2009
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

class SMMEntityType(aName: String, aPackageName: String) extends SMMObjectType(aName, aPackageName) {
  def this(anEntity: SMMEntityEntity) = this(anEntity.name, anEntity.packageName)
  override def isEntity = true
}

class SMMValueType(aName: String, aPackageName: String) extends SMMObjectType(aName, aPackageName) {
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

class SMMStringType extends SMMObjectType("XString", "org.simplemodeling.dsl.datatype")
object SMMStringType extends SMMStringType

class SMMBooleanType extends SMMObjectType("XBoolean", "org.simplemodeling.dsl.datatype")
object SMMBooleanType extends SMMBooleanType

class SMMByteType extends SMMObjectType("XByte", "org.simplemodeling.dsl.datatype")
object SMMByteType extends SMMByteType

class SMMShortType extends SMMObjectType("XShort", "org.simplemodeling.dsl.datatype")
object SMMShortType extends SMMShortType

class SMMIntType extends SMMObjectType("XInt", "org.simplemodeling.dsl.datatype")
object SMMIntType extends SMMIntType

class SMMLongType extends SMMObjectType("XLong", "org.simplemodeling.dsl.datatype")
object SMMLongType extends SMMLongType

class SMMFloatType extends SMMObjectType("XFloat", "org.simplemodeling.dsl.datatype")
object SMMFloatType extends SMMFloatType

class SMMDoubleType extends SMMObjectType("XDouble", "org.simplemodeling.dsl.datatype")
object SMMDoubleType extends SMMDoubleType

class SMMUnsignedByteType extends SMMObjectType("XUnsignedByte", "org.simplemodeling.dsl.datatype")
object SMMUnsignedByteType extends SMMUnsignedByteType

class SMMUnsignedShortType extends SMMObjectType("XUnsignedShort", "org.simplemodeling.dsl.datatype")
object SMMUnsignedShortType extends SMMUnsignedShortType

class SMMUnsignedIntType extends SMMObjectType("XUnsignedInt", "org.simplemodeling.dsl.datatype")
object SMMUnsignedIntType extends SMMUnsignedIntType

class SMMUnsignedLongType extends SMMObjectType("XUnsignedLong", "org.simplemodeling.dsl.datatype")
object SMMUnsignedLongType extends SMMUnsignedLongType

class SMMIntegerType extends SMMObjectType("XInteger", "org.simplemodeling.dsl.datatype")
object SMMIntegerType extends SMMIntegerType

class SMMDecimalType extends SMMObjectType("XDecimal", "org.simplemodeling.dsl.datatype")
object SMMDecimalType extends SMMDecimalType
