package org.simplemodeling.SimpleModeler.entities

import org.apache.commons.lang3.StringUtils
import scalaz._, Scalaz._
import org.goldenport.entity._
import org.simplemodeling.dsl.SPowertypeKind
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov. 26, 2012
 * @version Nov. 26, 2012
 * @author  ASAMI, Tomoharu
 */
case class PState(name: String, value: Either[String, Int], label: String, model: SMState) extends PEnumeration

object PState {
  def create(s: SMState) = {
    val a = s.label.toText
    val b = if (StringUtils.isNotBlank(a)) a else s.name
    new PState(s.name, s.value, b, s)
  }
}
