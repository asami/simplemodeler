package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.goldenport.entity._
import org.simplemodeling.dsl.SPowertypeKind
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov. 26, 2012
 * @version Nov. 26, 2012
 * @author  ASAMI, Tomoharu
 */
trait PEnumeration {
  val name: String
  val value: Either[String, Int]
  val label: String

  def sqlValue = {
    value match {
      case Left(v) => "'" + v + "'"
      case Right(v) => v.toString
    }
  }

  def sqlLabel = {
    "'" + label + "'"
  }
}
