package org.simplemodeling.SimpleModeler.entities

import scalaz._, Scalaz._
import org.goldenport.entity._
import org.simplemodeling.dsl.SPowertypeKind
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov. 26, 2012
 * @version May.  7, 2016
 * @author  ASAMI, Tomoharu
 */
trait PEnumeration {
  val name: String
  val value: \/[String, Int]
  val label: String

  def sqlValue = {
    value match {
      case -\/(v) => "'" + v + "'"
      case \/-(v) => v.toString
    }
  }

  def sqlLabel = {
    "'" + label + "'"
  }
}
