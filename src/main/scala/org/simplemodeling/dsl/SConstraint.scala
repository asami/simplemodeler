package org.simplemodeling.dsl

import scalaz._
import Scalaz._
import scala.collection.mutable.ArrayBuffer
import org.goldenport.sdoc._

/**
 * @since   Jun. 18, 2009
 *  version Jun. 20, 2009
 * @version Apr. 11, 2012
 * @author  ASAMI, Tomoharu
 */
sealed trait SConstraint {
  def literal: String
}

// JSR 303
case class CAssertFalse() extends SConstraint {
  def literal = "@AssertFalse"
  override def toString() = literal
}
object CAssertFalse extends CAssertFalse()

case class CAssertTrue() extends SConstraint {
  def literal = "@AssertTrue"
}
object CAssertTrue extends CAssertTrue()

case class CDecimalMax(value: String) extends SConstraint {
  def literal = """@DecimalMax("%s")""".format(value)
}

case class CDecimalMin(value: String) extends SConstraint {
  def literal = """@DecimalMin("%s")""".format(value)
}

case class CDigits(integer: Int = -1, fraction: Int = -1) extends SConstraint {
  def literal = {
    val params = List((integer >= 0).option(integer),
                      (fraction >= 0).option(fraction)).mkString(", ")
    "@Didits(" + params + ")"
  }
}

case class CFuture() extends SConstraint {
  def literal = "@Future"
}
object CFuture extends CFuture

case class CMax(value: Long) extends SConstraint {
  def literal = "@Max(" + value + ")"
}

case class CMin(value: Long) extends SConstraint {
  def literal = "@Min(" + value + ")"
}

case class CNotNull() extends SConstraint {
  def literal = "@NotNull"
}
object CNotNull extends CNotNull

case class CNull() extends SConstraint {
  def literal = "@Null"
}
object CNull extends CNull

case class CPast extends SConstraint {
  def literal = "@Past"
}

case class CPattern(value: String) extends SConstraint {
  def literal = "@Pattern(\"" +  value + "\")"
}

case class CSize(min: Long = -1, max: Long = -1) extends SConstraint {
  def literal = {
    val params = List((min >= 0).option(min),
                      (max >= 0).option(max)).mkString(", ")
    "@Size(" + params + ")"
  }
}

// Ext-JS
case class CInclusion(values: List[String]) extends SConstraint {
  def literal = {
    ""
  }
}

case class CExclusion(values: List[String]) extends SConstraint {
  def literal = {
    ""
  }
}
