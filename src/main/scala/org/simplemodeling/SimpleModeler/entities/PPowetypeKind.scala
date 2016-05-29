package org.simplemodeling.SimpleModeler.entities

import org.apache.commons.lang3.StringUtils
import scalaz._, Scalaz._
import org.goldenport.entity._
import org.simplemodeling.dsl.SPowertypeKind
import org.simplemodeling.SimpleModeler.entity._

/*
 * @since   Nov. 26, 2012
 * @version May.  7, 2016
 * @author  ASAMI, Tomoharu
 */
case class PPowertypeKind(name: String, value: \/[String, Int], label: String, model: SPowertypeKind) extends PEnumeration

object PPowertypeKind {
  def create(k: SPowertypeKind) = {
    val value = k.value match {
      case Some(v) => {
        v.parseInt.fold(_ => k.name.left, _.right)
      }
      case None => k.name.left
    }
    val l = k.label.toText
    val label = if (StringUtils.isNotBlank(l)) l
    else k.name
    new PPowertypeKind(k.name, value, label, k)
  }
}











