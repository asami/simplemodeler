package org.simplemodeling.SimpleModeler.entities

import org.apache.commons.lang3.StringUtils
import scalaz._, Scalaz._
import org.goldenport.entity._
import org.simplemodeling.dsl.SPowertypeKind
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov. 26, 2012
 *  version Feb.  6, 2013
 * @version May.  7, 2016
 * @author  ASAMI, Tomoharu
 */
case class PState(name: String, value: \/[String, Int], label: String, model: SMState) extends PEnumeration {
  def lifecycle = model.lifecycle
}

object PState {
  def create(s: SMState) = {
    val a = s.label.toText
    val b = if (StringUtils.isNotBlank(a)) a else s.name
    new PState(s.name, s.value, b, s)
  }
}
